package com.github.muehmar.gradle.openapi.generator.java.generator.shared.builder;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.fieldRefs;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import java.util.Optional;
import lombok.Value;

public class DtoSetterGenerator {
  private DtoSetterGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> dtoSetterGenerator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendOptional(dtoSetters(), ParentPojoAndComposedPojos::forAllOfComposition)
        .appendSingleBlankLine()
        .appendOptional(dtoSetters(), ParentPojoAndComposedPojos::forOneOfComposition)
        .appendSingleBlankLine()
        .appendOptional(dtoSetters(), ParentPojoAndComposedPojos::forAnyOfComposition);
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
        .singleArgument(pojo -> String.format("%s dto", pojo.getComposedPojo().getClassName()))
        .content(dtoSetterContent())
        .build();
  }

  private static Generator<ParentPojoAndComposedPojo, PojoSettings> dtoSetterContent() {
    return Generator.<ParentPojoAndComposedPojo, PojoSettings>emptyGen()
        .appendList(
            setSingleNonDiscriminatorMember().append(setSingleDiscriminatorMember()),
            ParentPojoAndComposedPojo::getMembers)
        .append(setAdditionalProperties())
        .append(constant("return this;"));
  }

  private static Generator<PojosAndMember, PojoSettings> setSingleNonDiscriminatorMember() {
    return Generator.<PojosAndMember, PojoSettings>emptyGen()
        .append(
            (member, s, w) ->
                w.println(
                    "%s(dto.%s());",
                    member.prefixedMethodName(s.getBuilderMethodPrefix()),
                    member.getGetterNameWithSuffix(s)))
        .append(fieldRefs(), PojosAndMember::getMember)
        .filter(PojosAndMember::isNotDiscriminatorMember);
  }

  private static Generator<PojosAndMember, PojoSettings> setSingleDiscriminatorMember() {
    return Generator.<PojosAndMember, PojoSettings>emptyGen()
        .append(
            (member, s, w) ->
                w.println(
                    "%s(\"%s\");",
                    member.prefixedMethodName(s.getBuilderMethodPrefix()),
                    member.getDiscriminatorValue()))
        .append(fieldRefs(), PojosAndMember::getMember)
        .filter(PojosAndMember::isDiscriminatorMember);
  }

  private static <B> Generator<ParentPojoAndComposedPojo, B> setAdditionalProperties() {
    return Generator.<ParentPojoAndComposedPojo, B>constant(
            "dto.getAdditionalProperties().forEach(this::addAdditionalProperty);")
        .filter(ppcp -> ppcp.getComposedPojo().getAdditionalProperties().isAllowed());
  }

  @Value
  private static class ParentPojoAndComposedPojos {
    JavaObjectPojo parentPojo;
    NonEmptyList<JavaObjectPojo> composedPojos;

    private static Optional<ParentPojoAndComposedPojos> forAllOfComposition(
        JavaObjectPojo parentPojo) {
      return parentPojo
          .getAllOfComposition()
          .map(
              allOfComposition ->
                  new ParentPojoAndComposedPojos(parentPojo, allOfComposition.getPojos()));
    }

    private static Optional<ParentPojoAndComposedPojos> forOneOfComposition(
        JavaObjectPojo parentPojo) {
      return parentPojo
          .getOneOfComposition()
          .map(
              oneOfComposition ->
                  new ParentPojoAndComposedPojos(parentPojo, oneOfComposition.getPojos()));
    }

    private static Optional<ParentPojoAndComposedPojos> forAnyOfComposition(
        JavaObjectPojo parentPojo) {
      return parentPojo
          .getAnyOfComposition()
          .map(
              anyOfComposition ->
                  new ParentPojoAndComposedPojos(parentPojo, anyOfComposition.getPojos()));
    }

    public PList<ParentPojoAndComposedPojo> getComposedPojos() {
      return composedPojos
          .toPList()
          .map(composedPojo -> new ParentPojoAndComposedPojo(parentPojo, composedPojo));
    }
  }

  @Value
  private static class ParentPojoAndComposedPojo {
    JavaObjectPojo parentPojo;
    JavaObjectPojo composedPojo;

    public JavaIdentifier prefixedClassNameForMethod(String prefix) {
      return composedPojo.prefixedClassNameForMethod(prefix);
    }

    private PList<PojosAndMember> getMembers() {
      return composedPojo
          .getAllMembers()
          .map(member -> new PojosAndMember(parentPojo, composedPojo, member));
    }
  }

  @Value
  private static class PojosAndMember {
    JavaObjectPojo parentPojo;
    JavaObjectPojo composedPojo;
    JavaPojoMember member;

    private JavaIdentifier prefixedMethodName(String prefix) {
      return member.prefixedMethodName(prefix);
    }

    public JavaIdentifier getGetterNameWithSuffix(PojoSettings settings) {
      return member.getGetterNameWithSuffix(settings);
    }

    public String getDiscriminatorValue() {
      return parentPojo
          .getOneOfComposition()
          .flatMap(JavaOneOfComposition::getDiscriminator)
          .map(
              discriminator ->
                  discriminator.getValueForSchemaName(composedPojo.getSchemaName().asName()))
          .orElse("");
    }

    private boolean isDiscriminatorMember() {
      return parentPojo
          .getOneOfComposition()
          .flatMap(JavaOneOfComposition::getDiscriminator)
          .filter(
              discriminator -> discriminator.getPropertyName().equals(member.getName().asName()))
          .isPresent();
    }

    private boolean isNotDiscriminatorMember() {
      return not(isDiscriminatorMember());
    }
  }
}
