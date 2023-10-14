package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.validator;

import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaEscaper;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaMemberName;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.QualifiedClassNames;
import com.github.muehmar.gradle.openapi.generator.java.model.name.IsPresentFlagName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.util.Strings;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.HashMap;
import java.util.Optional;
import lombok.Value;

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
                optionalNullableCondition()))
        .contraMap(PropertyValue::fromJavaMember);
  }

  private static Generator<PropertyValue, PojoSettings> conditionGenerator(
      Condition... conditions) {
    return (propertyValue, settings, writer) -> {
      final PList<Writer> writers =
          PList.fromArray(conditions)
              .map(gen -> gen.generate(propertyValue, settings, javaWriter()));
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

  private static Generator<PropertyValue, PojoSettings> wrapNotNullsafeGenerator(
      Generator<PropertyValue, PojoSettings> conditions) {
    return Generator.<PropertyValue, PojoSettings>emptyGen()
        .append((pv, s, w) -> w.println("if(%s != null) {", pv.getNameAsIdentifier()))
        .append(conditions, 1)
        .append(Generator.constant("}"));
  }

  private static Condition requiredNotNullCondition() {
    return (propertyValue, settings, writer) -> {
      if (propertyValue.isRequiredAndNotNullable()) {
        return writer.print("%s != null", propertyValue.getNameAsIdentifier());
      }
      return writer;
    };
  }

  private static Condition requiredNullableCondition() {
    return (propertyValue, settings, writer) -> {
      if (propertyValue.isRequiredAndNullable()) {
        return writer.print(
            "(%s != null || %s)",
            propertyValue.getNameAsIdentifier(),
            IsPresentFlagName.fromJavaMemberName(propertyValue.getName()).getName());
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
    return (propertyValue, settings, writer) ->
        propertyValue
            .getType()
            .getConstraints()
            .getMin()
            .map(
                min ->
                    writer.print("%d <= %s", min.getValue(), propertyValue.getNameAsIdentifier()))
            .orElse(writer);
  }

  private static Condition maxCondition() {
    return (propertyValue, settings, writer) ->
        propertyValue
            .getType()
            .getConstraints()
            .getMax()
            .map(
                max ->
                    writer.print("%s <= %d", propertyValue.getNameAsIdentifier(), max.getValue()))
            .orElse(writer);
  }

  private static Condition minSizeCondition() {
    return (propertyValue, settings, writer) -> {
      final Optional<String> sizeAccessor = sizeAccessorForProperty(propertyValue);
      return propertyValue
          .getType()
          .getConstraints()
          .getSize()
          .flatMap(Size::getMin)
          .flatMap(
              min ->
                  sizeAccessor.map(
                      accessor ->
                          writer.print(
                              "%d <= %s.%s", min, propertyValue.getNameAsIdentifier(), accessor)))
          .orElse(writer);
    };
  }

  private static Condition maxSizeCondition() {
    return (propertyValue, settings, writer) -> {
      final Optional<String> sizeAccessor = sizeAccessorForProperty(propertyValue);
      return propertyValue
          .getType()
          .getConstraints()
          .getSize()
          .flatMap(Size::getMax)
          .flatMap(
              max ->
                  sizeAccessor.map(
                      accessor ->
                          writer.print(
                              "%s.%s <= %d", propertyValue.getNameAsIdentifier(), accessor, max)))
          .orElse(writer);
    };
  }

  private static Condition decimalMinCondition() {
    return (propertyValue, settings, writer) ->
        propertyValue
            .getType()
            .getConstraints()
            .getDecimalMin()
            .map(
                decimalMin ->
                    writer
                        .print(
                            "0 <%s BigDecimal.valueOf(%s).compareTo(new BigDecimal(\"%s\"))",
                            decimalMin.isInclusiveMin() ? "=" : "",
                            propertyValue.getNameAsIdentifier(),
                            decimalMin.getValue())
                        .ref(JavaRefs.JAVA_MATH_BIG_DECIMAL))
            .orElse(writer);
  }

  private static Condition decimalMaxCondition() {
    return (propertyValue, settings, writer) ->
        propertyValue
            .getType()
            .getConstraints()
            .getDecimalMax()
            .map(
                decimalMax ->
                    writer
                        .print(
                            "BigDecimal.valueOf(%s).compareTo(new BigDecimal(\"%s\") <%s 0)",
                            propertyValue.getNameAsIdentifier(),
                            decimalMax.getValue(),
                            decimalMax.isInclusiveMax() ? "=" : "")
                        .ref(JavaRefs.JAVA_MATH_BIG_DECIMAL))
            .orElse(writer);
  }

  private static Condition patternCondition() {
    return (propertyValue, settings, writer) ->
        propertyValue
            .getType()
            .getConstraints()
            .getPattern()
            .map(
                pattern ->
                    writer.print(
                        "java.util.regex.Pattern.matches(\"%s\", %s)",
                        pattern.getPatternEscaped(JavaEscaper::escape),
                        propertyValue.getNameAsIdentifier()))
            .orElse(writer);
  }

  private static Optional<String> sizeAccessorForProperty(PropertyValue propertyValue) {
    if (propertyValue.getType().isJavaArray()) {
      return Optional.of("length");
    }

    final HashMap<QualifiedClassName, String> methodName = new HashMap<>();
    methodName.put(QualifiedClassNames.LIST, "size()");
    methodName.put(QualifiedClassNames.MAP, "size()");
    methodName.put(QualifiedClassNames.STRING, "length()");

    return Optional.ofNullable(methodName.get(propertyValue.getType().getQualifiedClassName()));
  }

  private interface Condition extends Generator<PropertyValue, PojoSettings> {}

  @Value
  private static class PropertyValue {
    JavaMemberName name;
    JavaType type;
    Nullability nullability;
    Necessity necessity;

    public static PropertyValue fromJavaMember(JavaPojoMember member) {
      return new PropertyValue(
          member.getName(), member.getJavaType(), member.getNullability(), member.getNecessity());
    }

    private JavaIdentifier getNameAsIdentifier() {
      return name.asIdentifier();
    }

    private boolean isRequired() {
      return necessity.isRequired();
    }

    private boolean isOptional() {
      return necessity.isOptional();
    }

    private boolean isNullable() {
      return nullability.isNullable();
    }

    private boolean isNotNullable() {
      return nullability.isNotNullable();
    }

    public boolean isRequiredAndNullable() {
      return isRequired() && isNullable();
    }

    public boolean isRequiredAndNotNullable() {
      return isRequired() && isNotNullable();
    }

    public boolean isOptionalAndNullable() {
      return isOptional() && isNullable();
    }

    public boolean isOptionalAndNotNullable() {
      return isOptional() && isNotNullable();
    }
  }
}
