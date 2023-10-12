package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.validator;

import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import java.util.Optional;

public class PropertyValidationGenerator {
  private PropertyValidationGenerator() {}

  public static Generator<JavaPojoMember, PojoSettings> propertyValidationGenerator() {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(PRIVATE)
        .noGenericTypes()
        .returnType("boolean")
        .methodName(member -> IsPropertyValidMethodName.fromMember(member).asString())
        .noArguments()
        .content(propertyValidationMethodContent())
        .build();
  }

  private static Generator<JavaPojoMember, PojoSettings> propertyValidationMethodContent() {

    return wrapNotNullsafeGenerator(conditionGenerator(minCondition(), maxCondition()))
        .appendSingleBlankLine()
        .append(
            conditionGenerator(
                requiredNotNullCondition(),
                requiredNullableCondition(),
                optionalNotNullableCondition(),
                optionalNullableCondition()));
  }

  private static Generator<JavaPojoMember, PojoSettings> conditionGenerator(
      Condition... conditions) {
    return (member, settings, writer) -> {
      final NonEmptyList<String> formattedConditions =
          NonEmptyList.fromIter(
                  PList.fromArray(conditions).flatMapOptional(c -> c.format(member, settings)))
              .orElse(NonEmptyList.single("true"));

      final String firstFormatted = formattedConditions.head();
      final PList<String> remainingFormatted = formattedConditions.tail();

      if (remainingFormatted.isEmpty()) {
        return writer.println("return %s;", firstFormatted);
      } else {
        return remainingFormatted
            .foldLeft(
                writer.print("return %s", firstFormatted),
                (w, f) -> w.println().tab(2).print("&& %s", f))
            .println(";");
      }
    };
  }

  private static Generator<JavaPojoMember, PojoSettings> wrapNotNullsafeGenerator(
      Generator<JavaPojoMember, PojoSettings> conditions) {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append((m, s, w) -> w.println("if(%s != null) {", m.getNameAsIdentifier()))
        .append(conditions, 1)
        .append(Generator.constant("}"));
  }

  private static Condition requiredNotNullCondition() {
    return (member, settings) -> {
      if (member.isRequiredAndNotNullable()) {
        return Optional.of(String.format("%s != null", member.getNameAsIdentifier()));
      }
      return Optional.empty();
    };
  }

  private static Condition requiredNullableCondition() {
    return (member, settings) -> {
      if (member.isRequiredAndNullable()) {
        return Optional.of(
            String.format(
                "(%s != null || %s)", member.getNameAsIdentifier(), member.getIsPresentFlagName()));
      }
      return Optional.empty();
    };
  }

  private static Condition optionalNotNullableCondition() {
    // Implement with #142 when the flag is present
    return (member, settings) -> Optional.empty();
  }

  private static Condition optionalNullableCondition() {
    return (member, settings) -> Optional.empty();
  }

  private static Condition minCondition() {
    return (member, settings) ->
        member
            .getJavaType()
            .getConstraints()
            .getMin()
            .map(min -> String.format("%d <= %s", min.getValue(), member.getNameAsIdentifier()));
  }

  private static Condition maxCondition() {
    return (member, settings) ->
        member
            .getJavaType()
            .getConstraints()
            .getMax()
            .map(max -> String.format("%s <= %d", member.getNameAsIdentifier(), max.getValue()));
  }

  private interface Condition {
    Optional<String> format(JavaPojoMember member, PojoSettings settings);
  }
}
