package com.github.muehmar.gradle.openapi.generator.model;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class PojoName {
  private final Name name;
  private final SchemaName schemaName;
  private final String suffix;

  PojoName(Name name, SchemaName schemaName, String suffix) {
    this.name = name;
    this.schemaName = schemaName;
    this.suffix = suffix;
  }

  public static PojoName ofName(Name name) {
    return new PojoName(name, SchemaName.ofName(name), "");
  }

  public static PojoName ofNameAndSuffix(Name name, String suffix) {
    return new PojoName(name, SchemaName.ofName(name), suffix);
  }

  public static PojoName ofNameAndSuffix(String name, String suffix) {
    return new PojoName(Name.ofString(name), SchemaName.ofString(name), suffix);
  }

  public static PojoName deriveOpenApiPojoName(PojoName pojoName, Name pojoMemberName) {
    final Name name = pojoName.getName().startUpperCase().append(pojoMemberName.startUpperCase());
    final SchemaName newSchemaName =
        SchemaName.ofName(pojoName.getSchemaName().asName().append(".").append(pojoMemberName));
    return new PojoName(name, newSchemaName, pojoName.getSuffix());
  }

  public PojoName appendToName(String append) {
    return new PojoName(name.append(append), schemaName, suffix);
  }

  public PojoName prependSuffix(String prependText) {
    return new PojoName(name, schemaName, prependText + suffix);
  }

  public boolean equalsIgnoreCase(PojoName other) {
    return name.equalsIgnoreCase(other.name) && suffix.equals(other.suffix);
  }

  public PojoName startUppercase() {
    return new PojoName(name.startUpperCase(), schemaName, suffix);
  }

  public Name getName() {
    return name;
  }

  public SchemaName getSchemaName() {
    return schemaName;
  }

  public String getSuffix() {
    return suffix;
  }

  public String asString() {
    return name.append(suffix).asString();
  }

  @Override
  public String toString() {
    return "PojoName{"
        + "name="
        + name
        + ", schemaName="
        + schemaName
        + ", suffix='"
        + suffix
        + '\''
        + '}';
  }
}
