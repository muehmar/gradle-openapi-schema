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
import org.gradle.api.model.ObjectFactory;

@EqualsAndHashCode
public class OpenApiSchemaExtension implements Serializable {
  private final NamedDomainObjectContainer<SingleSchemaExtension> schemaExtensions;

  private EnumDescriptionExtension enumDescriptionExtension = null;
  private final List<ClassMapping> classMappings;
  private final List<FormatTypeMapping> formatTypeMappings;
  private final List<ConstantSchemaNameMapping> constantSchemaNameMappings;
  private final GetterSuffixes getterSuffixes;
  private final ValidationMethods validationMethods;
  private final WarningsConfig warnings;

  @Inject
  public OpenApiSchemaExtension(ObjectFactory objectFactory) {
    this.schemaExtensions = objectFactory.domainObjectContainer(SingleSchemaExtension.class);
    this.classMappings = new ArrayList<>();
    this.formatTypeMappings = new ArrayList<>();
    this.constantSchemaNameMappings = new ArrayList<>();
    this.getterSuffixes = GetterSuffixes.allUndefined();
    this.validationMethods = ValidationMethods.allUndefined();
    this.warnings = WarningsConfig.allUndefined();
  }

  public void schemas(Closure<SingleSchemaExtension> closure) {
    schemaExtensions.configure(closure);
  }

  public void classMapping(Action<ClassMapping> action) {
    final ClassMapping classMapping = new ClassMapping();
    action.execute(classMapping);
    classMappings.add(classMapping);
  }

  public void formatTypeMapping(Action<FormatTypeMapping> action) {
    final FormatTypeMapping formatTypeMapping = new FormatTypeMapping();
    action.execute(formatTypeMapping);
    formatTypeMappings.add(formatTypeMapping);
  }

  public void enumDescriptionExtraction(Action<EnumDescriptionExtension> action) {
    enumDescriptionExtension = new EnumDescriptionExtension();
    action.execute(enumDescriptionExtension);
  }

  public void getterSuffixes(Action<GetterSuffixes> action) {
    action.execute(getterSuffixes);
  }

  public void validationMethods(Action<ValidationMethods> action) {
    action.execute(validationMethods);
  }

  public void constantSchemaNameMapping(Action<ConstantSchemaNameMapping> action) {
    final ConstantSchemaNameMapping constantSchemaNameMapping = new ConstantSchemaNameMapping();
    action.execute(constantSchemaNameMapping);
    constantSchemaNameMappings.add(constantSchemaNameMapping);
  }

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

  public GetterSuffixes getCommonGetterSuffixes() {
    return getterSuffixes;
  }

  public ValidationMethods getCommonValidationMethods() {
    return validationMethods;
  }

  private List<ConstantSchemaNameMapping> getCommonConstantSchemaNameMappings() {
    return constantSchemaNameMappings;
  }

  public WarningsConfig getCommonWarnings() {
    return warnings;
  }

  public PList<SingleSchemaExtension> getSchemaExtensions() {
    return PList.fromIter(schemaExtensions)
        .map(ext -> ext.withCommonClassMappings(getCommonClassMappings()))
        .map(ext -> ext.withCommonFormatTypeMappings(getCommonFormatTypeMappings()))
        .map(ext -> ext.withCommonEnumDescription(getCommonEnumDescription()))
        .map(ext -> ext.withCommonGetterSuffixes(getCommonGetterSuffixes()))
        .map(ext -> ext.withCommonValidationMethods(getCommonValidationMethods()))
        .map(ext -> ext.withCommonConstantSchemaNameMappings(getCommonConstantSchemaNameMappings()))
        .map(ext -> ext.withCommonWarnings(getCommonWarnings()));
  }

  @Override
  public String toString() {
    return "OpenApiSchemaExtension{"
        + "schemaExtensions="
        + PList.fromIter(schemaExtensions)
        + ", enumDescriptionExtension="
        + enumDescriptionExtension
        + ", classMappings="
        + classMappings
        + ", formatTypeMappings="
        + formatTypeMappings
        + ", constantSchemaNameMappings="
        + constantSchemaNameMappings
        + ", getterSuffixes="
        + getterSuffixes
        + ", validationMethods="
        + validationMethods
        + ", warnings="
        + warnings
        + '}';
  }
}
