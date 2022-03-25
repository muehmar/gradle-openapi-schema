package com.github.muehmar.gradle.openapi.generator.settings;

import ch.bluecare.commons.data.PList;
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
}
