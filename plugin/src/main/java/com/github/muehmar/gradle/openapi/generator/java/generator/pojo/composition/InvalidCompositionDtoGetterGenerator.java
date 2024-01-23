package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.DeprecatedMethodGenerator.deprecatedJavaDocAndAnnotationForValidationMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static com.github.muehmar.gradle.openapi.generator.java.model.composition.DiscriminatableJavaComposition.Type.ANY_OF;
import static com.github.muehmar.gradle.openapi.generator.java.model.composition.DiscriminatableJavaComposition.Type.ONE_OF;
import static com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames.Composition.asConversionMethodName;
import static com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames.Composition.getCompositionValidCountMethodName;
import static com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames.Composition.getInvalidCompositionMethodName;
import static com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames.Composition.isValidAgainstTheCorrectSchemaMethodName;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.Generator.newLine;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.SettingsFunctions;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.DiscriminatableJavaComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaDiscriminator;
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
        .appendList(invalidCompositionDtoGetter(), CompositionContainer::fromParentPojo, newLine());
  }

  private static Generator<CompositionContainer, PojoSettings> invalidCompositionDtoGetter() {
    final Generator<CompositionContainer, PojoSettings> method =
        JavaGenerators.<CompositionContainer, PojoSettings>methodGen()
            .modifiers(SettingsFunctions::validationMethodModifiers)
            .noGenericTypes()
            .returnType("Map<String, Object>")
            .methodName(
                container -> getInvalidCompositionMethodName(container.getType()).asString())
            .noArguments()
            .doesNotThrow()
            .content(invalidCompositionDtoGetterContent())
            .build()
            .append(ref(JavaRefs.JAVA_UTIL_MAP))
            .append(ref(JavaRefs.JAVA_UTIL_HASH_MAP));

    return Generator.<CompositionContainer, PojoSettings>emptyGen()
        .append(deprecatedJavaDocAndAnnotationForValidationMethod())
        .append(ValidationAnnotationGenerator.validAnnotation())
        .append(jsonIgnore())
        .append(method)
        .filter(Filters.isValidationEnabled());
  }

  private static Generator<CompositionContainer, PojoSettings>
      invalidCompositionDtoGetterContent() {
    return Generator.<CompositionContainer, PojoSettings>emptyGen()
        .append(constant("final Map<String, Object> dtos = new HashMap<>();"))
        .append(addInvalidSingleResultDtos().filter(CompositionContainer::isSingleResult))
        .append(addInvalidMultiResultDtos().filter(CompositionContainer::isMultiResult))
        .append(constant("return dtos;"));
  }

  private static Generator<CompositionContainer, PojoSettings> addInvalidSingleResultDtos() {
    return Generator.<CompositionContainer, PojoSettings>emptyGen()
        .append(
            (container, s, w) ->
                w.println("if(%s) {", invalidCondition(container.getComposition())))
        .appendOptional(
            singleResultDiscriminatorHandling().indent(1),
            DiscriminatorAndMemberPojo::fromCompositionContainer)
        .appendList(putSingleInvalidDto(), CompositionContainer::getPojos)
        .append(constant("}"));
  }

  private static String invalidCondition(DiscriminatableJavaComposition composition) {
    final DiscriminatableJavaComposition.Type type = composition.getType();
    final String validCountCondition =
        String.format(
            "%s() %s",
            getCompositionValidCountMethodName(type), type.equals(ONE_OF) ? "!= 1" : "== 0");
    final String discriminatorCondition =
        String.format("!%s()", isValidAgainstTheCorrectSchemaMethodName(type));
    return PList.of(
            validCountCondition, composition.hasDiscriminator() ? discriminatorCondition : "")
        .filter(cond -> not(cond.trim().isEmpty()))
        .mkString(" || ");
  }

  private static Generator<NonEmptyList<DiscriminatorAndMemberPojo>, PojoSettings>
      singleResultDiscriminatorHandling() {
    final Generator<DiscriminatorAndMemberPojo, PojoSettings> singleCaseStatement =
        Generator.<DiscriminatorAndMemberPojo, PojoSettings>emptyGen()
            .append((dm, s, w) -> w.println("case \"%s\":", dm.getDiscriminatorStringValue()))
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
                w.tab(1)
                    .println(
                        "switch(%s) {",
                        l.head().discriminator.discriminatorPropertyToStringValue()))
        .appendList(singleCaseStatement, Function.identity())
        .append(constant("}"), 1)
        .append(constant("}"));
  }

  private static Generator<CompositionContainer, PojoSettings> addInvalidMultiResultDtos() {
    return Generator.<CompositionContainer, PojoSettings>emptyGen()
        .append(
            (container, s, w) ->
                w.println(
                    "if(%s() == 0) {", getCompositionValidCountMethodName(container.getType())))
        .appendList(putSingleInvalidDto(), CompositionContainer::getPojos)
        .append(constant("}"));
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
  private static class CompositionContainer {
    JavaObjectPojo parentPojo;
    DiscriminatableJavaComposition composition;

    static PList<CompositionContainer> fromParentPojo(JavaObjectPojo parentPojo) {
      return parentPojo
          .getDiscriminatableCompositions()
          .map(composition -> new CompositionContainer(parentPojo, composition));
    }

    DiscriminatableJavaComposition.Type getType() {
      return composition.getType();
    }

    PList<JavaObjectPojo> getPojos() {
      return composition.getPojos().toPList();
    }

    boolean isSingleResult() {
      final boolean isOneOf = composition.getType().equals(ONE_OF);
      final boolean isAnyOfWithDiscriminator =
          composition.getType().equals(ANY_OF) && composition.getDiscriminator().isPresent();
      return isOneOf || isAnyOfWithDiscriminator;
    }

    boolean isMultiResult() {
      return not(isSingleResult());
    }
  }

  @Value
  private static class DiscriminatorAndMemberPojo {
    JavaDiscriminator discriminator;
    JavaObjectPojo memberPojo;
    DiscriminatableJavaComposition.Type compositionType;

    public static Optional<NonEmptyList<DiscriminatorAndMemberPojo>> fromCompositionContainer(
        CompositionContainer container) {
      return container
          .getComposition()
          .getDiscriminator()
          .map(discriminator -> fromDiscriminator(container.getComposition(), discriminator));
    }

    private static NonEmptyList<DiscriminatorAndMemberPojo> fromDiscriminator(
        DiscriminatableJavaComposition composition, JavaDiscriminator discriminator) {
      return composition
          .getPojos()
          .map(
              memberPojo ->
                  new DiscriminatorAndMemberPojo(discriminator, memberPojo, composition.getType()));
    }

    public String getDiscriminatorStringValue() {
      return discriminator.getStringValueForSchemaName(
          memberPojo.getSchemaName().getOriginalName());
    }
  }
}
