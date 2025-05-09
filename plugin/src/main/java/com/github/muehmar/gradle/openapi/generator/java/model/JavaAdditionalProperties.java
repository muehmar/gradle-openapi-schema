package com.github.muehmar.gradle.openapi.generator.java.model;

import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaAnyType.javaAnyType;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import com.github.muehmar.gradle.openapi.exception.OpenApiGeneratorException;
import com.github.muehmar.gradle.openapi.generator.java.generator.enumpojo.EnumContentBuilder;
import com.github.muehmar.gradle.openapi.generator.java.generator.enumpojo.EnumGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TechnicalPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaPojoName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaEnumType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaMapType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaStringType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.model.AdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.model.type.AnyType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Optional;
import java.util.function.Function;
import lombok.Value;

@Value
public class JavaAdditionalProperties {
  private static final JavaName MAP_PROPERTY_NAME = JavaName.fromString("additionalProperties");
  boolean allowed;
  JavaType type;

  public static JavaAdditionalProperties wrap(
      JavaPojoName pojoName, AdditionalProperties additionalProperties, TypeMappings typeMappings) {
    final JavaType javaType = JavaType.wrap(additionalProperties.getType(), typeMappings);
    ensureNoMapTypeWithConversion(pojoName, javaType);
    return new JavaAdditionalProperties(additionalProperties.isAllowed(), javaType);
  }

  private static void ensureNoMapTypeWithConversion(JavaPojoName pojoName, JavaType javaType) {
    if (javaType.isMapType() && javaType.hasApiTypeDeep()) {
      final String message =
          String.format(
              "The pojo '%s' contains a Map as additional properties and a conversion is registered with mapping, which is currently not supported. This should be fixed with: https://github.com/muehmar/gradle-openapi-schema/issues/307.",
              pojoName);
      throw new OpenApiGeneratorException(message);
    }
  }

  public static JavaAdditionalProperties anyTypeAllowed() {
    return new JavaAdditionalProperties(true, javaAnyType(AnyType.create(NULLABLE)));
  }

  public static JavaAdditionalProperties allowedFor(JavaType type) {
    return new JavaAdditionalProperties(true, type);
  }

  public static JavaAdditionalProperties notAllowed() {
    return new JavaAdditionalProperties(false, javaAnyType(AnyType.create(NULLABLE)));
  }

  public static JavaName additionalPropertiesName() {
    return MAP_PROPERTY_NAME;
  }

  /** Returns a map type containing the property value type as value type in the map. */
  public JavaType getMapContainerType() {
    return JavaMapType.ofKeyAndValueType(JavaStringType.noFormat(), type);
  }

  public boolean isNotAllowed() {
    return not(allowed);
  }

  public boolean isNotValueAnyType() {
    return not(isValueAnyType());
  }

  public boolean isValueAnyType() {
    return type.isAnyType();
  }

  public TechnicalPojoMember asTechnicalPojoMember() {
    return TechnicalPojoMember.additionalProperties(type);
  }

  public Optional<EnumGenerator.EnumContent> asEnumContent() {
    final Function<JavaEnumType, Optional<EnumGenerator.EnumContent>> createEnumContent =
        enumType ->
            Optional.of(
                EnumContentBuilder.create()
                    .className(JavaName.fromName(type.getQualifiedClassName().getClassName()))
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
