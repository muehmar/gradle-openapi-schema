package com.github.muehmar.gradle.openapi.generator.java.model;

import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaAnyType.javaAnyType;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import com.github.muehmar.gradle.openapi.generator.java.generator.enumpojo.EnumGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TechnicalPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaMapType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaStringType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.NonGenericJavaType;
import com.github.muehmar.gradle.openapi.generator.model.AdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.model.type.AnyType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Optional;
import lombok.Value;

@Value
public class JavaAdditionalProperties {
  private static final JavaName MAP_PROPERTY_NAME = JavaName.fromString("additionalProperties");
  boolean allowed;
  NonGenericJavaType type;

  public static JavaAdditionalProperties wrap(
      AdditionalProperties additionalProperties, TypeMappings typeMappings) {
    final NonGenericJavaType javaType = JavaType.wrap(additionalProperties.getType(), typeMappings);
    return new JavaAdditionalProperties(additionalProperties.isAllowed(), javaType);
  }

  public static JavaAdditionalProperties anyTypeAllowed() {
    return new JavaAdditionalProperties(true, javaAnyType(AnyType.create(NULLABLE)));
  }

  public static JavaAdditionalProperties allowedFor(NonGenericJavaType type) {
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

  /**
   * Returns the content of the nested enum class in case the value type is an enum. As the value
   * type is never a container (see {@link
   * com.github.muehmar.gradle.openapi.generator.model.type.AdditionalPropertiesValueType}), no enum
   * nested within a container has to be considered.
   */
  public Optional<EnumGenerator.EnumContent> asEnumContent() {
    return type.foldNonGenericJavaType(
        ignore -> Optional.empty(),
        enumType -> enumType.getNestedEnumContent("Additional property enum"),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty());
  }
}
