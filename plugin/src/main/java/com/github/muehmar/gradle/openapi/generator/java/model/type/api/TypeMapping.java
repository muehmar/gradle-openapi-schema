package com.github.muehmar.gradle.openapi.generator.java.model.type.api;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.FormatTypeMapping;
import java.util.Optional;
import lombok.Value;

@Value
public class TypeMapping {
  QualifiedClassName className;
  Optional<ApiType> apiType;

  public static TypeMapping fromClassName(QualifiedClassName className) {
    return new TypeMapping(className, Optional.empty());
  }

  public static TypeMapping fromClassMappings(
      QualifiedClassName originalClassName, PList<ClassTypeMapping> classTypeMappings) {
    return fromClassMappings(originalClassName, classTypeMappings, PList.empty());
  }

  public static TypeMapping fromClassMappings(
      QualifiedClassName originalClassName,
      PList<ClassTypeMapping> classTypeMappings,
      PList<JavaType> generics) {
    return classTypeMappings
        .filter(
            classMapping ->
                classMapping.getFromClass().equals(originalClassName.getClassName().asString()))
        .headOption()
        .map(classMapping -> fromClassMapping(originalClassName, classMapping, generics))
        .orElseGet(() -> fromClassName(originalClassName));
  }

  private static TypeMapping fromClassMapping(
      QualifiedClassName originalClassName,
      ClassTypeMapping classTypeMapping,
      PList<JavaType> generics) {
    final QualifiedClassName mappedClassName =
        QualifiedClassName.fromClassTypeMapping(classTypeMapping);

    return classTypeMapping
        .getTypeConversion()
        .map(conversion -> ApiType.fromConversion(mappedClassName, conversion, generics))
        .map(apiType -> new TypeMapping(originalClassName, Optional.of(apiType)))
        .orElseGet(() -> fromClassName(mappedClassName));
  }

  public static TypeMapping fromFormatMappings(
      QualifiedClassName originalClassName,
      String formatString,
      PList<FormatTypeMapping> formatTypeMappings) {
    return fromFormatMappings(originalClassName, formatString, formatTypeMappings, PList.empty());
  }

  public static TypeMapping fromFormatMappings(
      QualifiedClassName originalClassName,
      String formatString,
      PList<FormatTypeMapping> formatTypeMappings,
      PList<JavaType> generics) {
    return formatTypeMappings
        .filter(formatMapping -> formatMapping.getFormatType().equals(formatString))
        .headOption()
        .map(formatMapping -> fromFormatMapping(originalClassName, formatMapping, generics))
        .orElseGet(() -> fromClassName(originalClassName));
  }

  private static TypeMapping fromFormatMapping(
      QualifiedClassName originalClassName,
      FormatTypeMapping formatTypeMapping,
      PList<JavaType> generics) {
    final QualifiedClassName mappedClassName =
        QualifiedClassName.ofQualifiedClassName(formatTypeMapping.getClassType());

    return formatTypeMapping
        .getTypeConversion()
        .map(conversion -> ApiType.fromConversion(mappedClassName, conversion, generics))
        .map(apiType -> new TypeMapping(originalClassName, Optional.of(apiType)))
        .orElseGet(() -> fromClassName(mappedClassName));
  }

  public TypeMapping or(TypeMapping other, QualifiedClassName originalClassName) {
    if (this.apiType.isPresent()) {
      return this;
    } else if (other.getApiType().isPresent()) {
      return other;
    } else if (this.className.equals(originalClassName)) {
      return other;
    } else {
      return this;
    }
  }
}
