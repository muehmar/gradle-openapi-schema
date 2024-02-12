package com.github.muehmar.gradle.openapi.poc;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@JsonDeserialize(builder = StringAdditionalPropertiesDto.Builder.class)
public class StringAdditionalPropertiesDto {
  private final String name;
  private final Map<String, Object> additionalProperties;

  public StringAdditionalPropertiesDto(String name, Map<String, Object> additionalProperties) {
    this.name = name;
    this.additionalProperties = Collections.unmodifiableMap(additionalProperties);
  }

  @NotNull
  public String getName() {
    return name;
  }

  @JsonAnyGetter
  public Map<String, @Size(max = 10) @Pattern(regexp = "[A-Za-z0-9]+") String>
      getAdditionalProperties() {
    final Map<String, String> props = new HashMap<>();
    additionalProperties.forEach(
        (key, value) -> castAdditionalProperty(value).ifPresent(v -> props.put(key, v)));
    return props;
  }

  /**
   * Returns the additional property with {@code key} wrapped in an {@link Optional} if present,
   * {@link Optional#empty()} otherwise
   */
  public Optional<String> getAdditionalProperty(String key) {
    return Optional.ofNullable(additionalProperties.get(key)).flatMap(this::castAdditionalProperty);
  }

  private Optional<String> castAdditionalProperty(Object property) {
    try {
      return Optional.of((String) property);
    } catch (ClassCastException e) {
      return Optional.empty();
    }
  }

  /** Returns the number of present properties of this object. */
  @Min(2)
  @Max(4)
  @JsonIgnore
  public int getPropertyCount() {
    return (name != null ? 1 : 0) + additionalProperties.size();
  }

  public StringAdditionalPropertiesDto withName(String name) {
    return new StringAdditionalPropertiesDto(name, additionalProperties);
  }

  boolean isValid() {
    return new Validator().isValid();
  }

  private class Validator {
    private boolean isNameValid() {
      return name != null;
    }

    private boolean isAdditionalPropertiesValid() {
      if (getAdditionalProperties() != null) {
        return getAdditionalProperties().values().stream()
            .allMatch(this::isAdditionalPropertiesValueValid);
      }

      return false;
    }

    private boolean isAdditionalPropertiesValueValid(String additionalPropertiesValue) {
      if (additionalPropertiesValue != null) {
        return additionalPropertiesValue.length() <= 10
            && java.util.regex.Pattern.matches("[A-Za-z0-9]+", additionalPropertiesValue);
      }

      return true;
    }

    private boolean isValid() {
      return isNameValid()
          && isAllAdditionalPropertiesHaveCorrectType()
          && 2 <= getPropertyCount()
          && getPropertyCount() <= 4
          && isAdditionalPropertiesValid();
    }
  }

  @JsonIgnore
  @AssertTrue(message = "Not all additional properties are instances of String")
  private boolean isAllAdditionalPropertiesHaveCorrectType() {
    return getAdditionalProperties().size() == additionalProperties.size();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || this.getClass() != obj.getClass()) return false;
    final StringAdditionalPropertiesDto other = (StringAdditionalPropertiesDto) obj;
    return Objects.deepEquals(this.name, other.name)
        && Objects.deepEquals(this.additionalProperties, other.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, additionalProperties);
  }

  @Override
  public String toString() {
    return "StringAdditionalPropertiesDto{"
        + "name="
        + "'"
        + name
        + "'"
        + ", "
        + "additionalProperties="
        + additionalProperties
        + "}";
  }

  @JsonPOJOBuilder(withPrefix = "set")
  public static final class Builder {

    private Builder() {}

    private String name;
    private Map<String, Object> additionalProperties = new HashMap<>();

    @JsonProperty("name")
    private Builder setName(String name) {
      this.name = name;
      return this;
    }

    private Builder addAdditionalProperty(String key, Object value) {
      this.additionalProperties.put(key, value);
      return this;
    }

    @JsonAnySetter
    public Builder addAdditionalProperty(String key, String value) {
      this.additionalProperties.put(key, value);
      return this;
    }

    public Builder setAdditionalProperties(Map<String, String> additionalProperties) {
      this.additionalProperties = new HashMap<>(additionalProperties);
      return this;
    }

    public StringAdditionalPropertiesDto build() {
      additionalProperties.remove("name");

      return new StringAdditionalPropertiesDto(name, additionalProperties);
    }
  }

  /**
   * Instantiates a new staged builder. Explicit properties have precedence over additional
   * properties, i.e. an additional property with the same name as an explicit property will be
   * discarded.
   */
  public static FullPropertyBuilder0 fullBuilder() {
    return new FullPropertyBuilder0(new Builder());
  }

  /**
   * Instantiates a new staged builder. Explicit properties have precedence over additional
   * properties, i.e. an additional property with the same name as an explicit property will be
   * discarded.
   */
  public static FullPropertyBuilder0 fullStringAdditionalPropertiesDtoBuilder() {
    return new FullPropertyBuilder0(new Builder());
  }

  public static final class FullPropertyBuilder0 {
    private final Builder builder;

    private FullPropertyBuilder0(Builder builder) {
      this.builder = builder;
    }

    public FullOptPropertyBuilder0 setName(String name) {
      return new FullOptPropertyBuilder0(builder.setName(name));
    }
  }

  public static final class FullOptPropertyBuilder0 {
    private final Builder builder;

    private FullOptPropertyBuilder0(Builder builder) {
      this.builder = builder;
    }

    public FullOptPropertyBuilder0 addAdditionalProperty(String key, String value) {
      return new FullOptPropertyBuilder0(builder.addAdditionalProperty(key, value));
    }

    public FullOptPropertyBuilder0 setAdditionalProperties(
        Map<String, String> additionalProperties) {
      return new FullOptPropertyBuilder0(builder.setAdditionalProperties(additionalProperties));
    }

    public StringAdditionalPropertiesDto build() {
      return builder.build();
    }
  }

  /**
   * Instantiates a new staged builder. Explicit properties have precedence over additional
   * properties, i.e. an additional property with the same name as an explicit property will be
   * discarded.
   */
  public static PropertyBuilder0 builder() {
    return new PropertyBuilder0(new Builder());
  }

  /**
   * Instantiates a new staged builder. Explicit properties have precedence over additional
   * properties, i.e. an additional property with the same name as an explicit property will be
   * discarded.
   */
  public static PropertyBuilder0 stringAdditionalPropertiesDtoBuilder() {
    return new PropertyBuilder0(new Builder());
  }

  public static final class PropertyBuilder0 {
    private final Builder builder;

    private PropertyBuilder0(Builder builder) {
      this.builder = builder;
    }

    public PropertyBuilder1 setName(String name) {
      return new PropertyBuilder1(builder.setName(name));
    }
  }

  public static final class PropertyBuilder1 {
    private final Builder builder;

    private PropertyBuilder1(Builder builder) {
      this.builder = builder;
    }

    public OptPropertyBuilder0 andAllOptionals() {
      return new OptPropertyBuilder0(builder);
    }

    public Builder andOptionals() {
      return builder;
    }

    public StringAdditionalPropertiesDto build() {
      return builder.build();
    }
  }

  public static final class OptPropertyBuilder0 {
    private final Builder builder;

    private OptPropertyBuilder0(Builder builder) {
      this.builder = builder;
    }

    public OptPropertyBuilder0 addAdditionalProperty(String key, String value) {
      return new OptPropertyBuilder0(builder.addAdditionalProperty(key, value));
    }

    public OptPropertyBuilder0 setAdditionalProperties(Map<String, String> additionalProperties) {
      return new OptPropertyBuilder0(builder.setAdditionalProperties(additionalProperties));
    }

    public StringAdditionalPropertiesDto build() {
      return builder.build();
    }
  }
}
