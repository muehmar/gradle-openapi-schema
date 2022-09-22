package com.github.muehmar.gradle.openapi.generator.settings;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
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
  String builderMethodPrefix;
  boolean enableConstraints;
  List<ClassTypeMapping> classTypeMappings;
  List<FormatTypeMapping> formatTypeMappings;
  EnumDescriptionSettings enumDescriptionSettings;
  GetterSuffixes getterSuffixes;
  RawGetter rawGetter;
  List<String> excludeSchemas;

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

  @Getter("excludeSchemas")
  public List<String> getExcludeSchemasList() {
    return excludeSchemas;
  }

  public PList<ClassTypeMapping> getClassTypeMappings() {
    return PList.fromIter(classTypeMappings);
  }

  public PList<FormatTypeMapping> getFormatTypeMappings() {
    return PList.fromIter(formatTypeMappings);
  }

  public ExcludedSchemas getExcludedSchemas() {
    return ExcludedSchemas.fromExcludedPojoNames(
        PList.fromIter(excludeSchemas).map(name -> PojoName.ofNameAndSuffix(name, suffix)));
  }

  public boolean isEnableSafeBuilder() {
    return enableSafeBuilder;
  }

  public boolean isDisableSafeBuilder() {
    return !isEnableSafeBuilder();
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

  public RawGetter getRawGetter() {
    return rawGetter;
  }

  public TypeMappings getTypeMappings() {
    return new TypeMappings(PList.fromIter(classTypeMappings), PList.fromIter(formatTypeMappings));
  }
}
