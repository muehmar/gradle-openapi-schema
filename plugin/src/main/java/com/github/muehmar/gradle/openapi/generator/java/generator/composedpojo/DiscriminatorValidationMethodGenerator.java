package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.Generator.ofWriterFunction;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.ValidationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaComposedPojo;
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

  public static Generator<JavaComposedPojo, PojoSettings> generator() {
    final Generator<PojoAndDiscriminator, PojoSettings> annotation =
        ValidationGenerator.assertTrue(
            pojo -> "Not valid against the schema described by the discriminator");
    final MethodGen<PojoAndDiscriminator, PojoSettings> method =
        MethodGenBuilder.<PojoAndDiscriminator, PojoSettings>create()
            .modifiers((pojo, settings) -> settings.getRawGetter().getModifier().asJavaModifiers())
            .noGenericTypes()
            .returnType("boolean")
            .methodName("isValidAgainstTheCorrectSchema")
            .noArguments()
            .content(methodContent())
            .build();

    return Generator.<JavaComposedPojo, PojoSettings>emptyGen()
        .appendOptional(annotation.append(method), PojoAndDiscriminator::fromPojo);
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
    JavaComposedPojo pojo;
    Discriminator discriminator;

    static Optional<PojoAndDiscriminator> fromPojo(JavaComposedPojo pojo) {
      return pojo.getDiscriminator().map(d -> new PojoAndDiscriminator(pojo, d));
    }

    PList<SinglePojoAndDiscriminator> getPojos() {
      return pojo.getJavaPojos().map(pojo -> new SinglePojoAndDiscriminator(pojo, discriminator));
    }
  }

  @Value
  private static class SinglePojoAndDiscriminator {
    JavaPojo pojo;
    Discriminator discriminator;

    String getDiscriminatorValue() {
      return discriminator.getValueForPojoName(pojo.getName());
    }

    Name isValidAgainstMethodName() {
      return CompositionNames.isValidAgainstMethodName(pojo);
    }
  }
}
