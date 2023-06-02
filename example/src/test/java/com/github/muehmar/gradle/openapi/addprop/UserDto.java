package com.github.muehmar.gradle.openapi.addprop;

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

  private String getEmailRaw() {
    return email;
  }

  @JsonAnyGetter
  public Map<String, Object> getAdditionalProperties() {
    return additionalProperties;
  }

  /**
   * Returns the additional property with {@code key} wrapped in and {@link Optional} if present,
   * {@link Optional#empty()} otherwise
   */
  public Optional<Object> getAdditionalProperty(String key) {
    return Optional.ofNullable(additionalProperties.get(key));
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

  /** Returns the number of present properties of this object. */
  @JsonIgnore
  public int getPropertyCount() {
    return 1
        + 1
        + (age != null ? 1 : 0)
        + ((isEmailNull || email != null) ? 1 : 0)
        + additionalProperties.size();
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
      return new UserDto(id, username, age, email, isEmailNull, additionalProperties);
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

    public Builder2 setUsername(String username) {
      return new Builder2(builder.setUsername(username));
    }
  }

  public static final class Builder2 {
    private final Builder builder;

    private Builder2(Builder builder) {
      this.builder = builder;
    }

    public OptBuilder0 andAllOptionals() {
      return new OptBuilder0(builder);
    }

    public Builder andOptionals() {
      return builder;
    }

    public UserDto build() {
      return builder.build();
    }
  }

  public static final class OptBuilder0 {
    private final Builder builder;

    private OptBuilder0(Builder builder) {
      this.builder = builder;
    }

    public OptBuilder1 setAge(Integer age) {
      return new OptBuilder1(builder.setAge(age));
    }

    public OptBuilder1 setAge(Optional<Integer> age) {
      return new OptBuilder1(builder.setAge(age));
    }
  }

  public static final class OptBuilder1 {
    private final Builder builder;

    private OptBuilder1(Builder builder) {
      this.builder = builder;
    }

    public OptBuilder2 setEmail(String email) {
      return new OptBuilder2(builder.setEmail(email));
    }

    public OptBuilder2 setEmail(Tristate<String> email) {
      return new OptBuilder2(builder.setEmail(email));
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

    public UserDto build() {
      return builder.build();
    }
  }
}
