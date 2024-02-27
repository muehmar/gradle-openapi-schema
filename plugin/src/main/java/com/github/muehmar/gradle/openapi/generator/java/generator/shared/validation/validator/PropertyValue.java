package com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator.PropertyValueBuilder.fullPropertyValueBuilder;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.additionalproperties.FrameworkAdditionalPropertiesGetter;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaPojoName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.PropertyInfoName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaRequiredAdditionalProperty;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.util.Optional;
import lombok.Value;

@Value
@PojoBuilder(includeOuterClassName = false)
public class PropertyValue {
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
        .nullability(NOT_NULLABLE)
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
        .accessor(String.format("%s()", FrameworkAdditionalPropertiesGetter.METHOD_NAME))
        .type(additionalProperties.asTechnicalPojoMember().getJavaType())
        .nullability(NOT_NULLABLE)
        .necessity(Necessity.REQUIRED)
        .isNested(false)
        .build();
  }

  public Optional<PropertyValue> nestedPropertyValue() {
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
        .nullability(type.getNullability())
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
