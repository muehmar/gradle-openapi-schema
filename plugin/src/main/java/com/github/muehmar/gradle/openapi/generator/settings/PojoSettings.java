package com.github.muehmar.gradle.openapi.generator.settings;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
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
  ValidationMethods validationMethods;
  List<String> excludeSchemas;
  PojoNameMappings pojoNameMappings;

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
        PList.fromIter(excludeSchemas).map(Name::ofString));
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

  public TypeMappings getTypeMappings() {
    return new TypeMappings(PList.fromIter(classTypeMappings), PList.fromIter(formatTypeMappings));
  }

  public PojoNameMapping pojoNameMapping() {
    return PList.fromIter(pojoNameMappings.getConstantNameMappings())
        .map(
            constantNameMapping ->
                PojoNameMapping.replaceConstant(
                    constantNameMapping.getConstant(), constantNameMapping.getReplacement()))
        .foldLeft(PojoNameMapping.noMapping(), PojoNameMapping::andThen);
  }
}
