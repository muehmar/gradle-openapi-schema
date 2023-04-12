package com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo;

import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.ConstructorGeneratorBuilder;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.function.BiFunction;

public class PojoConstructorGenerator {
  private PojoConstructorGenerator() {}

  public static <T extends JavaPojo> Generator<T, PojoSettings> generator() {
    final Generator<T, PojoSettings> method =
        ConstructorGeneratorBuilder.<T, PojoSettings>create()
            .modifiers(PUBLIC)
            .javaClassName(JavaPojo::getClassName)
            .arguments(constructorArguments())
            .content(constructorContent())
            .build()
            .filter(JavaPojo::isNotEnum);
    return JacksonAnnotationGenerator.<T>jsonCreator().filter(JavaPojo::isArray).append(method);
  }

  private static <T extends JavaPojo>
      BiFunction<T, PojoSettings, PList<String>> constructorArguments() {
    return (pojo, pojoSettings) ->
        pojo.getMembersOrEmpty().flatMap(PojoConstructorGenerator::createArguments);
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

  private static <T extends JavaPojo> Generator<T, PojoSettings> constructorContent() {
    return (pojo, settings, writer) -> {
      final PList<String> assignments =
          pojo.getMembersOrEmpty().flatMap(PojoConstructorGenerator::createMemberAssignment);
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
}
