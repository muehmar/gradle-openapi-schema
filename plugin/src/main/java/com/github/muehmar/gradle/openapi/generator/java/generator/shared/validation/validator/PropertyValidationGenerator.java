package com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator;

import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaEscaper;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.QualifiedClassNames;
import com.github.muehmar.gradle.openapi.generator.java.model.name.IsPresentFlagName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaObjectType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.HashMap;
import java.util.Optional;
import lombok.Value;

public class PropertyValidationGenerator {
  private PropertyValidationGenerator() {}

  public static Generator<JavaPojoMember, PojoSettings> memberValidationGenerator() {
    return propertyValueValidationGenerator().contraMap(PropertyValue::fromJavaMember);
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
                            new MethodGen.Argument(
                                pv.getType().getFullClassName().asString(),
                                pv.getNameAsIdentifier().asString()))
                        : PList.empty())
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
    return wrapNotNullsafeGenerator(
            conditionGenerator(
                minCondition(),
                maxCondition(),
                minSizeCondition(),
                maxSizeCondition(),
                decimalMinCondition(),
                decimalMaxCondition(),
                patternCondition(),
                deepValidationCondition()))
        .appendSingleBlankLine()
        .append(
            conditionGenerator(
                requiredNotNullCondition(),
                requiredNullableCondition(),
                optionalNotNullableCondition(),
                optionalNullableCondition()));
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
        .append(Generator.constant("}"));
  }

  private static Condition requiredNotNullCondition() {
    return (propertyValue, settings, writer) -> {
      if (propertyValue.isRequiredAndNotNullable()) {
        return writer.print("%s != null", propertyValue.getAccessor());
      }
      return writer;
    };
  }

  private static Condition requiredNullableCondition() {
    return (propertyValue, settings, writer) -> {
      if (propertyValue.isRequiredAndNullable()) {
        return writer.print(
            "(%s != null || %s)",
            propertyValue.getAccessor(),
            IsPresentFlagName.fromName(propertyValue.getName()).getName());
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
            .map(min -> writer.print("%d <= %s", min.getValue(), propertyValue.getAccessor()))
            .orElse(writer);
  }

  private static Condition maxCondition() {
    return (propertyValue, settings, writer) ->
        propertyValue
            .getType()
            .getConstraints()
            .getMax()
            .map(max -> writer.print("%s <= %d", propertyValue.getAccessor(), max.getValue()))
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
                          writer.print("%d <= %s.%s", min, propertyValue.getAccessor(), accessor)))
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
                          writer.print("%s.%s <= %d", propertyValue.getAccessor(), accessor, max)))
          .orElse(writer);
    };
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
                        propertyValue.getAccessor()))
            .orElse(writer);
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

  private interface Condition extends Generator<PropertyValue, PojoSettings> {}

  @Value
  public static class PropertyValue {
    Name name;
    String accessor;
    JavaType type;
    Nullability nullability;
    Necessity necessity;
    boolean isNested;

    public static PropertyValue fromJavaMember(JavaPojoMember member) {
      return new PropertyValue(
          member.getName().asName(),
          member.getNameAsIdentifier().asString(),
          member.getJavaType(),
          member.getNullability(),
          member.getNecessity(),
          false);
    }

    public static PropertyValue fromAdditionalProperties(
        JavaAdditionalProperties additionalProperties) {
      return new PropertyValue(
          Name.ofString(JavaAdditionalProperties.additionalPropertiesName().asString()),
          "getAdditionalProperties()",
          additionalProperties.asTechnicalPojoMember().getJavaType(),
          Nullability.NOT_NULLABLE,
          Necessity.REQUIRED,
          false);
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
      final Name nestedName = NestedValueName.fromName(name).getName();
      return new PropertyValue(
          nestedName,
          JavaIdentifier.fromName(nestedName).asString(),
          type,
          Nullability.NOT_NULLABLE,
          Necessity.REQUIRED,
          true);
    }

    public JavaIdentifier getNameAsIdentifier() {
      return JavaIdentifier.fromName(name);
    }

    public boolean isRequiredAndNullable() {
      return necessity.isRequired() && nullability.isNullable();
    }

    public boolean isRequiredAndNotNullable() {
      return necessity.isRequired() && nullability.isNotNullable();
    }
  }
}
