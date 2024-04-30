package com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator;

import static com.github.muehmar.gradle.openapi.generator.java.model.validation.ConstraintType.DECIMAL_MAX;
import static com.github.muehmar.gradle.openapi.generator.java.model.validation.ConstraintType.DECIMAL_MIN;
import static com.github.muehmar.gradle.openapi.generator.java.model.validation.ConstraintType.EMAIL;
import static com.github.muehmar.gradle.openapi.generator.java.model.validation.ConstraintType.MAX;
import static com.github.muehmar.gradle.openapi.generator.java.model.validation.ConstraintType.MIN;
import static com.github.muehmar.gradle.openapi.generator.java.model.validation.ConstraintType.MULTIPLE_OF;
import static com.github.muehmar.gradle.openapi.generator.java.model.validation.ConstraintType.PATTERN;
import static com.github.muehmar.gradle.openapi.generator.java.model.validation.ConstraintType.SIZE;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import com.github.muehmar.gradle.openapi.generator.java.JavaEscaper;
import com.github.muehmar.gradle.openapi.generator.java.model.name.IsNotNullFlagName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.IsPresentFlagName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassNames;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaObjectType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.model.validation.ConstraintType;
import com.github.muehmar.gradle.openapi.generator.java.model.validation.JavaConstraints;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.PropertyCount;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.task.TaskIdentifier;
import com.github.muehmar.gradle.openapi.util.Optionals;
import com.github.muehmar.gradle.openapi.warnings.Warning;
import com.github.muehmar.gradle.openapi.warnings.WarningsContext;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;

class ConstraintConditions {
  private ConstraintConditions() {}

  public interface Condition extends Generator<PropertyValue, PojoSettings> {}

  public static Condition minCondition() {
    return (propertyValue, settings, writer) ->
        propertyValue
            .getType()
            .getConstraints()
            .getMin()
            .map(
                min ->
                    writer.print(
                        "%s <= %s", min.getValueAsLiteralString(), propertyValue.getAccessor()))
            .filter(ignore -> isSupportedConstraint(propertyValue, MIN, settings))
            .orElse(writer);
  }

  public static Condition maxCondition() {
    return (propertyValue, settings, writer) ->
        propertyValue
            .getType()
            .getConstraints()
            .getMax()
            .map(
                max ->
                    writer.print(
                        "%s <= %s", propertyValue.getAccessor(), max.getValueAsLiteralString()))
            .filter(ignore -> isSupportedConstraint(propertyValue, MAX, settings))
            .orElse(writer);
  }

  public static Condition minSizeCondition() {
    return (propertyValue, settings, writer) -> {
      final Constraints constraints = propertyValue.getType().getConstraints();
      final Optional<Integer> minSize = constraints.getSize().flatMap(Size::getMin);
      final Optional<Integer> minPropertyCountForMap =
          constraints
              .getPropertyCount()
              .flatMap(PropertyCount::getMinProperties)
              .filter(ignore -> propertyValue.getType().isMapType());
      return Optionals.or(minSize, minPropertyCountForMap)
          .flatMap(
              min ->
                  sizeAccessorForProperty(propertyValue, settings)
                      .map(
                          accessor ->
                              writer.print(
                                  "%d <= %s.%s", min, propertyValue.getAccessor(), accessor)))
          .orElse(writer);
    };
  }

  public static Condition maxSizeCondition() {
    return (propertyValue, settings, writer) -> {
      final JavaType propertyValueType = propertyValue.getType();
      final Constraints constraints = propertyValueType.getConstraints();
      final Optional<Integer> maxSize = constraints.getSize().flatMap(Size::getMax);
      final Optional<Integer> maxPropertyCountForMap =
          constraints
              .getPropertyCount()
              .flatMap(PropertyCount::getMaxProperties)
              .filter(ignore -> propertyValueType.isMapType());
      return Optionals.or(maxSize, maxPropertyCountForMap)
          .flatMap(
              max ->
                  sizeAccessorForProperty(propertyValue, settings)
                      .map(
                          accessor ->
                              writer.print(
                                  "%s.%s <= %d", propertyValue.getAccessor(), accessor, max)))
          .orElse(writer);
    };
  }

  public static Optional<String> sizeAccessorForProperty(
      PropertyValue propertyValue, PojoSettings settings) {
    final JavaType propertyValueType = propertyValue.getType();
    if (propertyValueType.isJavaArray()) {
      return Optional.of("length");
    }

    if (isSupportedConstraint(propertyValue, SIZE, settings)) {

      if (propertyValueType.getQualifiedClassName().equals(QualifiedClassNames.STRING)) {
        return Optional.of("length()");
      }

      if (QualifiedClassNames.ALL_MAP_CLASSNAMES.exists(
          propertyValueType.getQualifiedClassName()::equals)) {
        return Optional.of("size()");
      }

      if (QualifiedClassNames.ALL_LIST_CLASSNAMES.exists(
          propertyValueType.getQualifiedClassName()::equals)) {
        return Optional.of("size()");
      }
    }

    return Optional.empty();
  }

  public static Condition decimalMinCondition() {
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
                            propertyValue.getAccessor(),
                            decimalMin.getValue())
                        .ref(JavaRefs.JAVA_MATH_BIG_DECIMAL))
            .filter(ignore -> isSupportedConstraint(propertyValue, DECIMAL_MIN, settings))
            .orElse(writer);
  }

  public static Condition decimalMaxCondition() {
    return (propertyValue, settings, writer) ->
        propertyValue
            .getType()
            .getConstraints()
            .getDecimalMax()
            .map(
                decimalMax ->
                    writer
                        .print(
                            "BigDecimal.valueOf(%s).compareTo(new BigDecimal(\"%s\")) <%s 0",
                            propertyValue.getAccessor(),
                            decimalMax.getValue(),
                            decimalMax.isInclusiveMax() ? "=" : "")
                        .ref(JavaRefs.JAVA_MATH_BIG_DECIMAL))
            .filter(ignore -> isSupportedConstraint(propertyValue, DECIMAL_MAX, settings))
            .orElse(writer);
  }

  public static Condition patternCondition() {
    return (propertyValue, settings, writer) ->
        propertyValue
            .getType()
            .getConstraints()
            .getPattern()
            .map(
                pattern ->
                    writer.print(
                        "%s.matches(\"%s\", %s)",
                        JavaRefs.JAVA_UTIL_REGEX_PATTERN,
                        pattern.getPatternEscaped(JavaEscaper::escape),
                        propertyValue.getAccessor()))
            .filter(ignore -> isSupportedConstraint(propertyValue, PATTERN, settings))
            .orElse(writer);
  }

  public static Condition emailCondition() {
    return (propertyValue, settings, writer) ->
        propertyValue
            .getType()
            .getConstraints()
            .getEmail()
            .map(
                email ->
                    writer
                        .print("EmailValidator.isValid(%s)", propertyValue.getAccessor())
                        .ref(OpenApiUtilRefs.EMAIL_VALIDATOR))
            .filter(ignore -> isSupportedConstraint(propertyValue, EMAIL, settings))
            .orElse(writer);
  }

  public static Condition uniqueArrayItemsCondition() {
    return (propertyValue, settings, writer) -> {
      final boolean uniqueItems = propertyValue.getType().getConstraints().isUniqueItems();
      final boolean isArrayType = propertyValue.getType().isArrayType();
      if (uniqueItems && isArrayType) {
        final JavaName methodName =
            propertyValue.getName().startUpperCase().prefix("has").append("UniqueItems");
        return writer.print("%s()", methodName);
      } else {
        return writer;
      }
    };
  }

  public static Condition multipleOfCondition() {
    return (propertyValue, settings, writer) -> {
      final JavaName methodName =
          MethodNames.getIsMultipleOfValidMethodName(propertyValue.getName());
      return propertyValue
          .getType()
          .getConstraints()
          .getMultipleOf()
          .map(ignore -> writer.print("%s()", methodName))
          .filter(ignore -> isSupportedConstraint(propertyValue, MULTIPLE_OF, settings))
          .orElse(writer);
    };
  }

  public static Condition necessityAndNullabilityCondition(
      Generator<PropertyValue, PojoSettings> conditionsGenerator) {
    return (propertyValue, settings, writer) -> {
      final boolean hasNoOtherConditions =
          isSingleTrueCondition(conditionsGenerator, propertyValue, settings);
      if (propertyValue.isNested()) {
        return nestedNecessityAndNullabilityCondition(propertyValue, writer, hasNoOtherConditions);
      } else {
        return notNestedNecessityAndNullabilityCondition(
            propertyValue, writer, hasNoOtherConditions);
      }
    };
  }

  private static Writer nestedNecessityAndNullabilityCondition(
      PropertyValue propertyValue, Writer writer, boolean hasNoOtherConditions) {
    if (propertyValue.getNullability().isNullable()) {
      return writer.print("true");
    } else {
      if (hasNoOtherConditions) {
        return writer.print("%s != null", propertyValue.getAccessor());
      } else {
        return writer.print("false");
      }
    }
  }

  private static Writer notNestedNecessityAndNullabilityCondition(
      PropertyValue propertyValue, Writer writer, boolean hasNoOtherConditions) {
    if (propertyValue.isRequiredAndNotNullable()) {
      if (hasNoOtherConditions) {
        return writer.print("%s != null", propertyValue.getAccessor());
      } else {
        return writer.print("false", propertyValue.getAccessor());
      }
    } else if (propertyValue.isRequiredAndNullable()) {
      final JavaName isPresentFlagName =
          IsPresentFlagName.fromName(propertyValue.getName()).getName();
      if (hasNoOtherConditions) {
        return writer.print("(%s != null || %s)", propertyValue.getAccessor(), isPresentFlagName);
      } else {
        return writer.print("%s", isPresentFlagName);
      }
    } else if (propertyValue.isOptionalAndNotNullable()) {
      return writer.print("%s", IsNotNullFlagName.fromName(propertyValue.getName()).getName());
    } else {
      return writer;
    }
  }

  private static boolean isSupportedConstraint(
      PropertyValue propertyValue, ConstraintType constraintType, PojoSettings settings) {
    if (JavaConstraints.isSupported(propertyValue.getType(), constraintType)) {
      return true;
    } else {
      final TaskIdentifier taskIdentifier = settings.getTaskIdentifier();
      final Warning warning =
          Warning.unsupportedValidation(
              propertyValue.getPropertyInfoName(), propertyValue.getType(), constraintType);
      WarningsContext.addWarningForTask(taskIdentifier, warning);
      return false;
    }
  }

  public static Condition deepValidationCondition() {
    return (propertyValue, settings, writer) -> {
      final NestedValueName nestedValueName = NestedValueName.fromName(propertyValue.getName());
      final IsPropertyValidMethodName nestedIsValidMethodName =
          IsPropertyValidMethodName.fromName(nestedValueName.getName());
      return propertyValue
          .getType()
          .fold(
              arrayType -> deepValidateArrayType(propertyValue, nestedIsValidMethodName, writer),
              booleanType -> writer,
              enumType -> writer,
              mapType -> deepValidateMapType(propertyValue, nestedIsValidMethodName, writer),
              javaAnyType -> writer,
              numericType -> writer,
              integerType -> writer,
              objectType -> deepValidateObjectType(propertyValue, objectType, writer),
              stringType -> writer);
    };
  }

  private static Writer deepValidateArrayType(
      PropertyValue propertyValue,
      IsPropertyValidMethodName nestedIsValidMethodName,
      Writer writer) {
    return writer.print(
        "%s.stream().allMatch(this::%s)", propertyValue.getAccessor(), nestedIsValidMethodName);
  }

  private static Writer deepValidateMapType(
      PropertyValue propertyValue,
      IsPropertyValidMethodName nestedIsValidMethodName,
      Writer writer) {
    return writer.print(
        "%s.values().stream().allMatch(this::%s)",
        propertyValue.getAccessor(), nestedIsValidMethodName);
  }

  private static Writer deepValidateObjectType(
      PropertyValue propertyValue, JavaObjectType objectType, Writer writer) {
    return objectType.getOrigin().equals(JavaObjectType.TypeOrigin.OPENAPI)
        ? writer.print("%s.isValid()", propertyValue.getAccessor())
        : writer;
  }

  public static boolean isSingleTrueCondition(
      Generator<PropertyValue, PojoSettings> generator, PropertyValue pv, PojoSettings settings) {
    return generator.generate(pv, settings, javaWriter()).asString().equals("return true;");
  }
}
