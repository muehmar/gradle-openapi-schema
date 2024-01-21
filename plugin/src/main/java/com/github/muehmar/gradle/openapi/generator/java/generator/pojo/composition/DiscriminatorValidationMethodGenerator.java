package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition;

import static com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames.Composition.isValidAgainstTheCorrectSchemaMethodName;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.Generator.ofWriterFunction;

import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.DeprecatedMethodGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.SettingsFunctions;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.DiscriminatableJavaComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaDiscriminator;
import com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import java.util.Optional;
import lombok.Value;

public class DiscriminatorValidationMethodGenerator {
  private DiscriminatorValidationMethodGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> discriminatorValidationMethodGenerator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendOptional(completeMethodGenerator(), PojoAndDiscriminator::fromOneOfComposition)
        .appendSingleBlankLine()
        .appendOptional(completeMethodGenerator(), PojoAndDiscriminator::fromAnyOfComposition);
  }

  private static Generator<PojoAndDiscriminator, PojoSettings> completeMethodGenerator() {
    final Generator<PojoAndDiscriminator, PojoSettings> annotation =
        ValidationAnnotationGenerator.assertTrue(
            pojo ->
                String.format(
                    "Not valid against the schema described by the %s-discriminator",
                    pojo.getCompositionType().getName().startLowerCase()));
    final MethodGen<PojoAndDiscriminator, PojoSettings> method =
        MethodGenBuilder.<PojoAndDiscriminator, PojoSettings>create()
            .modifiers(SettingsFunctions::validationMethodModifiers)
            .noGenericTypes()
            .returnType("boolean")
            .methodName(
                p -> isValidAgainstTheCorrectSchemaMethodName(p.getCompositionType()).asString())
            .noArguments()
            .doesNotThrow()
            .content(methodContent())
            .build();

    return DeprecatedMethodGenerator
        .<PojoAndDiscriminator>deprecatedJavaDocAndAnnotationForValidationMethod()
        .append(annotation)
        .append(JacksonAnnotationGenerator.jsonIgnore())
        .append(method);
  }

  private static Generator<PojoAndDiscriminator, PojoSettings> methodContent() {
    return Generator.<PojoAndDiscriminator, PojoSettings>of(
            (p, s, w) -> w.println("if (%s == null) {", p.getDiscriminator().getPropertyName()))
        .append(ofWriterFunction(w -> w.tab(1).println("return false;")))
        .append(constant("}"))
        .append(
            (p, s, w) ->
                w.println(
                    "switch(%s) {", p.getDiscriminator().discriminatorPropertyToStringValue()))
        .appendList(pojoCaseGenerator().indent(1), PojoAndDiscriminator::getPojos)
        .append(constant("}"))
        .append(constant("return false;"));
  }

  private static Generator<SinglePojoAndDiscriminator, PojoSettings> pojoCaseGenerator() {
    return Generator.of(
        (p, s, w) ->
            w.println(
                "case \"%s\": return %s();",
                p.getDiscriminatorValue(), p.isValidAgainstMethodName()));
  }

  @Value
  private static class PojoAndDiscriminator {
    JavaObjectPojo pojo;
    NonEmptyList<JavaObjectPojo> memberPojos;
    JavaDiscriminator discriminator;
    DiscriminatableJavaComposition.Type compositionType;

    static Optional<PojoAndDiscriminator> fromOneOfComposition(JavaObjectPojo pojo) {
      return fromComposition(pojo, pojo.getOneOfComposition());
    }

    static Optional<PojoAndDiscriminator> fromAnyOfComposition(JavaObjectPojo pojo) {
      return fromComposition(pojo, pojo.getAnyOfComposition());
    }

    static Optional<PojoAndDiscriminator> fromComposition(
        JavaObjectPojo pojo, Optional<? extends DiscriminatableJavaComposition> composition) {
      return composition.flatMap(
          comp ->
              comp.getDiscriminator()
                  .map(d -> new PojoAndDiscriminator(pojo, comp.getPojos(), d, comp.getType())));
    }

    NonEmptyList<SinglePojoAndDiscriminator> getPojos() {
      return memberPojos.map(p -> new SinglePojoAndDiscriminator(p, discriminator));
    }
  }

  @Value
  private static class SinglePojoAndDiscriminator {
    JavaPojo pojo;
    JavaDiscriminator discriminator;

    String getDiscriminatorValue() {
      return discriminator.getStringValueForSchemaName(pojo.getSchemaName().getOriginalName());
    }

    Name isValidAgainstMethodName() {
      return MethodNames.Composition.isValidAgainstMethodName(pojo);
    }
  }
}
