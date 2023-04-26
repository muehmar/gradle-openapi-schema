package com.github.muehmar.gradle.openapi.addprop;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/** */
@JsonDeserialize(builder = UserHobbiesDto.Builder.class)
public class UserHobbiesDto {
  private final String name;
  private final String description;
  private final Map<String, String> additionalProperties;

  public UserHobbiesDto(String name, String description, Map<String, String> additionalProperties) {
    this.name = name;
    this.description = description;
    this.additionalProperties = Collections.unmodifiableMap(additionalProperties);
  }

  /** */
  @NotNull
  public String getName() {
    return name;
  }

  /** */
  @JsonIgnore
  public Optional<String> getDescriptionOpt() {
    return Optional.ofNullable(description);
  }

  /** */
  @JsonIgnore
  public String getDescriptionOr(String defaultValue) {
    return this.description == null ? defaultValue : this.description;
  }

  @JsonProperty("description")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String getDescriptionRaw() {
    return description;
  }

  /** */
  public UserHobbiesDto withName(String name) {
    return new UserHobbiesDto(name, description, additionalProperties);
  }

  /** */
  public UserHobbiesDto withDescription(String description) {
    return new UserHobbiesDto(name, description, additionalProperties);
  }

  /** */
  public UserHobbiesDto withAdditionalProperties(Map<String, String> additionalProperties) {
    return new UserHobbiesDto(name, description, additionalProperties);
  }

  @JsonAnyGetter
  public Map<String, String> getAdditionalProperties() {
    return additionalProperties;
  }

  /**
   * Returns the additional property with {@code key} wrapped in and {@link Optional} if present,
   * {@link Optional#empty()} otherwise
   */
  public Optional<String> getAdditionalProperty(String key) {
    return Optional.ofNullable(additionalProperties.get(key));
  }

  /** Returns the number of present properties of this object. */
  @JsonIgnore
  @Min(2)
  @Max(3)
  public int getPropertyCount() {
    return 1 + (description != null ? 1 : 0) + additionalProperties.size();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || this.getClass() != obj.getClass()) return false;
    final UserHobbiesDto other = (UserHobbiesDto) obj;
    return Objects.equals(this.name, other.name)
        && Objects.equals(this.description, other.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description);
  }

  @Override
  public String toString() {
    return "UserHobbiesDto{"
        + "name='"
        + name
        + '\''
        + ", description='"
        + description
        + '\''
        + ", additionalProperties="
        + additionalProperties
        + '}';
  }

  @JsonPOJOBuilder(withPrefix = "set")
  public static final class Builder {

    private Builder() {}

    private String name;
    private String description;
    private Map<String, String> additionalProperties = new HashMap<>();

    /** */
    @JsonProperty("name")
    private Builder setName(String name) {
      this.name = name;
      return this;
    }

    /** */
    @JsonProperty("description")
    public Builder setDescription(String description) {
      this.description = description;
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

    /** */
    public Builder setDescription(Optional<String> description) {
      this.description = description.orElse(null);
      return this;
    }

    public UserHobbiesDto build() {
      return new UserHobbiesDto(name, description, additionalProperties);
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

    /** */
    public Builder1 setName(String name) {
      return new Builder1(builder.setName(name));
    }
  }

  public static final class Builder1 {
    private final Builder builder;

    private Builder1(Builder builder) {
      this.builder = builder;
    }

    public OptBuilder0 andAllOptionals() {
      return new OptBuilder0(builder);
    }

    public Builder andOptionals() {
      return builder;
    }

    public UserHobbiesDto build() {
      return builder.build();
    }
  }

  public static final class OptBuilder0 {
    private final Builder builder;

    private OptBuilder0(Builder builder) {
      this.builder = builder;
    }

    /** */
    public OptBuilder1 setDescription(String description) {
      return new OptBuilder1(builder.setDescription(description));
    }

    /** */
    public OptBuilder1 setDescription(Optional<String> description) {
      return new OptBuilder1(description.map(builder::setDescription).orElse(builder));
    }
  }

  public static final class OptBuilder1 {
    private final Builder builder;

    private OptBuilder1(Builder builder) {
      this.builder = builder;
    }

    public OptBuilder1 addAdditionalProperty(String key, String value) {
      return new OptBuilder1(builder.addAdditionalProperty(key, value));
    }

    public OptBuilder1 setAdditionalProperties(Map<String, String> additionalProperties) {
      return new OptBuilder1(builder.setAdditionalProperties(additionalProperties));
    }

    public UserHobbiesDto build() {
      return builder.build();
    }
  }
}
