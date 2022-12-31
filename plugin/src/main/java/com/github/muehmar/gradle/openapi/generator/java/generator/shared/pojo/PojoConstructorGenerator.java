package com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo;

import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.ConstructorGeneratorBuilder;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.function.BiFunction;

public class PojoConstructorGenerator {
  private PojoConstructorGenerator() {}

  public static Generator<JavaPojo, PojoSettings> generator() {
    return ConstructorGeneratorBuilder.<JavaPojo, PojoSettings>create()
        .modifiers(PUBLIC)
        .pojoName(JavaPojo::getName)
        .arguments(constructorArguments())
        .content(constructorContent())
        .build()
        .filter(JavaPojo::isNotEnum);
  }

  private static BiFunction<JavaPojo, PojoSettings, PList<String>> constructorArguments() {
    return (pojo, pojoSettings) ->
        pojo.getMembersOrEmpty().flatMap(PojoConstructorGenerator::createArguments);
  }

  private static PList<String> createArguments(JavaPojoMember member) {
    final String memberArgument =
        String.format("%s %s", member.getJavaType().getFullClassName(), member.getName());
    if (member.isRequired() && member.isNullable()) {
      final String requiredPresentFlag =
          String.format("boolean is%sPresent", member.getName().startUpperCase());
      return PList.of(memberArgument, requiredPresentFlag);
    } else if (member.isOptional() && member.isNullable()) {
      final String optionalNullFlag =
          String.format("boolean is%sNull", member.getName().startUpperCase());
      return PList.of(memberArgument, optionalNullFlag);
    } else {
      return PList.single(memberArgument);
    }
  }

  private static Generator<JavaPojo, PojoSettings> constructorContent() {
    return (pojo, settings, writer) -> {
      final PList<String> assignments =
          pojo.getMembersOrEmpty().flatMap(PojoConstructorGenerator::createMemberAssignment);
      return assignments.foldLeft(writer, Writer::println);
    };
  }

  private static PList<String> createMemberAssignment(JavaPojoMember member) {
    final Name memberName = member.getName();
    final String memberAssignment = String.format("this.%s = %s;", memberName, memberName);
    if (member.isRequiredAndNullable()) {
      final String requiredPresentFlagAssignment =
          String.format(
              "this.is%sPresent = is%sPresent;",
              memberName.startUpperCase(), memberName.startUpperCase());
      return PList.of(memberAssignment, requiredPresentFlagAssignment);
    } else if (member.isOptionalAndNullable()) {
      final String optionalNullFlagAssignment =
          String.format(
              "this.is%sNull = is%sNull;",
              memberName.startUpperCase(), memberName.startUpperCase());
      return PList.of(memberAssignment, optionalNullFlagAssignment);
    } else {
      return PList.single(memberAssignment);
    }
  }
}
