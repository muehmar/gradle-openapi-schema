package com.github.muehmar.gradle.openapi.generator.java.model.type.api;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.DtoMapping;
import com.github.muehmar.gradle.openapi.generator.settings.FormatTypeMapping;
import com.github.muehmar.gradle.openapi.task.TaskIdentifier;
import java.util.Optional;
import lombok.Value;

@Value
public class TypeMapping {
  QualifiedClassName className;
  Optional<ApiType> apiType;

  public static TypeMapping fromClassName(QualifiedClassName className) {
    return new TypeMapping(className, Optional.empty());
  }

  public static TypeMapping fromClassNameAndPluginApiType(
      QualifiedClassName className, Optional<PluginApiType> pluginApiType) {
    return new TypeMapping(className, pluginApiType.map(ApiType::ofPluginType));
  }

  public static TypeMapping fromClassMappings(
      QualifiedClassName internalClassName,
      Optional<PluginApiType> pluginApiType,
      PList<ClassTypeMapping> classTypeMappings,
      PList<JavaType> generics,
      TaskIdentifier taskIdentifier) {
    final QualifiedClassName standardApiType =
        pluginApiType.map(PluginApiType::getClassName).orElse(internalClassName);
    return classTypeMappings
        .filter(
            classMapping ->
                classMapping.getFromClass().equals(standardApiType.getClassName().asString()))
        .headOption()
        .map(
            classMapping -> {
              UsedMappingsContext.recordClassMappingUsage(taskIdentifier, classMapping);
              return fromClassMapping(internalClassName, pluginApiType, classMapping, generics);
            })
        .orElseGet(() -> fromClassNameAndPluginApiType(internalClassName, pluginApiType));
  }

  private static TypeMapping fromClassMapping(
      QualifiedClassName internalClassName,
      Optional<PluginApiType> pluginApiType,
      ClassTypeMapping classTypeMapping,
      PList<JavaType> generics) {
    final QualifiedClassName mappedClassName =
        QualifiedClassName.fromClassTypeMapping(classTypeMapping);

    return classTypeMapping
        .getTypeConversion()
        .map(conversion -> UserDefinedApiType.fromConversion(mappedClassName, conversion, generics))
        .map(
            userDefinedApiType ->
                new TypeMapping(
                    internalClassName, Optional.of(ApiType.of(userDefinedApiType, pluginApiType))))
        .orElseGet(() -> fromClassNameAndPluginApiType(mappedClassName, Optional.empty()));
  }

  public static TypeMapping fromFormatMappings(
      QualifiedClassName internalClassName,
      Optional<PluginApiType> pluginApiType,
      String formatString,
      PList<FormatTypeMapping> formatTypeMappings,
      PList<JavaType> generics,
      TaskIdentifier taskIdentifier) {
    return formatTypeMappings
        .filter(formatMapping -> formatMapping.getFormatType().equals(formatString))
        .headOption()
        .map(
            formatMapping -> {
              UsedMappingsContext.recordFormatMappingUsage(taskIdentifier, formatMapping);
              return fromFormatMapping(internalClassName, pluginApiType, formatMapping, generics);
            })
        .orElseGet(() -> fromClassNameAndPluginApiType(internalClassName, pluginApiType));
  }

  private static TypeMapping fromFormatMapping(
      QualifiedClassName internalClassName,
      Optional<PluginApiType> pluginApiType,
      FormatTypeMapping formatTypeMapping,
      PList<JavaType> generics) {
    final QualifiedClassName mappedClassName =
        QualifiedClassName.ofQualifiedClassName(formatTypeMapping.getClassType());

    return formatTypeMapping
        .getTypeConversion()
        .map(conversion -> UserDefinedApiType.fromConversion(mappedClassName, conversion, generics))
        .map(
            userDefinedApiType ->
                new TypeMapping(
                    internalClassName, Optional.of(ApiType.of(userDefinedApiType, pluginApiType))))
        .orElseGet(() -> fromClassNameAndPluginApiType(mappedClassName, Optional.empty()));
  }

  public static TypeMapping fromDtoMappings(
      QualifiedClassName internalClassName,
      PList<DtoMapping> dtoMappings,
      TaskIdentifier taskIdentifier) {
    return dtoMappings
        .filter(dtoMapping -> dtoMapping.getDtoName().equals(internalClassName.asString()))
        .headOption()
        .map(
            dtoMapping -> {
              UsedMappingsContext.recordDtoMappingUsage(taskIdentifier, dtoMapping);
              return fromDtoMapping(internalClassName, dtoMapping);
            })
        .orElseGet(() -> fromClassName(internalClassName));
  }

  private static TypeMapping fromDtoMapping(
      QualifiedClassName internalClassName, DtoMapping dtoMapping) {
    final QualifiedClassName mappedClassName =
        QualifiedClassName.ofQualifiedClassName(dtoMapping.getCustomType());

    return dtoMapping
        .getTypeConversion()
        .map(
            conversion ->
                UserDefinedApiType.fromConversion(mappedClassName, conversion, PList.empty()))
        .map(ApiType::ofUserDefinedType)
        .map(apiType -> new TypeMapping(internalClassName, Optional.of(apiType)))
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
