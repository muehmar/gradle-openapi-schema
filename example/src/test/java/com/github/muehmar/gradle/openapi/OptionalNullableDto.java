package com.github.muehmar.gradle.openapi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.Optional;

@JsonDeserialize(builder = OptionalNullableDto.Builder.class)
public class OptionalNullableDto {
  private final String prop1;
  private final String prop2;
  private final boolean isProp2Null;

  OptionalNullableDto(String prop1, String prop2, boolean isProp2Null) {
    this.prop1 = prop1;
    this.prop2 = prop2;
    this.isProp2Null = isProp2Null;
  }

  public String getProp1() {
    return prop1;
  }

  @JsonProperty("prop2")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Object getProp2Jackson() {
    return isProp2Null ? new JacksonNullContainer<>(prop2) : prop2;
  }

  @JsonIgnore
  public Optional<String> getProp2() {
    return Optional.ofNullable(prop2);
  }

  @JsonIgnore
  public boolean isProp2Null() {
    return isProp2Null;
  }

  @JsonPOJOBuilder(withPrefix = "")
  static class Builder {
    private String prop1;
    private String prop2;
    private boolean isProp2Null = false;

    private Builder() {}

    Builder prop1(String prop1) {
      this.prop1 = prop1;
      return this;
    }

    Builder prop2(String prop2) {
      this.prop2 = prop2;
      if (prop2 == null) {
        this.isProp2Null = true;
      }
      return this;
    }

    public OptionalNullableDto build() {
      return new OptionalNullableDto(prop1, prop2, isProp2Null);
    }
  }

  public static class JacksonNullContainer<T> {
    private final T value;

    public JacksonNullContainer(T value) {
      this.value = value;
    }

    @JsonValue
    public T getValue() {
      return value;
    }
  }
}
