package com.github.muehmar.gradle.openapi.dsl;

import ch.bluecare.commons.data.PList;
import groovy.lang.Closure;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.model.ObjectFactory;

public class OpenApiSchemaExtension implements Serializable {
  private final NamedDomainObjectContainer<SingleSchemaExtension> schemaExtensions;

  private EnumDescriptionExtension enumDescriptionExtension = null;
  private final List<ClassMapping> classMappings;
  private final List<FormatTypeMapping> formatTypeMappings;

  @Inject
  public OpenApiSchemaExtension(ObjectFactory objectFactory) {
    this.schemaExtensions = objectFactory.domainObjectContainer(SingleSchemaExtension.class);
    this.classMappings = new ArrayList<>();
    this.formatTypeMappings = new ArrayList<>();
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

  private Optional<EnumDescriptionExtension> getCommonEnumDescription() {
    return Optional.ofNullable(enumDescriptionExtension);
  }

  private PList<ClassMapping> getCommonClassMappings() {
    return PList.fromIter(classMappings);
  }

  private PList<FormatTypeMapping> getCommonFormatTypeMappings() {
    return PList.fromIter(formatTypeMappings);
  }

  public PList<SingleSchemaExtension> getSchemaExtensions() {
    return PList.fromIter(schemaExtensions)
        .map(ext -> ext.withCommonClassMappings(getCommonClassMappings()))
        .map(ext -> ext.withCommonFormatTypeMappings(getCommonFormatTypeMappings()))
        .map(ext -> ext.withCommonEnumDescription(getCommonEnumDescription()));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OpenApiSchemaExtension that = (OpenApiSchemaExtension) o;
    return Objects.equals(schemaExtensions, that.schemaExtensions)
        && Objects.equals(enumDescriptionExtension, that.enumDescriptionExtension)
        && Objects.equals(classMappings, that.classMappings)
        && Objects.equals(formatTypeMappings, that.formatTypeMappings);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        schemaExtensions, enumDescriptionExtension, classMappings, formatTypeMappings);
  }

  @Override
  public String toString() {
    return "OpenApiSchemaGenExtension{"
        + "schemaGenExtensions="
        + PList.fromIter(schemaExtensions)
        + ", enumDescriptionExtension="
        + enumDescriptionExtension
        + ", classMappings="
        + classMappings
        + ", formatTypeMappings="
        + formatTypeMappings
        + '}';
  }
}
