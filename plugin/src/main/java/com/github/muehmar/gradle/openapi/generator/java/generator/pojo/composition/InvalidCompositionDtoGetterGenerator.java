package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.JavaDocGenerators.deprecatedValidationMethodJavaDoc;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames.Composition.AnyOf.getAnyOfValidCountMethodName;
import static com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames.Composition.CompositionType.ANY_OF;
import static com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames.Composition.CompositionType.ONE_OF;
import static com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames.Composition.OneOf.getOneOfValidCountMethodName;
import static com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames.Composition.asConversionMethodName;
import static com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames.Composition.getInvalidCompositionMethodName;
import static io.github.muehmar.codegenerator.Generator.constant;

import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.AnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.SettingsFunctions;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaDiscriminator;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import java.util.Optional;
import java.util.function.Function;
import lombok.Value;

public class InvalidCompositionDtoGetterGenerator {
  private InvalidCompositionDtoGetterGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> invalidCompositionDtoGetterGenerator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(invalidCompositionDtoGetter(ONE_OF))
        .appendSingleBlankLine()
        .append(invalidCompositionDtoGetter(ANY_OF));
  }

  private static Generator<JavaObjectPojo, PojoSettings> invalidCompositionDtoGetter(
      MethodNames.Composition.CompositionType type) {
    final Generator<JavaObjectPojo, PojoSettings> method =
        JavaGenerators.<JavaObjectPojo, PojoSettings>methodGen()
            .modifiers(SettingsFunctions::validationMethodModifiers)
            .noGenericTypes()
            .returnType("Map<String, Object>")
            .methodName(getInvalidCompositionMethodName(type).asString())
            .noArguments()
            .doesNotThrow()
            .content(invalidCompositionDtoGetterContent(type))
            .build()
            .append(ref(JavaRefs.JAVA_UTIL_MAP))
            .append(ref(JavaRefs.JAVA_UTIL_HASH_MAP));

    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(deprecatedValidationMethodJavaDoc())
        .append(ValidationAnnotationGenerator.validAnnotation())
        .append(jsonIgnore())
        .append(AnnotationGenerator.deprecatedAnnotationForValidationMethod())
        .append(method)
        .filter(p -> createInvalidCompositionDtoGetter(type, p))
        .filter(Filters.isValidationEnabled());
  }

  private static boolean createInvalidCompositionDtoGetter(
      MethodNames.Composition.CompositionType type, JavaObjectPojo p) {
    final boolean createForOneOf = p.getOneOfComposition().isPresent() && type.equals(ONE_OF);
    final boolean createForAnyOf = p.getAnyOfComposition().isPresent() && type.equals(ANY_OF);
    return createForOneOf || createForAnyOf;
  }

  private static Generator<JavaObjectPojo, PojoSettings> invalidCompositionDtoGetterContent(
      MethodNames.Composition.CompositionType type) {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(constant("final Map<String, Object> dtos = new HashMap<>();"))
        .append(addInvalidOneOfDtos().filter(ignore -> type.equals(ONE_OF)))
        .append(addInvalidAnyOfDtos().filter(ignore -> type.equals(ANY_OF)))
        .append(constant("return dtos;"));
  }

  private static Generator<JavaObjectPojo, PojoSettings> addInvalidOneOfDtos() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(w -> w.println("if(%s() != 1) {", getOneOfValidCountMethodName()))
        .appendOptional(
            oneOfDiscriminatorHandling().indent(1), DiscriminatorAndMemberPojo::fromParentPojo)
        .appendList(putSingleInvalidDto(), JavaObjectPojo::getOneOfPojos)
        .append(constant("}"))
        .filter(pojo -> pojo.getOneOfComposition().isPresent());
  }

  private static Generator<NonEmptyList<DiscriminatorAndMemberPojo>, PojoSettings>
      oneOfDiscriminatorHandling() {
    final Generator<DiscriminatorAndMemberPojo, PojoSettings> singleCaseStatement =
        Generator.<DiscriminatorAndMemberPojo, PojoSettings>emptyGen()
            .append(
                (dm, s, w) ->
                    w.println("case \"%s\":", dm.getMemberPojo().getSchemaName().getOriginalName()))
            .append(
                (dm, s, w) ->
                    w.tab(1)
                        .println(
                            "dtos.put(\"%s\", %s());",
                            dm.getMemberPojo().getSchemaName().getOriginalName(),
                            asConversionMethodName(dm.getMemberPojo())))
            .append(constant("return dtos;"), 1)
            .indent(2);
    return Generator.<NonEmptyList<DiscriminatorAndMemberPojo>, PojoSettings>emptyGen()
        .append(
            (l, s, w) ->
                w.println("if(%s != null) {", l.head().getDiscriminator().getPropertyName()))
        .append(
            (l, s, w) ->
                w.tab(1).println("switch(%s) {", l.head().getDiscriminator().getPropertyName()))
        .appendList(singleCaseStatement, Function.identity())
        .append(constant("}"), 1)
        .append(constant("}"));
  }

  private static Generator<JavaObjectPojo, PojoSettings> addInvalidAnyOfDtos() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(constant("if(%s() == 0) {", getAnyOfValidCountMethodName()))
        .appendList(putSingleInvalidDto(), JavaObjectPojo::getAnyOfPojos)
        .append(constant("}"))
        .filter(pojo -> pojo.getAnyOfComposition().isPresent());
  }

  private static Generator<JavaObjectPojo, PojoSettings> putSingleInvalidDto() {
    return Generator.<JavaObjectPojo, PojoSettings>of(
            (p, s, w) ->
                w.println(
                    "dtos.put(\"%s\", %s());",
                    p.getSchemaName().getOriginalName(), asConversionMethodName(p)))
        .indent(1);
  }

  @Value
  private static class DiscriminatorAndMemberPojo {
    JavaDiscriminator discriminator;
    JavaObjectPojo memberPojo;

    public static Optional<NonEmptyList<DiscriminatorAndMemberPojo>> fromParentPojo(
        JavaObjectPojo pojo) {
      return pojo.getOneOfComposition().flatMap(DiscriminatorAndMemberPojo::fromComposition);
    }

    private static Optional<NonEmptyList<DiscriminatorAndMemberPojo>> fromComposition(
        JavaOneOfComposition composition) {
      return composition
          .getDiscriminator()
          .map(discriminator -> fromDiscriminator(composition, discriminator));
    }

    private static NonEmptyList<DiscriminatorAndMemberPojo> fromDiscriminator(
        JavaOneOfComposition composition, JavaDiscriminator discriminator) {
      return composition
          .getPojos()
          .map(memberPojo -> new DiscriminatorAndMemberPojo(discriminator, memberPojo));
    }
  }
}
