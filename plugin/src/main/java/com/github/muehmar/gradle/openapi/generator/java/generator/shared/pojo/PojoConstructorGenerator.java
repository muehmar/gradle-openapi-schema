package com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo;

import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.ConstructorGeneratorBuilder;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.util.Optional;
import java.util.function.BiFunction;
import lombok.Value;

public class PojoConstructorGenerator {
  private PojoConstructorGenerator() {}

  public static Generator<ConstructorContent, PojoSettings> generator() {
    final Generator<ConstructorContent, PojoSettings> method =
        ConstructorGeneratorBuilder.<ConstructorContent, PojoSettings>create()
            .modifiers(PUBLIC)
            .javaClassName(ConstructorContent::getClassName)
            .arguments(constructorArguments())
            .content(constructorContent())
            .build()
            .append(additionalPropertiesImports());
    return JacksonAnnotationGenerator.<ConstructorContent>jsonCreator()
        .filter(ConstructorContent::isArray)
        .append(method);
  }

  private static Generator<ConstructorContent, PojoSettings> additionalPropertiesImports() {
    final Generator<JavaAdditionalProperties, PojoSettings> imports =
        Generator.<JavaType, PojoSettings>emptyGen()
            .append(w -> w.ref(JavaRefs.JAVA_UTIL_MAP))
            .append(w -> w.ref(JavaRefs.JAVA_UTIL_COLLECTIONS))
            .append(RefsGenerator.javaTypeRefs())
            .contraMap(JavaAdditionalProperties::getType);
    return Generator.<ConstructorContent, PojoSettings>emptyGen()
        .appendOptional(imports, ConstructorContent::getAdditionalProperties);
  }

  private static BiFunction<ConstructorContent, PojoSettings, PList<String>>
      constructorArguments() {
    return (pojo, pojoSettings) ->
        pojo.getMembers()
            .flatMap(PojoConstructorGenerator::createArguments)
            .concat(createAdditionalPropertyArgument(pojo));
  }

  private static PList<String> createArguments(JavaPojoMember member) {
    final String memberArgument =
        String.format(
            "%s %s", member.getJavaType().getFullClassName(), member.getNameAsIdentifier());
    if (member.isRequired() && member.isNullable()) {
      final String requiredPresentFlag = String.format("boolean %s", member.getIsPresentFlagName());
      return PList.of(memberArgument, requiredPresentFlag);
    } else if (member.isOptional() && member.isNullable()) {
      final String optionalNullFlag = String.format("boolean %s", member.getIsNullFlagName());
      return PList.of(memberArgument, optionalNullFlag);
    } else {
      return PList.single(memberArgument);
    }
  }

  private static PList<String> createAdditionalPropertyArgument(ConstructorContent content) {
    return PList.fromOptional(content.getAdditionalProperties())
        .map(
            props ->
                String.format(
                    "Map<String, %s> %s",
                    props.getType().getFullClassName(),
                    JavaAdditionalProperties.getPropertyName()));
  }

  private static Generator<ConstructorContent, PojoSettings> constructorContent() {
    return (content, settings, writer) -> {
      final PList<String> assignments =
          content
              .getMembers()
              .flatMap(PojoConstructorGenerator::createMemberAssignment)
              .concat(createAdditionalPropertiesAssignment(content));
      return assignments.foldLeft(writer, Writer::println);
    };
  }

  private static PList<String> createMemberAssignment(JavaPojoMember member) {
    final JavaIdentifier memberName = member.getNameAsIdentifier();
    final String memberAssignmentFormat = "this.%s = %s;";
    final String memberAssignment = String.format(memberAssignmentFormat, memberName, memberName);
    if (member.isRequiredAndNullable()) {
      final String requiredPresentFlagAssignment =
          String.format(
              memberAssignmentFormat, member.getIsPresentFlagName(), member.getIsPresentFlagName());
      return PList.of(memberAssignment, requiredPresentFlagAssignment);
    } else if (member.isOptionalAndNullable()) {
      final String optionalNullFlagAssignment =
          String.format(
              memberAssignmentFormat, member.getIsNullFlagName(), member.getIsNullFlagName());
      return PList.of(memberAssignment, optionalNullFlagAssignment);
    } else {
      return PList.single(memberAssignment);
    }
  }

  private static PList<String> createAdditionalPropertiesAssignment(ConstructorContent content) {
    return PList.fromOptional(content.getAdditionalProperties())
        .map(
            props ->
                String.format(
                    "this.%s = Collections.unmodifiableMap(%s);",
                    JavaAdditionalProperties.getPropertyName(),
                    JavaAdditionalProperties.getPropertyName()));
  }

  @Value
  @PojoBuilder(builderName = "ConstructorContentBuilder")
  public static class ConstructorContent {
    boolean isArray;
    JavaIdentifier className;
    PList<JavaPojoMember> members;
    Optional<JavaAdditionalProperties> additionalProperties;
  }
}
