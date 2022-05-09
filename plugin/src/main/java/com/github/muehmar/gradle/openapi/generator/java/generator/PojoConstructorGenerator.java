package com.github.muehmar.gradle.openapi.generator.java.generator;

import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.Resolver;
import com.github.muehmar.gradle.openapi.generator.data.Name;
import com.github.muehmar.gradle.openapi.generator.data.Pojo;
import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import com.github.muehmar.gradle.openapi.generator.java.JavaResolver;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import java.util.function.BiFunction;

public class PojoConstructorGenerator {

  public static final Resolver RESOLVER = new JavaResolver();

  private PojoConstructorGenerator() {}

  public static Generator<Pojo, PojoSettings> generator() {
    return ConstructorGeneratorBuilder.<Pojo, PojoSettings>create()
        .modifiers(PUBLIC)
        .className(pojo -> pojo.className(RESOLVER).asString())
        .arguments(constructorArguments())
        .content(constructorContent())
        .build();
  }

  private static BiFunction<Pojo, PojoSettings, PList<String>> constructorArguments() {
    return (pojo, pojoSettings) ->
        pojo.getMembers().flatMap(PojoConstructorGenerator::createArguments);
  }

  private static PList<String> createArguments(PojoMember member) {
    final String memberArgument =
        String.format("%s %s", member.getTypeName(RESOLVER), member.memberName(RESOLVER));
    if (member.isRequired() && member.isNullable()) {
      final String requiredPresentFlag =
          String.format("boolean is%sPresent", member.memberName(RESOLVER).startUpperCase());
      return PList.of(memberArgument, requiredPresentFlag);
    } else if (member.isOptional() && member.isNullable()) {
      final String optionalNullFlag =
          String.format("boolean is%sNull", member.memberName(RESOLVER).startUpperCase());
      return PList.of(memberArgument, optionalNullFlag);
    } else {
      return PList.single(memberArgument);
    }
  }

  private static Generator<Pojo, PojoSettings> constructorContent() {
    return (pojo, settings, writer) -> {
      final PList<String> assignments =
          pojo.getMembers().flatMap(PojoConstructorGenerator::createMemberAssignment);
      return assignments.foldLeft(writer, Writer::println);
    };
  }

  private static PList<String> createMemberAssignment(PojoMember member) {
    final Name memberName = member.memberName(RESOLVER);
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
