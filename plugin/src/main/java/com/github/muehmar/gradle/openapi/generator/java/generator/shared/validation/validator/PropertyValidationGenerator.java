package com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator.PropertyValueBuilder.fullPropertyValueBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.model.validation.ConstraintType.DECIMAL_MAX;
import static com.github.muehmar.gradle.openapi.generator.java.model.validation.ConstraintType.DECIMAL_MIN;
import static com.github.muehmar.gradle.openapi.generator.java.model.validation.ConstraintType.EMAIL;
import static com.github.muehmar.gradle.openapi.generator.java.model.validation.ConstraintType.MAX;
import static com.github.muehmar.gradle.openapi.generator.java.model.validation.ConstraintType.MIN;
import static com.github.muehmar.gradle.openapi.generator.java.model.validation.ConstraintType.MULTIPLE_OF;
import static com.github.muehmar.gradle.openapi.generator.java.model.validation.ConstraintType.PATTERN;
import static com.github.muehmar.gradle.openapi.generator.java.model.validation.ConstraintType.SIZE;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.java.MethodGen.Argument.argument;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaEscaper;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.IsPresentFlagName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaPojoName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames;
import com.github.muehmar.gradle.openapi.generator.java.model.name.PropertyInfoName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassNames;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaRequiredAdditionalProperty;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaObjectType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.model.validation.ConstraintType;
import com.github.muehmar.gradle.openapi.generator.java.model.validation.JavaConstraints;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.PropertyCount;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.task.TaskIdentifier;
import com.github.muehmar.gradle.openapi.util.Optionals;
import com.github.muehmar.gradle.openapi.warnings.Warning;
import com.github.muehmar.gradle.openapi.warnings.WarningsContext;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.writer.Writer;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.util.Optional;
import lombok.Value;

public class PropertyValidationGenerator {
  private PropertyValidationGenerator() {}

  public static Generator<JavaPojoMember, PojoSettings> memberValidationGenerator() {
    return propertyValueValidationGenerator().contraMap(PropertyValue::fromJavaMember);
  }

  public static Generator<JavaObjectPojo, PojoSettings> requiredAdditionalPropertyGenerator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(
            propertyValueValidationGenerator(), PropertyValue::fromRequiredAdditionalProperties);
  }

  public static Generator<PropertyValue, PojoSettings> propertyValueValidationGenerator() {
    final MethodGen<PropertyValue, PojoSettings> method =
        JavaGenerators.<PropertyValue, PojoSettings>methodGen()
            .modifiers(PRIVATE)
            .noGenericTypes()
            .returnType("boolean")
            .methodName(
                propertyValue ->
                    IsPropertyValidMethodName.fromName(propertyValue.getName()).asString())
            .arguments(
                pv ->
                    pv.isNested
                        ? PList.single(
                            argument(pv.getType().getParameterizedClassName(), pv.getName()))
                        : PList.empty())
            .doesNotThrow()
            .content(singlePropertyValueValidationGenerator())
            .build();
    return (propertyValue, settings, writer) -> {
      final Writer validationMethodWriter = method.generate(propertyValue, settings, writer);

      return propertyValue
          .nestedPropertyValue()
          .map(
              nestedPropertyValue ->
                  propertyValueValidationGenerator()
                      .generate(nestedPropertyValue, settings, javaWriter()))
          .map(w -> validationMethodWriter.printSingleBlankLine().append(w))
          .orElse(validationMethodWriter);
    };
  }

  private static Generator<PropertyValue, PojoSettings> singlePropertyValueValidationGenerator() {
    final Generator<PropertyValue, PojoSettings> conditionsGenerator =
        conditionGenerator(
            minCondition(),
            maxCondition(),
            minSizeCondition(),
            maxSizeCondition(),
            decimalMinCondition(),
            decimalMaxCondition(),
            patternCondition(),
            emailCondition(),
            uniqueArrayItemsCondition(),
            multipleOfCondition(),
            deepValidationCondition());
    return wrapNotNullsafeGenerator(conditionsGenerator)
        .append(conditionGenerator(necessityAndNullabilityCondition(conditionsGenerator)));
  }

  private static Generator<PropertyValue, PojoSettings> conditionGenerator(
      Condition... conditions) {
    return (propertyValue, settings, writer) -> {
      final PList<Writer> conditionWriters =
          PList.fromArray(conditions)
              .map(gen -> gen.generate(propertyValue, settings, javaWriter()));
      final ReturningAndConditions returningAndConditions =
          ReturningAndConditions.forConditions(conditionWriters);
      return writer.append(returningAndConditions.getWriter());
    };
  }

  private static Generator<PropertyValue, PojoSettings> wrapNotNullsafeGenerator(
      Generator<PropertyValue, PojoSettings> conditions) {
    return Generator.<PropertyValue, PojoSettings>emptyGen()
        .append((pv, s, w) -> w.println("if(%s != null) {", pv.getAccessor()))
        .append(conditions, 1)
        .append(Generator.constant("}"))
        .appendSingleBlankLine()
        .filter((pv, settings) -> not(isSingleTrueCondition(conditions, pv, settings)));
  }

  private static Condition necessityAndNullabilityCondition(
      Generator<PropertyValue, PojoSettings> conditionsGenerator) {
    return (propertyValue, settings, writer) -> {
      final boolean noConditions =
          isSingleTrueCondition(conditionsGenerator, propertyValue, settings);
      if (propertyValue.isRequiredAndNotNullable()) {
        if (noConditions) {
          return writer.print("%s != null", propertyValue.getAccessor());
        } else {
          return writer.print("false", propertyValue.getAccessor());
        }
      } else if (propertyValue.isRequiredAndNullable()) {
        final JavaName isPresentFlagName =
            IsPresentFlagName.fromName(propertyValue.getName()).getName();
        if (noConditions) {
          return writer.print("(%s != null || %s)", propertyValue.getAccessor(), isPresentFlagName);
        } else {
          return writer.print("%s", isPresentFlagName);
        }
      } else if (propertyValue.isOptionalAndNotNullable()) {
        // Implement with #142 when the flag is present
        return writer;
      } else {
        return writer;
      }
    };
  }

  private static Condition minCondition() {
    return (propertyValue, settings, writer) ->
        propertyValue
            .getType()
            .getConstraints()
            .getMin()
            .map(min -> writer.print("%d <= %s", min.getValue(), propertyValue.getAccessor()))
            .filter(ignore -> isSupportedConstraint(propertyValue, MIN, settings))
            .orElse(writer);
  }

  private static Condition maxCondition() {
    return (propertyValue, settings, writer) ->
        propertyValue
            .getType()
            .getConstraints()
            .getMax()
            .map(max -> writer.print("%s <= %d", propertyValue.getAccessor(), max.getValue()))
            .filter(ignore -> isSupportedConstraint(propertyValue, MAX, settings))
            .orElse(writer);
  }

  private static Condition minSizeCondition() {
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

  private static Condition maxSizeCondition() {
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

  private static Optional<String> sizeAccessorForProperty(
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
                            propertyValue.getAccessor(),
                            decimalMin.getValue())
                        .ref(JavaRefs.JAVA_MATH_BIG_DECIMAL))
            .filter(ignore -> isSupportedConstraint(propertyValue, DECIMAL_MIN, settings))
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
                            "BigDecimal.valueOf(%s).compareTo(new BigDecimal(\"%s\")) <%s 0",
                            propertyValue.getAccessor(),
                            decimalMax.getValue(),
                            decimalMax.isInclusiveMax() ? "=" : "")
                        .ref(JavaRefs.JAVA_MATH_BIG_DECIMAL))
            .filter(ignore -> isSupportedConstraint(propertyValue, DECIMAL_MAX, settings))
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
                        "%s.matches(\"%s\", %s)",
                        JavaRefs.JAVA_UTIL_REGEX_PATTERN,
                        pattern.getPatternEscaped(JavaEscaper::escape),
                        propertyValue.getAccessor()))
            .filter(ignore -> isSupportedConstraint(propertyValue, PATTERN, settings))
            .orElse(writer);
  }

  private static Condition emailCondition() {
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

  private static Condition uniqueArrayItemsCondition() {
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

  private static Condition multipleOfCondition() {
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

  private static boolean isSupportedConstraint(
      PropertyValue propertyValue, ConstraintType constraintType, PojoSettings settings) {
    if (JavaConstraints.isSupported(propertyValue.getType(), constraintType)) {
      return true;
    } else {
      final TaskIdentifier taskIdentifier = settings.getTaskIdentifier();
      final Warning warning =
          Warning.unsupportedValidation(
              propertyValue.propertyInfoName, propertyValue.getType(), constraintType);
      WarningsContext.addWarningForTask(taskIdentifier, warning);
      return false;
    }
  }

  private static Condition deepValidationCondition() {
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

  private static boolean isSingleTrueCondition(
      Generator<PropertyValue, PojoSettings> generator, PropertyValue pv, PojoSettings settings) {
    return generator.generate(pv, settings, javaWriter()).asString().equals("return true;");
  }

  private interface Condition extends Generator<PropertyValue, PojoSettings> {}

  @Value
  @PojoBuilder(includeOuterClassName = false)
  public static class PropertyValue {
    PropertyInfoName propertyInfoName;
    JavaName name;
    String accessor;
    JavaType type;
    Nullability nullability;
    Necessity necessity;
    boolean isNested;

    public static PropertyValue fromJavaMember(JavaPojoMember member) {
      return fullPropertyValueBuilder()
          .propertyInfoName(member.getPropertyInfoName())
          .name(member.getName())
          .accessor(member.getName().asString())
          .type(member.getJavaType())
          .nullability(member.getNullability())
          .necessity(member.getNecessity())
          .isNested(false)
          .build();
    }

    public static PList<PropertyValue> fromRequiredAdditionalProperties(JavaObjectPojo pojo) {
      return pojo.getRequiredAdditionalProperties()
          .map(reqProp -> fromRequiredAdditionalProperty(reqProp, pojo.getJavaPojoName()));
    }

    public static PropertyValue fromRequiredAdditionalProperty(
        JavaRequiredAdditionalProperty additionalProperty, JavaPojoName pojoName) {
      return fullPropertyValueBuilder()
          .propertyInfoName(
              PropertyInfoName.fromPojoNameAndMemberName(pojoName, additionalProperty.getName()))
          .name(additionalProperty.getName())
          .accessor(
              String.format("%s()", additionalProperty.getName().startUpperCase().prefix("get")))
          .type(additionalProperty.getJavaType())
          .nullability(Nullability.NOT_NULLABLE)
          .necessity(Necessity.REQUIRED)
          .isNested(false)
          .build();
    }

    public static PropertyValue fromAdditionalProperties(JavaObjectPojo pojo) {
      return fromAdditionalProperties(pojo.getAdditionalProperties(), pojo.getJavaPojoName());
    }

    public static PropertyValue fromAdditionalProperties(
        JavaAdditionalProperties additionalProperties, JavaPojoName pojoName) {
      return fullPropertyValueBuilder()
          .propertyInfoName(
              PropertyInfoName.fromPojoNameAndMemberName(
                  pojoName, JavaAdditionalProperties.additionalPropertiesName()))
          .name(JavaAdditionalProperties.additionalPropertiesName())
          .accessor("getAdditionalProperties()")
          .type(additionalProperties.asTechnicalPojoMember().getJavaType())
          .nullability(Nullability.NOT_NULLABLE)
          .necessity(Necessity.REQUIRED)
          .isNested(false)
          .build();
    }

    private Optional<PropertyValue> nestedPropertyValue() {
      return type.fold(
          arrayType -> Optional.of(nestedForType(arrayType.getItemType())),
          booleanType -> Optional.empty(),
          enumType -> Optional.empty(),
          mapType -> Optional.of(nestedForType(mapType.getValue())),
          anyType -> Optional.empty(),
          numericType -> Optional.empty(),
          integerType -> Optional.empty(),
          objectType -> Optional.empty(),
          stringType -> Optional.empty());
    }

    private PropertyValue nestedForType(JavaType type) {
      final JavaName nestedName = NestedValueName.fromName(name).getName();
      return fullPropertyValueBuilder()
          .propertyInfoName(propertyInfoName)
          .name(nestedName)
          .accessor(nestedName.asString())
          .type(type)
          .nullability(Nullability.NULLABLE)
          .necessity(Necessity.OPTIONAL)
          .isNested(true)
          .build();
    }

    public boolean isRequiredAndNullable() {
      return necessity.isRequired() && nullability.isNullable();
    }

    public boolean isRequiredAndNotNullable() {
      return necessity.isRequired() && nullability.isNotNullable();
    }

    public boolean isOptionalAndNullable() {
      return necessity.isOptional() && nullability.isNullable();
    }

    public boolean isOptionalAndNotNullable() {
      return necessity.isOptional() && nullability.isNotNullable();
    }
  }
}
