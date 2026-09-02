package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.exception.OpenApiGeneratorException;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.AccessorProfile;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ConversionGenerationMode;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ToApiTypeConversionRenderer;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaDiscriminator;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen.Argument;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;
import lombok.Value;

public class DtoSetterGenerator {
  private DtoSetterGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> dtoSetterGenerator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(dtoSetters(), ParentPojoAndComposedPojos::fromParentPojo, newLine());
  }

  private static Generator<ParentPojoAndComposedPojos, PojoSettings> dtoSetters() {
    return Generator.<ParentPojoAndComposedPojos, PojoSettings>emptyGen()
        .appendList(singleDtoSetter(), ParentPojoAndComposedPojos::getComposedPojos, newLine());
  }

  private static Generator<ParentPojoAndComposedPojo, PojoSettings> singleDtoSetter() {
    return MethodGenBuilder.<ParentPojoAndComposedPojo, PojoSettings>create()
        .modifiers(PRIVATE)
        .noGenericTypes()
        .returnType("Builder")
        .methodName(
            (pojo, settings) ->
                pojo.prefixedClassNameForMethod(settings.getBuilderMethodPrefix()).asString())
        .singleArgument(
            pojo -> new Argument(pojo.getComposedPojo().getClassName().asString(), "dto"))
        .doesNotThrow()
        .content(dtoSetterContent())
        .build();
  }

  private static Generator<ParentPojoAndComposedPojo, PojoSettings> dtoSetterContent() {
    final Generator<PojosAndMember, PojoSettings> singleMemberSetter =
        setSingleNonDiscriminatorAndNonNullableContainerValueMember()
            .append(setSingleNullableContainerValueMember())
            .append(setSingleDiscriminatorMember());
    return Generator.<ParentPojoAndComposedPojo, PojoSettings>emptyGen()
        .appendList(singleMemberSetter, ParentPojoAndComposedPojo::getMembers)
        .append(setNullableAdditionalProperties())
        .append(setNotNullableAdditionalProperties())
        .append(constant("return this;"));
  }

  private static Generator<PojosAndMember, PojoSettings>
      setSingleNonDiscriminatorAndNonNullableContainerValueMember() {
    return Generator.<PojosAndMember, PojoSettings>emptyGen()
        .append(
            (member, s, w) ->
                w.println(
                    "%s%s(dto.%s());",
                    member.setterCondition(),
                    member.prefixedMethodName(s.getBuilderMethodPrefix()),
                    member.getGetterNameWithSuffix(s)))
        .filter(PojosAndMember::isNotDiscriminatorAndNotNullableContainerValueMember);
  }

  private static Generator<PojosAndMember, PojoSettings> setSingleNullableContainerValueMember() {
    return Generator.<PojosAndMember, PojoSettings>emptyGen()
        .append(
            (member, s, w) ->
                w.println(
                    "%s%s_(dto.%s());",
                    member.setterCondition(),
                    member.prefixedMethodName(s.getBuilderMethodPrefix()),
                    member.getGetterNameWithSuffix(s)))
        .filter(PojosAndMember::isNullableContainerValueType);
  }

  private static Generator<PojosAndMember, PojoSettings> setSingleDiscriminatorMember() {
    return Generator.<PojosAndMember, PojoSettings>emptyGen()
        .append(
            (member, s, w) -> {
              final Writer discriminatorValue = member.getDiscriminatorValue();
              return w.println(
                      "%s(%s);",
                      member.prefixedMethodName(s.getBuilderMethodPrefix()),
                      discriminatorValue.asString())
                  .refs(discriminatorValue.getRefs());
            })
        .filter(PojosAndMember::isDiscriminatorMember);
  }

  private static <B> Generator<ParentPojoAndComposedPojo, B> setNotNullableAdditionalProperties() {
    return Generator.<ParentPojoAndComposedPojo, B>constant("dto.getAdditionalProperties()")
        .append(
            constant(".forEach(prop -> addAdditionalProperty(prop.getName(), prop.getValue()));"),
            2)
        .filter(ParentPojoAndComposedPojo::hasNotNullableAdditionalProperties)
        .filter(ppcp -> ppcp.getComposedPojo().getAdditionalProperties().isAllowed());
  }

  private static <B> Generator<ParentPojoAndComposedPojo, B> setNullableAdditionalProperties() {
    return Generator.<ParentPojoAndComposedPojo, B>constant("dto.getAdditionalProperties()")
        .append(
            constant(
                ".forEach(prop -> addAdditionalProperty(prop.getName(), prop.getValue().orElse(null)));"),
            2)
        .filter(ParentPojoAndComposedPojo::hasNullableAdditionalProperties)
        .filter(ppcp -> ppcp.getComposedPojo().getAdditionalProperties().isAllowed());
  }

  @Value
  private static class ParentPojoAndComposedPojos {
    JavaObjectPojo parentPojo;
    Optional<JavaDiscriminator> discriminator;
    NonEmptyList<JavaObjectPojo> composedPojos;

    private static PList<ParentPojoAndComposedPojos> fromParentPojo(JavaObjectPojo parentPojo) {
      final PList<ParentPojoAndComposedPojos> mappedAllOf =
          PList.fromOptional(
              parentPojo
                  .getAllOfComposition()
                  .map(
                      composition ->
                          new ParentPojoAndComposedPojos(
                              parentPojo, Optional.empty(), composition.getPojos())));
      final PList<ParentPojoAndComposedPojos> mappedOneOfAndAnyOf =
          parentPojo
              .getDiscriminatableCompositions()
              .map(
                  composition ->
                      new ParentPojoAndComposedPojos(
                          parentPojo, composition.getDiscriminator(), composition.getPojos()));
      return mappedAllOf.concat(mappedOneOfAndAnyOf);
    }

    public PList<ParentPojoAndComposedPojo> getComposedPojos() {
      return composedPojos
          .toPList()
          .map(
              composedPojo ->
                  new ParentPojoAndComposedPojo(parentPojo, discriminator, composedPojo));
    }
  }

  @Value
  private static class ParentPojoAndComposedPojo {
    JavaObjectPojo parentPojo;
    Optional<JavaDiscriminator> discriminator;
    JavaObjectPojo composedPojo;

    public JavaName prefixedClassNameForMethod(String prefix) {
      return composedPojo.prefixedClassNameForMethod(prefix);
    }

    private PList<PojosAndMember> getMembers() {
      return composedPojo
          .getAllMembers()
          .map(member -> new PojosAndMember(parentPojo, discriminator, composedPojo, member));
    }

    private boolean hasNullableAdditionalProperties() {
      return composedPojo.getAdditionalProperties().getType().getNullability().isNullable();
    }

    private boolean hasNotNullableAdditionalProperties() {
      return not(hasNullableAdditionalProperties());
    }
  }

  @Value
  private static class PojosAndMember {
    JavaObjectPojo parentPojo;
    Optional<JavaDiscriminator> discriminator;
    JavaObjectPojo composedPojo;
    JavaPojoMember member;

    private JavaName prefixedMethodName(String prefix) {
      return member.prefixedMethodName(prefix);
    }

    public JavaName getGetterNameWithSuffix(PojoSettings settings) {
      return member.getGetterNameWithSuffix(settings);
    }

    /**
     * The expression for the fixed discriminator value of the composed pojo. Returned as {@link
     * Writer} as the conversion of an api-typed member registers the refs of the classes used by
     * the conversion, which are needed for the imports of the generated dto.
     */
    Writer getDiscriminatorValue() {
      final Name schemaName = composedPojo.getSchemaName().getOriginalName();
      return discriminator
          .map(
              d ->
                  d.getValueForSchemaName(
                      schemaName,
                      strValue -> javaWriter().print("\"%s\"", strValue),
                      enumName -> enumDiscriminatorValue(d, schemaName)))
          .orElseGet(Writer::javaWriter);
    }

    private Writer enumDiscriminatorValue(JavaDiscriminator d, Name schemaName) {
      final String stringValue = String.format("\"%s\"", d.getStringValueForSchemaName(schemaName));
      // An enum property always has an api type (the enum itself or a mapped custom type with a
      // conversion), as a mapping without conversion for a discriminator property is rejected when
      // the pojo is created.
      return member
          .getJavaType()
          .getApiType()
          .map(
              apiType ->
                  ToApiTypeConversionRenderer.toApiTypeConversion(
                      apiType, stringValue, ConversionGenerationMode.NO_NULL_CHECK))
          .orElseThrow(
              () ->
                  new OpenApiGeneratorException(
                      String.format(
                          "Unable to determine the discriminator value for the property '%s' of schema '%s'.",
                          member.getName(), schemaName)));
    }

    String setterCondition() {
      return AccessorProfile.of(member).hasReadableFlagAccessor()
          ? String.format("if (dto.%s()) ", member.getFlagGetterName())
          : "";
    }

    private boolean isNotDiscriminatorAndNotNullableContainerValueMember() {
      return not(isDiscriminatorMember()) && not(isNullableContainerValueType());
    }

    private boolean isNullableContainerValueType() {
      return not(isDiscriminatorMember()) && member.getJavaType().isNullableContainerValueType();
    }

    private boolean isDiscriminatorMember() {
      return discriminator
          .filter(discriminator -> discriminator.getPropertyName().equals(member.getName()))
          .isPresent();
    }
  }
}
