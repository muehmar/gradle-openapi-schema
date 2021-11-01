package com.github.muehmar.gradle.openapi.generator.settings;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@PojoExtension
public class PojoSettings extends PojoSettingsExtension implements Serializable {
  private final JsonSupport jsonSupport;
  private final String packageName;
  private final String suffix;
  private final boolean enableSafeBuilder;
  private final boolean enableConstraints;
  private final List<ClassTypeMapping> classTypeMappings;
  private final List<FormatTypeMapping> formatTypeMappings;
  private final EnumDescriptionSettings enumDescriptionSettings;

  PojoSettings(
      JsonSupport jsonSupport,
      String packageName,
      String suffix,
      boolean enableSafeBuilder,
      boolean enableConstraints,
      List<ClassTypeMapping> classTypeMappings,
      List<FormatTypeMapping> formatTypeMappings,
      EnumDescriptionSettings enumDescriptionSettings) {
    this.jsonSupport = jsonSupport;
    this.packageName = packageName;
    this.suffix = suffix;
    this.enableSafeBuilder = enableSafeBuilder;
    this.enableConstraints = enableConstraints;
    this.classTypeMappings = classTypeMappings;
    this.formatTypeMappings = formatTypeMappings;
    this.enumDescriptionSettings = enumDescriptionSettings;
  }

  public PojoSettings(
      JsonSupport jsonSupport,
      String packageName,
      String suffix,
      boolean enableSafeBuilder,
      boolean enableConstraints,
      PList<ClassTypeMapping> classTypeMappings,
      PList<FormatTypeMapping> formatTypeMappings,
      EnumDescriptionSettings enumDescriptionSettings) {
    this.jsonSupport = jsonSupport;
    this.packageName = packageName;
    this.suffix = suffix;
    this.enableSafeBuilder = enableSafeBuilder;
    this.enableConstraints = enableConstraints;
    this.classTypeMappings = classTypeMappings.toArrayList();
    this.formatTypeMappings = formatTypeMappings.toArrayList();
    this.enumDescriptionSettings = enumDescriptionSettings;
  }

  public String getPackageName() {
    return packageName;
  }

  public String getSuffix() {
    return suffix;
  }

  public JsonSupport getJsonSupport() {
    return jsonSupport;
  }

  public boolean isJacksonJson() {
    return jsonSupport.equals(JsonSupport.JACKSON);
  }

  public boolean isEnableConstraints() {
    return enableConstraints;
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

  public EnumDescriptionSettings getEnumDescriptionSettings() {
    return enumDescriptionSettings;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PojoSettings that = (PojoSettings) o;
    return enableSafeBuilder == that.enableSafeBuilder
        && enableConstraints == that.enableConstraints
        && jsonSupport == that.jsonSupport
        && Objects.equals(packageName, that.packageName)
        && Objects.equals(suffix, that.suffix)
        && Objects.equals(classTypeMappings, that.classTypeMappings)
        && Objects.equals(formatTypeMappings, that.formatTypeMappings)
        && Objects.equals(enumDescriptionSettings, that.enumDescriptionSettings);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        jsonSupport,
        packageName,
        suffix,
        enableSafeBuilder,
        enableConstraints,
        classTypeMappings,
        formatTypeMappings,
        enumDescriptionSettings);
  }

  @Override
  public String toString() {
    return "PojoSettings{"
        + "jsonSupport="
        + jsonSupport
        + ", packageName='"
        + packageName
        + '\''
        + ", suffix='"
        + suffix
        + '\''
        + ", enableSafeBuilder="
        + enableSafeBuilder
        + ", enableConstraints="
        + enableConstraints
        + ", classTypeMappings="
        + classTypeMappings
        + ", formatTypeMappings="
        + formatTypeMappings
        + ", enumDescriptionExtraction="
        + enumDescriptionSettings
        + '}';
  }
}
