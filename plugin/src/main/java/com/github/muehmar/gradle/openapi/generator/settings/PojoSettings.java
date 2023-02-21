package com.github.muehmar.gradle.openapi.generator.settings;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import io.github.muehmar.pojobuilder.annotations.FieldBuilder;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.io.Serializable;
import java.util.List;
import lombok.Value;
import lombok.With;

@Value
@With
@PojoBuilder
public class PojoSettings implements Serializable {
  JsonSupport jsonSupport;
  String packageName;
  String suffix;
  boolean enableSafeBuilder;
  String builderMethodPrefix;
  boolean enableValidation;

  ValidationApi validationApi;
  List<ClassTypeMapping> classTypeMappings;
  List<FormatTypeMapping> formatTypeMappings;
  EnumDescriptionSettings enumDescriptionSettings;
  GetterSuffixes getterSuffixes;
  RawGetter rawGetter;
  List<String> excludeSchemas;

  public boolean isJacksonJson() {
    return jsonSupport.equals(JsonSupport.JACKSON);
  }

  public boolean isEnableValidation() {
    return enableValidation;
  }

  public List<ClassTypeMapping> getClassTypeMappingsList() {
    return classTypeMappings;
  }

  public List<FormatTypeMapping> getFormatTypeMappingsList() {
    return formatTypeMappings;
  }

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
