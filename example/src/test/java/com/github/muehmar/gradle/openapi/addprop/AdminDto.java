package com.github.muehmar.gradle.openapi.addprop;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonDeserialize(builder = AdminDto.Builder.class)
public class AdminDto {
  private final String id;
  private final String adminname;
  private final Long level;
  private final ColorEnum color;
  private final Map<String, Object> additionalProperties;

  public AdminDto(
      String id,
      String adminname,
      Long level,
      ColorEnum color,
      Map<String, Object> additionalProperties
    ) {
    this.id = id;
    this.adminname = adminname;
    this.level = level;
    this.color = color;
    this.additionalProperties = Collections.unmodifiableMap(additionalProperties);
  }

  public enum ColorEnum {
    YELLOW("yellow", ""),
    ORANGE("orange", ""),
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
      for (ColorEnum e: ColorEnum.values()) {
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
  @Size(min = 2)
  public String getId() {
    return id;
  }

  @NotNull
  public String getAdminname() {
    return adminname;
  }

  @JsonIgnore
  public Optional<Long> getLevelOpt() {
    return Optional.ofNullable(level);
  }

  @JsonIgnore
  public Long getLevelOr(Long defaultValue) {
    return this.level == null ? defaultValue : this.level;
  }

  @JsonProperty("level")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Min(value = 1)
  private Long getLevelRaw() {
    return level;
  }

  @JsonIgnore
  public Optional<ColorEnum> getColorOpt() {
    return Optional.ofNullable(color);
  }

  @JsonIgnore
  public ColorEnum getColorOr(ColorEnum defaultValue) {
    return this.color == null ? defaultValue : this.color;
  }

  @JsonProperty("color")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private ColorEnum getColorRaw() {
    return color;
  }

  @JsonAnyGetter
  public Map<String, Object> getAdditionalProperties() {
    return additionalProperties;
  }

  /**
   * Returns the additional property with {@code key} wrapped in and {@link
   * Optional} if present, {@link Optional#empty()} otherwise
   */
  public Optional<Object> getAdditionalProperty(String key) {
    return Optional.ofNullable(additionalProperties.get(key));
  }

  public AdminDto withId(String id) {
    return new AdminDto(id, adminname, level, color, additionalProperties);
  }

  public AdminDto withAdminname(String adminname) {
    return new AdminDto(id, adminname, level, color, additionalProperties);
  }

  public AdminDto withLevel(Long level) {
    return new AdminDto(id, adminname, level, color, additionalProperties);
  }

  public AdminDto withLevel(Optional<Long> level) {
    return new AdminDto(id, adminname, level.orElse(null), color, additionalProperties);
  }

  public AdminDto withColor(ColorEnum color) {
    return new AdminDto(id, adminname, level, color, additionalProperties);
  }

  public AdminDto withColor(Optional<ColorEnum> color) {
    return new AdminDto(id, adminname, level, color.orElse(null), additionalProperties);
  }

  /**
   * Returns the number of present properties of this object.
   */
  @JsonIgnore
  public int getPropertyCount() {
    return
      1 +
      1 +
      (level != null ? 1 : 0) +
      (color != null ? 1 : 0) +
      additionalProperties.size();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || this.getClass() != obj.getClass()) return false;
    final AdminDto other = (AdminDto) obj;
    return Objects.deepEquals(this.id, other.id)
        && Objects.deepEquals(this.adminname, other.adminname)
        && Objects.deepEquals(this.level, other.level)
        && Objects.deepEquals(this.color, other.color)
        && Objects.deepEquals(this.additionalProperties, other.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
      id,
      adminname,
      level,
      color,
      additionalProperties
    );
  }

  @Override
  public String toString() {
    return "AdminDto{" +
      "id=" + "'" + id + "'" + ", " +
      "adminname=" + "'" + adminname + "'" + ", " +
      "level=" + level + ", " +
      "color=" + color + ", " +
      "additionalProperties=" + additionalProperties +
      "}";
  }

  @JsonPOJOBuilder(withPrefix = "set")
  public static final class Builder {

    private Builder() {
    }

    private String id;
    private String adminname;
    private Long level;
    private ColorEnum color;
    private Map<String, Object> additionalProperties = new HashMap<>();

    @JsonProperty("id")
    private Builder setId(String id) {
      this.id = id;
      return this;
    }

    @JsonProperty("adminname")
    private Builder setAdminname(String adminname) {
      this.adminname = adminname;
      return this;
    }

    @JsonProperty("level")
    public Builder setLevel(Long level) {
      this.level = level;
      return this;
    }

    public Builder setLevel(Optional<Long> level) {
      this.level = level.orElse(null);
      return this;
    }

    @JsonProperty("color")
    public Builder setColor(ColorEnum color) {
      this.color = color;
      return this;
    }

    public Builder setColor(Optional<ColorEnum> color) {
      this.color = color.orElse(null);
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

    public AdminDto build() {
      return new AdminDto(id, adminname, level, color, additionalProperties);
    }
  }

  public static Builder0 newBuilder() {
    return new Builder0(new Builder());
  }

  public static final class Builder0 {
    private final Builder builder;

    private Builder0(Builder builder) {
      this.builder = builder;
    }

    public Builder1 setId(String id) {
      return new Builder1(builder.setId(id));
    }

  }

  public static final class Builder1 {
    private final Builder builder;

    private Builder1(Builder builder) {
      this.builder = builder;
    }

    public Builder2 setAdminname(String adminname) {
      return new Builder2(builder.setAdminname(adminname));
    }

  }

  public static final class Builder2 {
    private final Builder builder;

    private Builder2(Builder builder) {
      this.builder = builder;
    }

    public OptBuilder0 andAllOptionals(){
      return new OptBuilder0(builder);
    }

    public Builder andOptionals(){
      return builder;
    }

    public AdminDto build(){
      return builder.build();
    }
  }

  public static final class OptBuilder0 {
    private final Builder builder;

    private OptBuilder0(Builder builder) {
      this.builder = builder;
    }

    public OptBuilder1 setLevel(Long level) {
      return new OptBuilder1(builder.setLevel(level));
    }

    public OptBuilder1 setLevel(Optional<Long> level) {
      return new OptBuilder1(builder.setLevel(level));
    }

  }

  public static final class OptBuilder1 {
    private final Builder builder;

    private OptBuilder1(Builder builder) {
      this.builder = builder;
    }

    public OptBuilder2 setColor(ColorEnum color) {
      return new OptBuilder2(builder.setColor(color));
    }

    public OptBuilder2 setColor(Optional<ColorEnum> color) {
      return new OptBuilder2(builder.setColor(color));
    }

  }

  public static final class OptBuilder2 {
    private final Builder builder;

    private OptBuilder2(Builder builder) {
      this.builder = builder;
    }

    public OptBuilder2 addAdditionalProperty(String key, Object value) {
      return new OptBuilder2(builder.addAdditionalProperty(key, value));
    }

    public OptBuilder2 setAdditionalProperties(Map<String, Object> additionalProperties) {
      return new OptBuilder2(builder.setAdditionalProperties(additionalProperties));
    }

    public AdminDto build(){
      return builder.build();
    }
  }
}