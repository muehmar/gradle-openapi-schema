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
    return conditionGenerator(minCondition(), maxCondition());
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
