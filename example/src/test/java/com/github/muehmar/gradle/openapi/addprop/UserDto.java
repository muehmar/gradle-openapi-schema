package com.github.muehmar.gradle.openapi.addprop;

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
import javax.validation.constraints.NotNull;

@JsonDeserialize(builder = UserDto.Builder.class)
public class UserDto {
  private final String type;
  private final String username;
  private final Map<String, Object> additionalProperties;

  public UserDto(String type, String username, Map<String, Object> additionalProperties) {
    this.type = type;
    this.username = username;
    this.additionalProperties = Collections.unmodifiableMap(additionalProperties);
  }

  @NotNull
  public String getType() {
    return type;
  }

  @NotNull
  public String getUsername() {
    return username;
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

  public UserDto withType(String type) {
    return new UserDto(type, username, additionalProperties);
  }

  public UserDto withUsername(String username) {
    return new UserDto(type, username, additionalProperties);
  }

  /** Returns the number of present properties of this object. */
  @JsonIgnore
  public int getPropertyCount() {
    return 1 + 1 + additionalProperties.size();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || this.getClass() != obj.getClass()) return false;
    final UserDto other = (UserDto) obj;
    return Objects.deepEquals(this.type, other.type)
        && Objects.deepEquals(this.username, other.username)
        && Objects.deepEquals(this.additionalProperties, other.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, username, additionalProperties);
  }

  @Override
  public String toString() {
    return "UserDto{"
        + "type="
        + "'"
        + type
        + "'"
        + ", "
        + "username="
        + "'"
        + username
        + "'"
        + ", "
        + "additionalProperties="
        + additionalProperties
        + "}";
  }

  @JsonPOJOBuilder(withPrefix = "set")
  public static final class Builder {

    private Builder() {}

    private String type;
    private String username;
    private Map<String, Object> additionalProperties = new HashMap<>();

    @JsonProperty("type")
    private Builder setType(String type) {
      this.type = type;
      return this;
    }

    @JsonProperty("username")
    private Builder setUsername(String username) {
      this.username = username;
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

    public UserDto build() {
      return new UserDto(type, username, additionalProperties);
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

    public PropertyBuilder1 setType(String type) {
      return new PropertyBuilder1(builder.setType(type));
    }
  }

  public static final class PropertyBuilder1 {
    private final Builder builder;

    private PropertyBuilder1(Builder builder) {
      this.builder = builder;
    }

    public PropertyBuilder2 setUsername(String username) {
      return new PropertyBuilder2(builder.setUsername(username));
    }
  }

  public static final class PropertyBuilder2 {
    private final Builder builder;

    private PropertyBuilder2(Builder builder) {
      this.builder = builder;
    }

    public OptPropertyBuilder0 andAllOptionals() {
      return new OptPropertyBuilder0(builder);
    }

    public Builder andOptionals() {
      return builder;
    }

    public UserDto build() {
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

    public UserDto build() {
      return builder.build();
    }
  }
}
