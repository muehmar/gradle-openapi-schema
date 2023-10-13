package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.validator;

import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaEscaper;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.QualifiedClassNames;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.util.Strings;
import io.github.muehmar.codegenerator.writer.Writer;
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
      final PList<Writer> writers =
          PList.fromArray(conditions).map(gen -> gen.generate(member, settings, javaWriter()));
      final NonEmptyList<String> formattedConditions =
          NonEmptyList.fromIter(writers.map(Writer::asString).filter(Strings::nonEmptyOrBlank))
              .orElse(NonEmptyList.single("true"));

      final String firstFormatted = formattedConditions.head();
      final PList<String> remainingFormatted = formattedConditions.tail();

      final PList<String> refs = writers.flatMap(Writer::getRefs);

      if (remainingFormatted.isEmpty()) {
        return writer.println("return %s;", firstFormatted).refs(refs);
      } else {
        return remainingFormatted
            .foldLeft(
                writer.print("return %s", firstFormatted),
                (w, f) -> w.println().tab(2).print("&& %s", f))
            .println(";")
            .refs(refs);
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
    return (member, settings, writer) -> {
      if (member.isRequiredAndNotNullable()) {
        return writer.print("%s != null", member.getNameAsIdentifier());
      }
      return writer;
    };
  }

  private static Condition requiredNullableCondition() {
    return (member, settings, writer) -> {
      if (member.isRequiredAndNullable()) {
        return writer.print(
            "(%s != null || %s)", member.getNameAsIdentifier(), member.getIsPresentFlagName());
      }
      return writer;
    };
  }

  private static Condition optionalNotNullableCondition() {
    // Implement with #142 when the flag is present
    return (member, settings, writer) -> writer;
  }

  private static Condition optionalNullableCondition() {
    return (member, settings, writer) -> writer;
  }

  private static Condition minCondition() {
    return (member, settings, writer) ->
        member
            .getJavaType()
            .getConstraints()
            .getMin()
            .map(min -> writer.print("%d <= %s", min.getValue(), member.getNameAsIdentifier()))
            .orElse(writer);
  }

  private static Condition maxCondition() {
    return (member, settings, writer) ->
        member
            .getJavaType()
            .getConstraints()
            .getMax()
            .map(max -> writer.print("%s <= %d", member.getNameAsIdentifier(), max.getValue()))
            .orElse(writer);
  }

  private static Condition minSizeCondition() {
    return (member, settings, writer) -> {
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
                          writer.print("%d <= %s.%s", min, member.getNameAsIdentifier(), accessor)))
          .orElse(writer);
    };
  }

  private static Condition maxSizeCondition() {
    return (member, settings, writer) -> {
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
                          writer.print("%s.%s <= %d", member.getNameAsIdentifier(), accessor, max)))
          .orElse(writer);
    };
  }

  private static Condition decimalMinCondition() {
    return (member, settings, writer) ->
        member
            .getJavaType()
            .getConstraints()
            .getDecimalMin()
            .map(
                decimalMin ->
                    writer
                        .print(
                            "0 <%s BigDecimal.valueOf(%s).compareTo(new BigDecimal(\"%s\"))",
                            decimalMin.isInclusiveMin() ? "=" : "",
                            member.getNameAsIdentifier(),
                            decimalMin.getValue())
                        .ref(JavaRefs.JAVA_MATH_BIG_DECIMAL))
            .orElse(writer);
  }

  private static Condition decimalMaxCondition() {
    return (member, settings, writer) ->
        member
            .getJavaType()
            .getConstraints()
            .getDecimalMax()
            .map(
                decimalMax ->
                    writer
                        .print(
                            "BigDecimal.valueOf(%s).compareTo(new BigDecimal(\"%s\") <%s 0)",
                            member.getNameAsIdentifier(),
                            decimalMax.getValue(),
                            decimalMax.isInclusiveMax() ? "=" : "")
                        .ref(JavaRefs.JAVA_MATH_BIG_DECIMAL))
            .orElse(writer);
  }

  private static Condition patternCondition() {
    return (member, settings, writer) ->
        member
            .getJavaType()
            .getConstraints()
            .getPattern()
            .map(
                pattern ->
                    writer.print(
                        "java.util.regex.Pattern.matches(\"%s\", %s)",
                        pattern.getPatternEscaped(JavaEscaper::escape),
                        member.getNameAsIdentifier()))
            .orElse(writer);
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

  private interface Condition extends Generator<JavaPojoMember, PojoSettings> {}
}
