package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition;

import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.constraints.PropertyCount;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import lombok.Value;

public class BasicValidationMethodGenerator {
  private BasicValidationMethodGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> basicValidationMethodGenerator() {
    return JavaGenerators.<JavaObjectPojo, PojoSettings>methodGen()
        .modifiers()
        .noGenericTypes()
        .returnType("boolean")
        .methodName("validateBasic")
        .noArguments()
        .content(basicValidationMethodContent())
        .build();
  }

  private static Generator<JavaObjectPojo, PojoSettings> basicValidationMethodContent() {
    return Generator.<JavaObjectPojo, PojoSettings>constant("return")
        .append(
            singleCondition(basicValidationMethodContentWithRequiredMembers(), " &&")
                .append(singleCondition(methodContentOneOfCondition(), " &&"))
                .append(singleCondition(methodContentOneOfDiscriminatorCondition(), " &&"))
                .append(singleCondition(methodContentAnyOfCondition(), " &&"))
                .append(singleCondition(additionalPropertiesTypeCondition(), " &&"))
                .append(singleCondition(minPropertyCountCondition(), " &&"))
                .append(singleCondition(maxPropertyCountCondition(), ";")),
            1);
  }

  private static Generator<JavaObjectPojo, PojoSettings>
      basicValidationMethodContentWithRequiredMembers() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(
            singlePojoMemberCondition(),
            pojo -> pojo.getMembers().map(member -> new JavaPojoAndMember(pojo, member)))
        .filter(BasicValidationMethodGenerator::hasRequiredInMembers)
        .filter(BasicValidationMethodGenerator::hasRequiredInMembers);
  }

  private static Generator<JavaPojoAndMember, PojoSettings> singlePojoMemberCondition() {
    final Generator<JavaPojoAndMember, PojoSettings> requiredNotNullableGen =
        (pm, s, w) -> w.println("%s != null &&", pm.member.getNameAsIdentifier());
    final Generator<JavaPojoAndMember, PojoSettings> requiredNullableGen =
        (pm, s, w) -> w.println("%s &&", pm.member.getIsPresentFlagName());
    return Generator.<JavaPojoAndMember, PojoSettings>emptyGen()
        .appendConditionally(requiredNotNullableGen, JavaPojoAndMember::isRequiredAndNotNullable)
        .appendConditionally(requiredNullableGen, JavaPojoAndMember::isRequiredAndNullable);
  }

  private static Generator<JavaObjectPojo, PojoSettings> methodContentOneOfCondition() {
    return Generator.<JavaObjectPojo, PojoSettings>constant("getOneOfValidCount() == 1 &&")
        .filter(JavaObjectPojo::hasOneOfComposition);
  }

  private static Generator<JavaObjectPojo, PojoSettings>
      methodContentOneOfDiscriminatorCondition() {
    return Generator.<JavaObjectPojo, PojoSettings>constant("isValidAgainstTheCorrectSchema() &&")
        .filter(BasicValidationMethodGenerator::hasOneOfDiscriminator);
  }

  private static Generator<JavaObjectPojo, PojoSettings> methodContentAnyOfCondition() {
    return Generator.<JavaObjectPojo, PojoSettings>constant("getAnyOfValidCount() >= 1 &&")
        .filter(JavaObjectPojo::hasAnyOfComposition);
  }

  private static Generator<JavaObjectPojo, PojoSettings> additionalPropertiesTypeCondition() {
    return Generator.<JavaObjectPojo, PojoSettings>constant(
            "isAllAdditionalPropertiesHaveCorrectType() &&")
        .filter(pojo -> pojo.getAdditionalProperties().isNotValueAnyType());
  }

  private static Generator<JavaObjectPojo, PojoSettings> minPropertyCountCondition() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(
            (p, s, w) ->
                w.println(
                    "%d <= getPropertyCount() &&",
                    p.getConstraints()
                        .getPropertyCount()
                        .flatMap(PropertyCount::getMinProperties)
                        .orElse(0)))
        .filter(
            pojo ->
                pojo.getConstraints()
                    .getPropertyCount()
                    .flatMap(PropertyCount::getMinProperties)
                    .isPresent());
  }

  private static Generator<JavaObjectPojo, PojoSettings> maxPropertyCountCondition() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(
            (p, s, w) ->
                w.println(
                    "getPropertyCount() <= %d;",
                    p.getConstraints()
                        .getPropertyCount()
                        .flatMap(PropertyCount::getMaxProperties)
                        .orElse(0)))
        .filter(
            pojo ->
                pojo.getConstraints()
                    .getPropertyCount()
                    .flatMap(PropertyCount::getMaxProperties)
                    .isPresent());
  }

  private static boolean hasRequiredInMembers(JavaObjectPojo pojo) {
    return pojo.getMembers().exists(JavaPojoMember::isRequired);
  }

  private static boolean hasOneOfDiscriminator(JavaObjectPojo pojo) {
    return pojo.getOneOfComposition().flatMap(JavaOneOfComposition::getDiscriminator).isPresent();
  }

  private static Generator<JavaObjectPojo, PojoSettings> singleCondition(
      Generator<JavaObjectPojo, PojoSettings> generator, String suffix) {
    return (p, s, w) ->
        generator.generate(p, s, javaWriter()).asString().isEmpty()
            ? w.println("true%s", suffix)
            : generator.generate(p, s, w);
  }

  @Value
  private static class JavaPojoAndMember {
    JavaObjectPojo pojo;
    JavaPojoMember member;

    boolean isRequiredAndNotNullable() {
      return member.isRequiredAndNotNullable();
    }

    public boolean isRequiredAndNullable() {
      return member.isRequiredAndNullable();
    }
  }
}
