package com.github.muehmar.gradle.openapi.poc;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.github.muehmar.openapi.util.JacksonNullContainer;
import com.github.muehmar.openapi.util.Tristate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@JsonDeserialize(builder = UserDto.Builder.class)
public class UserDto {
  private final String id;
  private final String username;
  private final Integer age;
  private final String email;
  private final boolean isEmailNull;
  private final Map<String, Object> additionalProperties;

  public UserDto(
      String id,
      String username,
      Integer age,
      String email,
      boolean isEmailNull,
      Map<String, Object> additionalProperties) {
    this.id = id;
    this.username = username;
    this.age = age;
    this.email = email;
    this.isEmailNull = isEmailNull;
    this.additionalProperties = Collections.unmodifiableMap(additionalProperties);
  }

  @NotNull
  @Size(min = 2)
  public String getId() {
    return id;
  }

  @NotNull
  public String getUsername() {
    return username;
  }

  @JsonIgnore
  public Optional<Integer> getAgeOpt() {
    return Optional.ofNullable(age);
  }

  @JsonIgnore
  public Integer getAgeOr(Integer defaultValue) {
    return this.age == null ? defaultValue : this.age;
  }

  @JsonProperty("age")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Min(value = 18)
  @Max(value = 199)
  private Integer getAgeRaw() {
    return age;
  }

  @JsonIgnore
  public Tristate<String> getEmailTristate() {
    return Tristate.ofNullableAndNullFlag(email, isEmailNull);
  }

  @JsonProperty("email")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Object getEmailJackson() {
    return isEmailNull ? new JacksonNullContainer<>(email) : email;
  }

  @JsonIgnore
  private String getEmailRaw() {
    return email;
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

  /** Returns the number of present properties of this object. */
  @JsonIgnore
  public int getPropertyCount() {
    return (id != null ? 1 : 0)
        + (username != null ? 1 : 0)
        + (age != null ? 1 : 0)
        + ((isEmailNull || email != null) ? 1 : 0)
        + additionalProperties.size();
  }

  public UserDto withId(String id) {
    return new UserDto(id, username, age, email, isEmailNull, additionalProperties);
  }

  public UserDto withUsername(String username) {
    return new UserDto(id, username, age, email, isEmailNull, additionalProperties);
  }

  public UserDto withAge(Integer age) {
    return new UserDto(id, username, age, email, isEmailNull, additionalProperties);
  }

  public UserDto withAge(Optional<Integer> age) {
    return new UserDto(id, username, age.orElse(null), email, isEmailNull, additionalProperties);
  }

  public UserDto withEmail(String email) {
    return new UserDto(id, username, age, email, false, additionalProperties);
  }

  public UserDto withEmail(Tristate<String> email) {
    return new UserDto(
        id,
        username,
        age,
        email.onValue(val -> val).onNull(() -> null).onAbsent(() -> null),
        email.onValue(ignore -> false).onNull(() -> true).onAbsent(() -> false),
        additionalProperties);
  }

  boolean isValid() {
    return new Validator().isValid();
  }

  private class Validator {
    private boolean isIdValid() {
      if (id != null) {
        return 2 <= id.length();
      }

      return false;
    }

    private boolean isUsernameValid() {
      return username != null;
    }

    private boolean isAgeValid() {
      if (age != null) {
        return 18 <= age && age <= 199;
      }

      return true;
    }

    private boolean isEmailValid() {
      return true;
    }

    private boolean isAdditionalPropertiesValid() {
      if (getAdditionalProperties() != null) {
        return getAdditionalProperties().values().stream()
            .allMatch(this::isAdditionalPropertiesValueValid);
      }

      return false;
    }

    private boolean isAdditionalPropertiesValueValid(Object additionalPropertiesValue) {
      return true;
    }

    private boolean isValid() {
      return isIdValid()
          && isUsernameValid()
          && isAgeValid()
          && isEmailValid()
          && isAdditionalPropertiesValid();
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || this.getClass() != obj.getClass()) return false;
    final UserDto other = (UserDto) obj;
    return Objects.deepEquals(this.id, other.id)
        && Objects.deepEquals(this.username, other.username)
        && Objects.deepEquals(this.age, other.age)
        && Objects.deepEquals(this.email, other.email)
        && Objects.deepEquals(this.isEmailNull, other.isEmailNull)
        && Objects.deepEquals(this.additionalProperties, other.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, username, age, email, isEmailNull, additionalProperties);
  }

  @Override
  public String toString() {
    return "UserDto{"
        + "id="
        + "'"
        + id
        + "'"
        + ", "
        + "username="
        + "'"
        + username
        + "'"
        + ", "
        + "age="
        + age
        + ", "
        + "email="
        + "'"
        + email
        + "'"
        + ", "
        + "isEmailNull="
        + isEmailNull
        + ", "
        + "additionalProperties="
        + additionalProperties
        + "}";
  }

  @JsonPOJOBuilder(withPrefix = "set")
  public static final class Builder {

    private Builder() {}

    private String id;
    private String username;
    private Integer age;
    private String email;
    private boolean isEmailNull = false;
    private Map<String, Object> additionalProperties = new HashMap<>();

    @JsonProperty("id")
    private Builder setId(String id) {
      this.id = id;
      return this;
    }

    @JsonProperty("username")
    private Builder setUsername(String username) {
      this.username = username;
      return this;
    }

    @JsonProperty("age")
    public Builder setAge(Integer age) {
      this.age = age;
      return this;
    }

    public Builder setAge(Optional<Integer> age) {
      this.age = age.orElse(null);
      return this;
    }

    @JsonProperty("email")
    public Builder setEmail(String email) {
      this.email = email;
      this.isEmailNull = email == null;
      return this;
    }

    public Builder setEmail(Tristate<String> email) {
      this.email = email.onValue(val -> val).onNull(() -> null).onAbsent(() -> null);
      this.isEmailNull = email.onValue(ignore -> false).onNull(() -> true).onAbsent(() -> false);
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
      additionalProperties.remove("id");
      additionalProperties.remove("username");
      additionalProperties.remove("age");
      additionalProperties.remove("email");

      return new UserDto(id, username, age, email, isEmailNull, additionalProperties);
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
  public static FullPropertyBuilder0 fullUserDtoBuilder() {
    return new FullPropertyBuilder0(new Builder());
  }

  public static final class FullPropertyBuilder0 {
    private final Builder builder;

    private FullPropertyBuilder0(Builder builder) {
      this.builder = builder;
    }

    public FullPropertyBuilder1 setId(String id) {
      return new FullPropertyBuilder1(builder.setId(id));
    }
  }

  public static final class FullPropertyBuilder1 {
    private final Builder builder;

    private FullPropertyBuilder1(Builder builder) {
      this.builder = builder;
    }

    public FullOptPropertyBuilder0 setUsername(String username) {
      return new FullOptPropertyBuilder0(builder.setUsername(username));
    }
  }

  public static final class FullOptPropertyBuilder0 {
    private final Builder builder;

    private FullOptPropertyBuilder0(Builder builder) {
      this.builder = builder;
    }

    public FullOptPropertyBuilder1 setAge(Integer age) {
      return new FullOptPropertyBuilder1(builder.setAge(age));
    }

    public FullOptPropertyBuilder1 setAge(Optional<Integer> age) {
      return new FullOptPropertyBuilder1(builder.setAge(age));
    }
  }

  public static final class FullOptPropertyBuilder1 {
    private final Builder builder;

    private FullOptPropertyBuilder1(Builder builder) {
      this.builder = builder;
    }

    public FullOptPropertyBuilder2 setEmail(String email) {
      return new FullOptPropertyBuilder2(builder.setEmail(email));
    }

    public FullOptPropertyBuilder2 setEmail(Tristate<String> email) {
      return new FullOptPropertyBuilder2(builder.setEmail(email));
    }
  }

  public static final class FullOptPropertyBuilder2 {
    private final Builder builder;

    private FullOptPropertyBuilder2(Builder builder) {
      this.builder = builder;
    }

    public FullOptPropertyBuilder2 addAdditionalProperty(String key, Object value) {
      return new FullOptPropertyBuilder2(builder.addAdditionalProperty(key, value));
    }

    public FullOptPropertyBuilder2 setAdditionalProperties(
        Map<String, Object> additionalProperties) {
      return new FullOptPropertyBuilder2(builder.setAdditionalProperties(additionalProperties));
    }

    public UserDto build() {
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
  public static PropertyBuilder0 userDtoBuilder() {
    return new PropertyBuilder0(new Builder());
  }

  public static final class PropertyBuilder0 {
    private final Builder builder;

    private PropertyBuilder0(Builder builder) {
      this.builder = builder;
    }

    public PropertyBuilder1 setId(String id) {
      return new PropertyBuilder1(builder.setId(id));
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

    public OptPropertyBuilder1 setAge(Integer age) {
      return new OptPropertyBuilder1(builder.setAge(age));
    }

    public OptPropertyBuilder1 setAge(Optional<Integer> age) {
      return new OptPropertyBuilder1(builder.setAge(age));
    }
  }

  public static final class OptPropertyBuilder1 {
    private final Builder builder;

    private OptPropertyBuilder1(Builder builder) {
      this.builder = builder;
    }

    public OptPropertyBuilder2 setEmail(String email) {
      return new OptPropertyBuilder2(builder.setEmail(email));
    }

    public OptPropertyBuilder2 setEmail(Tristate<String> email) {
      return new OptPropertyBuilder2(builder.setEmail(email));
    }
  }

  public static final class OptPropertyBuilder2 {
    private final Builder builder;

    private OptPropertyBuilder2(Builder builder) {
      this.builder = builder;
    }

    public OptPropertyBuilder2 addAdditionalProperty(String key, Object value) {
      return new OptPropertyBuilder2(builder.addAdditionalProperty(key, value));
    }

    public OptPropertyBuilder2 setAdditionalProperties(Map<String, Object> additionalProperties) {
      return new OptPropertyBuilder2(builder.setAdditionalProperties(additionalProperties));
    }

    public UserDto build() {
      return builder.build();
    }
  }
}
