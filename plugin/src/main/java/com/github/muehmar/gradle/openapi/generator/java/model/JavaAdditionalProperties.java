package com.github.muehmar.gradle.openapi.generator.java.model;

import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaAnyType.javaAnyType;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import com.github.muehmar.gradle.openapi.generator.java.generator.enumpojo.EnumContentBuilder;
import com.github.muehmar.gradle.openapi.generator.java.generator.enumpojo.EnumGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaEnumType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.model.AdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Optional;
import java.util.function.Function;
import lombok.Value;

@Value
public class JavaAdditionalProperties {
  private static final JavaIdentifier MAP_PROPERTY_NAME =
      JavaIdentifier.fromString("additionalProperties");
  boolean allowed;
  JavaType type;

  public static JavaAdditionalProperties wrap(
      AdditionalProperties additionalProperties, TypeMappings typeMappings) {
    return new JavaAdditionalProperties(
        additionalProperties.isAllowed(),
        JavaType.wrap(additionalProperties.getType(), typeMappings));
  }

  public static JavaAdditionalProperties anyTypeAllowed() {
    return new JavaAdditionalProperties(true, javaAnyType());
  }

  public static JavaAdditionalProperties allowedFor(JavaType type) {
    return new JavaAdditionalProperties(true, type);
  }

  public static JavaAdditionalProperties notAllowed() {
    return new JavaAdditionalProperties(false, javaAnyType());
  }

  public static JavaIdentifier additionalPropertiesName() {
    return MAP_PROPERTY_NAME;
  }

  public boolean isValueAnyType() {
    return type.equals(javaAnyType());
  }

  public boolean isNotValueAnyType() {
    return not(type.equals(javaAnyType()));
  }

  public Optional<EnumGenerator.EnumContent> asEnumContent() {
    final Function<JavaEnumType, Optional<EnumGenerator.EnumContent>> createEnumContent =
        enumType ->
            Optional.of(
                EnumContentBuilder.create()
                    .className(JavaIdentifier.fromName(type.getQualifiedClassName().getClassName()))
                    .description("Additional property enum")
                    .members(enumType.getMembers())
                    .build());
    return type.fold(
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        createEnumContent,
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty());
  }
}
