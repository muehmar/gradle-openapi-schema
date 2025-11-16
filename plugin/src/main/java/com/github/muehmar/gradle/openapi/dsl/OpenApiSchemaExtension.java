package com.github.muehmar.gradle.openapi.dsl;

import ch.bluecare.commons.data.PList;
import groovy.lang.Closure;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import lombok.EqualsAndHashCode;
import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.file.Directory;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Provider;

@EqualsAndHashCode
public class OpenApiSchemaExtension implements Serializable {
  private final NamedDomainObjectContainer<SingleSchemaExtension> schemaExtensions;

  private String sourceSet;
  private String outputDir;
  private String suffix;
  private String jsonSupport;
  private String xmlSupport;
  private StagedBuilder stagedBuilder;
  private Boolean allowNullableForEnums;
  private String builderMethodPrefix;
  private EnumDescriptionExtension enumDescriptionExtension = null;
  private final List<ClassMapping> classMappings;
  private final List<FormatTypeMapping> formatTypeMappings;
  private final List<DtoMapping> dtoMappings;
  private final List<ConstantSchemaNameMapping> constantSchemaNameMappings;
  private final GetterSuffixes getterSuffixes;
  private final ValidationConfig validation;
  private final WarningsConfig warnings;

  @Inject
  public OpenApiSchemaExtension(ObjectFactory objectFactory) {
    this.schemaExtensions = objectFactory.domainObjectContainer(SingleSchemaExtension.class);
    this.classMappings = new ArrayList<>();
    this.formatTypeMappings = new ArrayList<>();
    this.dtoMappings = new ArrayList<>();
    this.constantSchemaNameMappings = new ArrayList<>();
    this.getterSuffixes = GetterSuffixes.allUndefined();
    this.validation = objectFactory.newInstance(ValidationConfig.class);
    this.warnings = WarningsConfig.allUndefined();
    this.stagedBuilder = StagedBuilder.defaultStagedBuilder();
  }

  // DSL API
  public void setSourceSet(String sourceSet) {
    this.sourceSet = sourceSet;
  }

  // DSL API
  public void setOutputDir(String outputDir) {
    this.outputDir = outputDir;
  }

  // DSL API
  public void setOutputDir(Provider<Directory> outputDir) {
    this.outputDir =
        Optional.ofNullable(outputDir).map(Provider::get).map(Directory::toString).orElse(null);
  }

  // DSL API
  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }

  // DSL API
  public void setJsonSupport(String jsonSupport) {
    this.jsonSupport = jsonSupport;
  }

  // DSL API
  public void setXmlSupport(String xmlSupport) {
    this.xmlSupport = xmlSupport;
  }

  // DSL API
  public void setAllowNullableForEnums(Boolean allowNullableForEnums) {
    this.allowNullableForEnums = allowNullableForEnums;
  }

  // DSL API
  public void setBuilderMethodPrefix(String builderMethodPrefix) {
    this.builderMethodPrefix = builderMethodPrefix;
  }

  // DSL API
  public void schemas(Closure<SingleSchemaExtension> closure) {
    schemaExtensions.configure(closure);
  }

  // DSL API
  public void classMapping(Action<ClassMapping> action) {
    final ClassMapping classMapping = new ClassMapping();
    action.execute(classMapping);
    classMappings.add(classMapping);
  }

  // DSL API
  public void formatTypeMapping(Action<FormatTypeMapping> action) {
    final FormatTypeMapping formatTypeMapping = new FormatTypeMapping();
    action.execute(formatTypeMapping);
    formatTypeMappings.add(formatTypeMapping);
  }

  // DSL API
  public void dtoMapping(Action<DtoMapping> action) {
    final DtoMapping dtoMapping = new DtoMapping();
    action.execute(dtoMapping);
    dtoMappings.add(dtoMapping);
  }

  // DSL API
  public void enumDescriptionExtraction(Action<EnumDescriptionExtension> action) {
    enumDescriptionExtension = new EnumDescriptionExtension();
    action.execute(enumDescriptionExtension);
  }

  // DSL API
  public void getterSuffixes(Action<GetterSuffixes> action) {
    action.execute(getterSuffixes);
  }

  // DSL API
  public void stagedBuilder(Action<StagedBuilder> action) {
    action.execute(stagedBuilder);
  }

  // DSL API
  public void validation(Action<ValidationConfig> action) {
    action.execute(validation);
  }

  // DSL API - Groovy closure support
  public ValidationConfig validation(Closure<ValidationConfig> closure) {
    return org.gradle.util.internal.ConfigureUtil.configureSelf(closure, validation);
  }

  // DSL API
  public void constantSchemaNameMapping(Action<ConstantSchemaNameMapping> action) {
    final ConstantSchemaNameMapping constantSchemaNameMapping = new ConstantSchemaNameMapping();
    action.execute(constantSchemaNameMapping);
    constantSchemaNameMappings.add(constantSchemaNameMapping);
  }

  // DSL API
  public void warnings(Action<WarningsConfig> action) {
    action.execute(warnings);
  }

  private Optional<EnumDescriptionExtension> getCommonEnumDescription() {
    return Optional.ofNullable(enumDescriptionExtension);
  }

  private List<ClassMapping> getCommonClassMappings() {
    return classMappings;
  }

  private List<FormatTypeMapping> getCommonFormatTypeMappings() {
    return formatTypeMappings;
  }

  private List<DtoMapping> getCommonDtoMappings() {
    return dtoMappings;
  }

  private GetterSuffixes getCommonGetterSuffixes() {
    return getterSuffixes;
  }

  private ValidationConfig getCommonValidation() {
    return validation;
  }

  private List<ConstantSchemaNameMapping> getCommonConstantSchemaNameMappings() {
    return constantSchemaNameMappings;
  }

  private WarningsConfig getCommonWarnings() {
    return warnings;
  }

  private Optional<String> getCommonSourceSet() {
    return Optional.ofNullable(sourceSet);
  }

  private Optional<Boolean> getCommonAllowNullableForEnums() {
    return Optional.ofNullable(allowNullableForEnums);
  }

  private Optional<String> getCommonOutputDir() {
    return Optional.ofNullable(outputDir);
  }

  private Optional<String> getCommonSuffix() {
    return Optional.ofNullable(suffix);
  }

  private Optional<String> getCommonJsonSupport() {
    return Optional.ofNullable(jsonSupport);
  }

  private Optional<String> getCommonXmlSupport() {
    return Optional.ofNullable(xmlSupport);
  }

  private StagedBuilder getCommonStagedBuilder() {
    return stagedBuilder;
  }

  private Optional<String> getCommonBuilderMethodPrefix() {
    return Optional.ofNullable(builderMethodPrefix);
  }

  public PList<SingleSchemaExtension> getSchemaExtensions() {
    return PList.fromIter(schemaExtensions)
        .map(ext -> ext.withCommonSourceSet(getCommonSourceSet()))
        .map(ext -> ext.withCommonOutputDir(getCommonOutputDir()))
        .map(ext -> ext.withCommonSuffix(getCommonSuffix()))
        .map(ext -> ext.withCommonJsonSupport(getCommonJsonSupport()))
        .map(ext -> ext.withCommonXmlSupport(getCommonXmlSupport()))
        .map(ext -> ext.withCommonStagedBuilder(getCommonStagedBuilder()))
        .map(ext -> ext.withCommonBuilderMethodPrefix(getCommonBuilderMethodPrefix()))
        .map(ext -> ext.withCommonClassMappings(getCommonClassMappings()))
        .map(ext -> ext.withCommonFormatTypeMappings(getCommonFormatTypeMappings()))
        .map(ext -> ext.withCommonEnumDescription(getCommonEnumDescription()))
        .map(ext -> ext.withCommonGetterSuffixes(getCommonGetterSuffixes()))
        .map(ext -> ext.withCommonValidation(getCommonValidation()))
        .map(ext -> ext.withCommonConstantSchemaNameMappings(getCommonConstantSchemaNameMappings()))
        .map(ext -> ext.withCommonWarnings(getCommonWarnings()))
        .map(ext -> ext.withCommonAllowNullableForEnums(getCommonAllowNullableForEnums()));
  }

  @Override
  public String toString() {
    return "OpenApiSchemaExtension{"
        + "schemaExtensions="
        + PList.fromIter(schemaExtensions)
        + ", sourceSet='"
        + sourceSet
        + '\''
        + ", outputDir='"
        + outputDir
        + '\''
        + ", suffix='"
        + suffix
        + '\''
        + ", jsonSupport='"
        + jsonSupport
        + '\''
        + ", xmlSupport='"
        + xmlSupport
        + '\''
        + ", stagedBuilder="
        + stagedBuilder
        + ", builderMethodPrefix='"
        + builderMethodPrefix
        + '\''
        + ", enumDescriptionExtension="
        + enumDescriptionExtension
        + ", classMappings="
        + classMappings
        + ", formatTypeMappings="
        + formatTypeMappings
        + ", dtoMappings="
        + dtoMappings
        + ", constantSchemaNameMappings="
        + constantSchemaNameMappings
        + ", getterSuffixes="
        + getterSuffixes
        + ", validation="
        + validation
        + ", warnings="
        + warnings
        + '}';
  }
}
