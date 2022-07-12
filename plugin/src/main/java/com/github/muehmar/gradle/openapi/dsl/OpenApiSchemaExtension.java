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
  private final GetterSuffixes getterSuffixes;
  private final ValidationGetter validationGetter;

  @Inject
  public OpenApiSchemaExtension(ObjectFactory objectFactory) {
    this.schemaExtensions = objectFactory.domainObjectContainer(SingleSchemaExtension.class);
    this.classMappings = new ArrayList<>();
    this.formatTypeMappings = new ArrayList<>();
    this.getterSuffixes = GetterSuffixes.allUndefined();
    this.validationGetter = ValidationGetter.allUndefined();
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

  public void validationGetter(Action<ValidationGetter> action) {
    action.execute(validationGetter);
  }

  private Optional<EnumDescriptionExtension> getCommonEnumDescription() {
    return Optional.ofNullable(enumDescriptionExtension);
  }

  private PList<ClassMapping> getCommonClassMappings() {
    return PList.fromIter(classMappings);
  }

  private PList<FormatTypeMapping> getCommonFormatTypeMappings() {
    return PList.fromIter(formatTypeMappings);
  }

  public GetterSuffixes getCommonGetterSuffixes() {
    return getterSuffixes;
  }

  public ValidationGetter getCommonValidationGetter() {
    return validationGetter;
  }

  public PList<SingleSchemaExtension> getSchemaExtensions() {
    return PList.fromIter(schemaExtensions)
        .map(ext -> ext.withCommonClassMappings(getCommonClassMappings()))
        .map(ext -> ext.withCommonFormatTypeMappings(getCommonFormatTypeMappings()))
        .map(ext -> ext.withCommonEnumDescription(getCommonEnumDescription()))
        .map(ext -> ext.withCommonGetterSuffixes(getCommonGetterSuffixes()))
        .map(ext -> ext.withCommonValidationGetter(getCommonValidationGetter()));
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
        + ", getterSuffixes="
        + getterSuffixes
        + ", validationGetter="
        + validationGetter
        + '}';
  }
}
