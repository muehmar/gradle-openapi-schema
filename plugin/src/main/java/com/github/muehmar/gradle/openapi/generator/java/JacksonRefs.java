package com.github.muehmar.gradle.openapi.generator.java;

public class JacksonRefs {
  private JacksonRefs() {}

  public static final String JSON_POJO_BUILDER =
      "com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder";
  public static final String JSON_DESERIALIZE =
      "com.fasterxml.jackson.databind.annotation.JsonDeserialize";
  public static final String JSON_VALUE = "com.fasterxml.jackson.annotation.JsonValue";
  public static final String JSON_CREATOR = "com.fasterxml.jackson.annotation.JsonCreator";
  public static final String JSON_IGNORE = "com.fasterxml.jackson.annotation.JsonIgnore";
  public static final String JSON_INCLUDE = "com.fasterxml.jackson.annotation.JsonInclude";
  public static final String JSON_PROPERTY = "com.fasterxml.jackson.annotation.JsonProperty";
}
