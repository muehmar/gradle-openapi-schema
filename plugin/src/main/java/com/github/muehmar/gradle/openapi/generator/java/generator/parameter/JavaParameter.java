package com.github.muehmar.gradle.openapi.generator.java.generator.parameter;

import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Parameter;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.type.IntegerType;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Optional;
import lombok.Value;

@Value
public class JavaParameter {
  Name name;
  JavaType javaType;
  Optional<Object> defaultValue;

  public static JavaParameter wrap(Parameter parameter) {
    final Type type = parameter.getType();
    return new JavaParameter(
        parameter.getName(),
        JavaType.wrap(type, TypeMappings.empty()),
        parameter.getDefaultValue());
  }

  public Name getTypeClassName() {
    return javaType.getClassName();
  }

  public Name getParamClassName() {
    return name.startUpperCase();
  }

  public boolean printMinOrMax() {
    return getJavaType()
        .getType()
        .fold(
            numericType -> false,
            integerType -> true,
            stringType -> false,
            arrayType -> false,
            booleanType -> false,
            objectType -> false,
            enumType -> false,
            mapType -> false,
            noType -> false);
  }

  public boolean printDecimalMinOrMax() {
    return getJavaType()
        .getType()
        .fold(
            numericType -> true,
            integerType -> false,
            stringType -> false,
            arrayType -> false,
            booleanType -> false,
            objectType -> false,
            enumType -> false,
            mapType -> false,
            noType -> false);
  }

  public boolean printSize() {
    return getJavaType()
        .getType()
        .fold(
            numericType -> false,
            integerType -> false,
            stringType -> true,
            arrayType -> false,
            booleanType -> false,
            objectType -> false,
            enumType -> false,
            mapType -> false,
            noType -> false);
  }

  public boolean printPattern() {
    return getJavaType()
        .getType()
        .fold(
            numericType -> false,
            integerType -> false,
            stringType -> true,
            arrayType -> false,
            booleanType -> false,
            objectType -> false,
            enumType -> false,
            mapType -> false,
            noType -> false);
  }

  public boolean printDefaultValue() {
    return getJavaType()
        .getType()
        .fold(
            numericType -> true,
            integerType -> true,
            stringType -> true,
            arrayType -> false,
            booleanType -> false,
            objectType -> false,
            enumType -> false,
            mapType -> false,
            noType -> false);
  }

  public boolean printDefaultAsString() {
    return getJavaType()
        .getType()
        .fold(
            numericType -> true,
            integerType -> true,
            stringType -> false,
            arrayType -> false,
            booleanType -> false,
            objectType -> false,
            enumType -> false,
            mapType -> false,
            noType -> false);
  }

  public String formatConstant(Object value) {
    return getJavaType()
        .getType()
        .fold(
            numericType -> formatNumericConstant(numericType, value),
            integerType -> formatIntegerConstant(integerType, value),
            stringType -> String.format("\"%s\"", value),
            arrayType -> "",
            booleanType -> "",
            objectType -> "",
            enumType -> "",
            mapType -> "",
            noType -> "");
  }

  private String formatNumericConstant(NumericType numericType, Object value) {
    if (numericType.getFormat() == NumericType.Format.FLOAT) {
      return value + "f";
    }
    return value + "";
  }

  private String formatIntegerConstant(IntegerType integerType, Object value) {
    if (integerType.getFormat() == IntegerType.Format.LONG) {
      return value + "L";
    }
    return value + "";
  }
}
