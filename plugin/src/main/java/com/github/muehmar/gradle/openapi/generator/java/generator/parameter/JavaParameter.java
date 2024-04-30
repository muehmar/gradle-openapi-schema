package com.github.muehmar.gradle.openapi.generator.java.generator.parameter;

import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassNames;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaIntegerType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaNumericType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.model.Parameter;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
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
    return javaType.getQualifiedClassName().getClassName();
  }

  public Name getParamClassName() {
    return name.startUpperCase();
  }

  public boolean printMinOrMax() {
    return getJavaType()
        .fold(
            arrayType -> false,
            booleanType -> false,
            enumType -> false,
            mapType -> false,
            anyType -> false,
            numericType -> false,
            integerType -> true,
            objectType -> false,
            stringType -> false);
  }

  public boolean printDecimalMinOrMax() {
    return getJavaType()
        .fold(
            arrayType -> false,
            booleanType -> false,
            enumType -> false,
            mapType -> false,
            anyType -> false,
            numericType -> true,
            integerType -> false,
            objectType -> false,
            stringType -> false);
  }

  public boolean printSize() {
    return getJavaType()
        .fold(
            arrayType -> false,
            booleanType -> false,
            enumType -> false,
            mapType -> false,
            anyType -> false,
            numericType -> false,
            integerType -> false,
            objectType -> false,
            stringType -> isJavaStringClass());
  }

  public boolean printPattern() {
    return getJavaType()
        .fold(
            arrayType -> false,
            booleanType -> false,
            enumType -> false,
            mapType -> false,
            anyType -> false,
            numericType -> false,
            integerType -> false,
            objectType -> false,
            stringType -> isJavaStringClass());
  }

  public boolean printDefaultValue() {
    return getJavaType()
        .fold(
            arrayType -> false,
            booleanType -> false,
            enumType -> false,
            mapType -> false,
            anyType -> false,
            numericType -> true,
            integerType -> true,
            objectType -> false,
            stringType -> isJavaStringClass());
  }

  public boolean printDefaultAsString() {
    return getJavaType()
        .fold(
            arrayType -> false,
            booleanType -> false,
            enumType -> false,
            mapType -> false,
            anyType -> false,
            numericType -> true,
            integerType -> true,
            objectType -> false,
            stringType -> false);
  }

  private boolean isJavaStringClass() {
    return getJavaType().getQualifiedClassName().equals(QualifiedClassNames.STRING);
  }

  public String formatConstant(Object value) {
    return getJavaType()
        .fold(
            arrayType -> "",
            booleanType -> "",
            enumType -> "",
            mapType -> "",
            anyType -> "",
            numericType -> formatNumericConstant(numericType, value),
            integerType -> formatIntegerConstant(integerType, value),
            objectType -> "",
            stringType -> String.format("\"%s\"", value));
  }

  private String formatNumericConstant(JavaNumericType numericType, Object value) {
    if (numericType.getQualifiedClassName().equals(QualifiedClassNames.FLOAT)) {
      return value + "f";
    }
    return value + "";
  }

  private String formatIntegerConstant(JavaIntegerType integerType, Object value) {
    if (integerType.getQualifiedClassName().equals(QualifiedClassNames.LONG)) {
      return value + "L";
    }
    return value + "";
  }
}
