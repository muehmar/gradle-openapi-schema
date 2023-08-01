package com.github.muehmar.gradle.openapi.addprop;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.constraints.NotNull;

@JsonDeserialize(builder = BaseDataDto.Builder.class)
public class BaseDataDto {
  private final ColorEnum color;
  private final Map<String, Object> additionalProperties;

  public BaseDataDto(ColorEnum color, Map<String, Object> additionalProperties) {
    this.color = color;
    this.additionalProperties = Collections.unmodifiableMap(additionalProperties);
  }

  public enum ColorEnum {
    GREEN("green", ""),
    YELLOW("yellow", ""),
    RED("red", "");

    private final String value;
    private final String description;

    ColorEnum(String value, String description) {
      this.value = value;
      this.description = description;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @JsonIgnore
    public String getDescription() {
      return description;
    }

    @Override
    public String toString() {
      return value;
    }

    @JsonCreator
    public static ColorEnum fromValue(String value) {
      for (ColorEnum e : ColorEnum.values()) {
        if (e.value.equals(value)) {
          return e;
        }
      }
      final String possibleValues =
          Stream.of(values()).map(ColorEnum::getValue).collect(Collectors.joining(", "));
      throw new IllegalArgumentException(
          "Unexpected value '"
              + value
              + "' for ColorEnum, possible values are ["
              + possibleValues
              + "]");
    }
  }

  @NotNull
  public ColorEnum getColor() {
    return color;
  }

  @JsonAnyGetter
  public Map<String, Object> getAdditionalProperties() {
    return additionalProperties;
  }

  /**
   * Returns the additional property with {@code key} wrapped in an {@link Optional} if present,
   * {@link Optional#empty()} otherwise
   */
  public Optional<Object> getAdditionalProperty(String key) {
    return Optional.ofNullable(additionalProperties.get(key));
  }

  public BaseDataDto withColor(ColorEnum color) {
    return new BaseDataDto(color, additionalProperties);
  }

  /** Returns the number of present properties of this object. */
  @JsonIgnore
  public int getPropertyCount() {
    return 1 + additionalProperties.size();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || this.getClass() != obj.getClass()) return false;
    final BaseDataDto other = (BaseDataDto) obj;
    return Objects.deepEquals(this.color, other.color)
        && Objects.deepEquals(this.additionalProperties, other.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(color, additionalProperties);
  }

  @Override
  public String toString() {
    return "BaseDataDto{"
        + "color="
        + color
        + ", "
        + "additionalProperties="
        + additionalProperties
        + "}";
  }

  @JsonPOJOBuilder(withPrefix = "set")
  public static final class Builder {

    private Builder() {}

    private ColorEnum color;
    private Map<String, Object> additionalProperties = new HashMap<>();

    @JsonProperty("color")
    private Builder setColor(ColorEnum color) {
      this.color = color;
      return this;
    }

    @JsonAnySetter
    public Builder addAdditionalProperty(String key, Object value) {
      this.additionalProperties.put(key, value);
      return this;
    }

    public Builder setAdditionalProperties(Map<String, Object> additionalProperties) {
      this.additionalProperties = new HashMap<>(additionalProperties);
      return this;
    }

    public BaseDataDto build() {
      return new BaseDataDto(color, additionalProperties);
    }
  }

  public static PropertyBuilder0 newBuilder() {
    return new PropertyBuilder0(new Builder());
  }

  public static final class PropertyBuilder0 {
    private final Builder builder;

    private PropertyBuilder0(Builder builder) {
      this.builder = builder;
    }

    public PropertyBuilder1 setColor(ColorEnum color) {
      return new PropertyBuilder1(builder.setColor(color));
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

    public BaseDataDto build() {
      return builder.build();
    }
  }

  public static final class OptPropertyBuilder0 {
    private final Builder builder;

    private OptPropertyBuilder0(Builder builder) {
      this.builder = builder;
    }

    public OptPropertyBuilder0 addAdditionalProperty(String key, Object value) {
      return new OptPropertyBuilder0(builder.addAdditionalProperty(key, value));
    }

    public OptPropertyBuilder0 setAdditionalProperties(Map<String, Object> additionalProperties) {
      return new OptPropertyBuilder0(builder.setAdditionalProperties(additionalProperties));
    }

    public BaseDataDto build() {
      return builder.build();
    }
  }
}
