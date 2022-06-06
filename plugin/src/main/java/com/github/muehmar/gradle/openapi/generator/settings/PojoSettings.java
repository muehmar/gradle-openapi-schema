package com.github.muehmar.gradle.openapi.generator.settings;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import io.github.muehmar.pojoextension.annotations.FieldBuilder;
import io.github.muehmar.pojoextension.annotations.Getter;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import java.io.Serializable;
import java.util.List;
import lombok.Value;

@Value
@PojoExtension
public class PojoSettings implements PojoSettingsExtension, Serializable {
  JsonSupport jsonSupport;
  String packageName;
  String suffix;
  boolean enableSafeBuilder;
  boolean enableConstraints;
  List<ClassTypeMapping> classTypeMappings;
  List<FormatTypeMapping> formatTypeMappings;
  EnumDescriptionSettings enumDescriptionSettings;
  GetterSuffixes getterSuffixes;

  public boolean isJacksonJson() {
    return jsonSupport.equals(JsonSupport.JACKSON);
  }

  public boolean isEnableConstraints() {
    return enableConstraints;
  }

  @Getter("classTypeMappings")
  public List<ClassTypeMapping> getClassTypeMappingsList() {
    return classTypeMappings;
  }

  @Getter("formatTypeMappings")
  public List<FormatTypeMapping> getFormatTypeMappingsList() {
    return formatTypeMappings;
  }

  public PList<ClassTypeMapping> getClassTypeMappings() {
    return PList.fromIter(classTypeMappings);
  }

  public PList<FormatTypeMapping> getFormatTypeMappings() {
    return PList.fromIter(formatTypeMappings);
  }

  public boolean isEnableSafeBuilder() {
    return enableSafeBuilder;
  }

  public boolean isDisableSafeBuilder() {
    return !isEnableSafeBuilder();
  }

  public String suffixForField(PojoMember field) {
    if (field.isRequiredAndNotNullable()) {
      return getterSuffixes.getRequiredSuffix();
    } else if (field.isRequiredAndNullable()) {
      return getterSuffixes.getRequiredNullableSuffix();
    } else if (field.isOptionalAndNotNullable()) {
      return getterSuffixes.getOptionalSuffix();
    } else {
      return getterSuffixes.getOptionalNullableSuffix();
    }
  }

  @FieldBuilder(fieldName = "classTypeMappings")
  public static List<ClassTypeMapping> classTypeMappings(
      PList<ClassTypeMapping> classTypeMappings) {
    return classTypeMappings.toArrayList();
  }

  @FieldBuilder(fieldName = "formatTypeMappings")
  public static List<FormatTypeMapping> formatTypeMappings(
      PList<FormatTypeMapping> formatTypeMappings) {
    return formatTypeMappings.toArrayList();
  }
}
