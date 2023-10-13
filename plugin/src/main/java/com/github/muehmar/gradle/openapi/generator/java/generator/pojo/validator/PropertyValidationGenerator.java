package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.validator;

import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static java.lang.String.format;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaEscaper;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.QualifiedClassNames;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import java.util.HashMap;
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
    return wrapNotNullsafeGenerator(
            conditionGenerator(
                minCondition(),
                maxCondition(),
                minSizeCondition(),
                maxSizeCondition(),
                decimalMinCondition(),
                decimalMaxCondition(),
                patternCondition()))
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
        return Optional.of(format("%s != null", member.getNameAsIdentifier()));
      }
      return Optional.empty();
    };
  }

  private static Condition requiredNullableCondition() {
    return (member, settings) -> {
      if (member.isRequiredAndNullable()) {
        return Optional.of(
            format(
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
            .map(min -> format("%d <= %s", min.getValue(), member.getNameAsIdentifier()));
  }

  private static Condition maxCondition() {
    return (member, settings) ->
        member
            .getJavaType()
            .getConstraints()
            .getMax()
            .map(max -> format("%s <= %d", member.getNameAsIdentifier(), max.getValue()));
  }

  private static Condition minSizeCondition() {
    return (member, settings) -> {
      final Optional<String> sizeAccessor = sizeAccessorForMember(member);
      return member
          .getJavaType()
          .getConstraints()
          .getSize()
          .flatMap(Size::getMin)
          .flatMap(
              min ->
                  sizeAccessor.map(
                      accessor ->
                          format("%d <= %s.%s", min, member.getNameAsIdentifier(), accessor)));
    };
  }

  private static Condition maxSizeCondition() {
    return (member, settings) -> {
      final Optional<String> sizeAccessor = sizeAccessorForMember(member);
      return member
          .getJavaType()
          .getConstraints()
          .getSize()
          .flatMap(Size::getMax)
          .flatMap(
              max ->
                  sizeAccessor.map(
                      accessor ->
                          format("%s.%s <= %d", member.getNameAsIdentifier(), accessor, max)));
    };
  }

  private static Condition decimalMinCondition() {
    return (member, settings) ->
        member
            .getJavaType()
            .getConstraints()
            .getDecimalMin()
            .map(
                decimalMin ->
                    format(
                        "0 <%s java.math.BigDecimal.valueOf(%s).compareTo(new java.math.BigDecimal(\"%s\"))",
                        decimalMin.isInclusiveMin() ? "=" : "",
                        member.getNameAsIdentifier(),
                        decimalMin.getValue()));
  }

  private static Condition decimalMaxCondition() {
    return (member, settings) ->
        member
            .getJavaType()
            .getConstraints()
            .getDecimalMax()
            .map(
                decimalMax ->
                    format(
                        "java.math.BigDecimal.valueOf(%s).compareTo(new java.math.BigDecimal(\"%s\") <%s 0)",
                        member.getNameAsIdentifier(),
                        decimalMax.getValue(),
                        decimalMax.isInclusiveMax() ? "=" : ""));
  }

  private static Condition patternCondition() {
    return (member, settings) ->
        member
            .getJavaType()
            .getConstraints()
            .getPattern()
            .map(
                pattern ->
                    format(
                        "java.util.regex.Pattern.matches(\"%s\", %s)",
                        pattern.getPatternEscaped(JavaEscaper::escape),
                        member.getNameAsIdentifier()));
  }

  private static Optional<String> sizeAccessorForMember(JavaPojoMember member) {
    if (member.getJavaType().isJavaArray()) {
      return Optional.of("length");
    }

    final HashMap<QualifiedClassName, String> methodName = new HashMap<>();
    methodName.put(QualifiedClassNames.LIST, "size()");
    methodName.put(QualifiedClassNames.MAP, "size()");
    methodName.put(QualifiedClassNames.STRING, "length()");

    return Optional.ofNullable(methodName.get(member.getJavaType().getQualifiedClassName()));
  }

  private interface Condition {
    Optional<String> format(JavaPojoMember member, PojoSettings settings);
  }
}
