package com.github.muehmar.gradle.openapi.generator.settings;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.annotations.Getter;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import java.io.Serializable;
import java.util.List;

@PojoExtension
@SuppressWarnings("java:S2160") // Created by PojoExtension
public class PojoSettings extends PojoSettingsExtension implements Serializable {
  private final JsonSupport jsonSupport;
  private final String packageName;
  private final String suffix;
  private final boolean enableSafeBuilder;
  private final boolean enableConstraints;
  private final List<ClassTypeMapping> classTypeMappings;
  private final List<FormatTypeMapping> formatTypeMappings;
  private final EnumDescriptionSettings enumDescriptionSettings;

  @SuppressWarnings("java:S107")
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

  @Getter("classTypeMappings")
  List<ClassTypeMapping> getClassTypeMappingsList() {
    return classTypeMappings;
  }

  @Getter("formatTypeMappings")
  List<FormatTypeMapping> getFormatTypeMappingsList() {
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

  public EnumDescriptionSettings getEnumDescriptionSettings() {
    return enumDescriptionSettings;
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
        + ", enumDescriptionSettings="
        + enumDescriptionSettings
        + '}';
  }
}
