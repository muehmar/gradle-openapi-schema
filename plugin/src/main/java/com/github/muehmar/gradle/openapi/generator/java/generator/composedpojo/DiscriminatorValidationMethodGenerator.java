package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.Generator.ofWriterFunction;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.AnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.JavaDocGenerators;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.SettingsFunctions;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.ValidationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.model.Discriminator;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import java.util.Optional;
import lombok.Value;

public class DiscriminatorValidationMethodGenerator {
  private DiscriminatorValidationMethodGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> discriminatorValidationMethodGenerator() {
    final Generator<PojoAndDiscriminator, PojoSettings> annotation =
        ValidationGenerator.assertTrue(
            pojo -> "Not valid against the schema described by the discriminator");
    final MethodGen<PojoAndDiscriminator, PojoSettings> method =
        MethodGenBuilder.<PojoAndDiscriminator, PojoSettings>create()
            .modifiers(SettingsFunctions::validationMethodModifiers)
            .noGenericTypes()
            .returnType("boolean")
            .methodName("isValidAgainstTheCorrectSchema")
            .noArguments()
            .content(methodContent())
            .build();

    final Generator<PojoAndDiscriminator, PojoSettings> completeMethodGen =
        JavaDocGenerators.<PojoAndDiscriminator>deprecatedValidationMethodJavaDoc()
            .append(annotation)
            .append(AnnotationGenerator.deprecatedValidationMethod())
            .append(JacksonAnnotationGenerator.jsonIgnore())
            .append(method);

    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendOptional(completeMethodGen, PojoAndDiscriminator::fromPojo);
  }

  private static Generator<PojoAndDiscriminator, PojoSettings> methodContent() {
    return Generator.<PojoAndDiscriminator, PojoSettings>of(
            (p, s, w) -> w.println("if (%s == null) {", p.getDiscriminator().getPropertyName()))
        .append(ofWriterFunction(w -> w.tab(1).println("return false;")))
        .append(constant("}"))
        .append((p, s, w) -> w.println("switch(%s) {", p.getDiscriminator().getPropertyName()))
        .appendList(gen().indent(1), PojoAndDiscriminator::getPojos)
        .append(constant("}"))
        .append(constant("return false;"));
  }

  private static Generator<SinglePojoAndDiscriminator, PojoSettings> gen() {
    return Generator.of(
        (p, s, w) ->
            w.println(
                "case \"%s\": return %s();",
                p.getDiscriminatorValue(), p.isValidAgainstMethodName()));
  }

  @Value
  private static class PojoAndDiscriminator {
    JavaObjectPojo pojo;
    PList<JavaObjectPojo> memberPojos;
    Discriminator discriminator;

    static Optional<PojoAndDiscriminator> fromPojo(JavaObjectPojo pojo) {
      return pojo.getOneOfComposition()
          .flatMap(
              comp ->
                  comp.getDiscriminator()
                      .map(d -> new PojoAndDiscriminator(pojo, comp.getPojos(), d)));
    }

    PList<SinglePojoAndDiscriminator> getPojos() {
      return memberPojos.map(p -> new SinglePojoAndDiscriminator(p, discriminator));
    }
  }

  @Value
  private static class SinglePojoAndDiscriminator {
    JavaPojo pojo;
    Discriminator discriminator;

    String getDiscriminatorValue() {
      return discriminator.getValueForSchemaName(pojo.getSchemaName().asName());
    }

    Name isValidAgainstMethodName() {
      return CompositionNames.isValidAgainstMethodName(pojo);
    }
  }
}
