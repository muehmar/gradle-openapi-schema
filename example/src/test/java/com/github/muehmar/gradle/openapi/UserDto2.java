package com.github.muehmar.gradle.openapi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.github.muehmar.openapi.util.JacksonNullContainer;
import com.github.muehmar.openapi.util.Tristate;
import java.util.Objects;
import java.util.Optional;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/** */
@JsonDeserialize(builder = UserDto2.Builder.class)
public class UserDto2 {
  private final String id;
  private final String username;
  private final boolean isUsernamePresent;
  private final String email;
  private final String phone;
  private final boolean isPhoneNull;

  public UserDto2(
      String id,
      String username,
      boolean isUsernamePresent,
      String email,
      String phone,
      boolean isPhoneNull) {
    this.id = id;
    this.username = username;
    this.isUsernamePresent = isUsernamePresent;
    this.email = email;
    this.phone = phone;
    this.isPhoneNull = isPhoneNull;
  }

  /** */
  @NotNull
  @Size(min = 6, max = 10)
  public String getId() {
    return id;
  }

  /** */
  @JsonIgnore
  public Optional<String> getUsernameOpt() {
    return Optional.ofNullable(username);
  }

  /** */
  @JsonIgnore
  public String getUsernameOr(String defaultValue) {
    return username == null ? defaultValue : username;
  }

  @Size(min = 5, max = 20)
  @JsonProperty("username")
  private String getUsernameRaw() {
    return username;
  }

  @AssertTrue(message = "username is required but it is not present")
  private boolean isUsernamePresent() {
    return isUsernamePresent;
  }

  /** */
  @JsonIgnore
  public Optional<String> getEmailOpt() {
    return Optional.ofNullable(email);
  }

  /** */
  @JsonIgnore
  public String getEmailOr(String defaultValue) {
    return email == null ? defaultValue : email;
  }

  @JsonProperty("email")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Pattern(regexp = "[A-Za-z0-9]+@[A-Za-z0-9]+\\.[a-z]+")
  private String getEmailRaw() {
    return email;
  }

  /** */
  @JsonIgnore
  public Tristate<String> getPhoneTristate() {
    return Tristate.ofNullableAndNullFlag(phone, isPhoneNull);
  }

  @JsonProperty("phone")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Object getPhoneJackson() {
    return isPhoneNull ? new JacksonNullContainer<>(phone) : phone;
  }

  @Pattern(regexp = "\\+41[0-9]{7}")
  private String getPhoneRaw() {
    return phone;
  }

  /** */
  public UserDto2 withId(String id) {
    return new UserDto2(id, username, isUsernamePresent, email, phone, isPhoneNull);
  }

  /** */
  public UserDto2 withUsername(String username) {
    return new UserDto2(id, username, isUsernamePresent, email, phone, isPhoneNull);
  }

  /** */
  public UserDto2 withEmail(String email) {
    return new UserDto2(id, username, isUsernamePresent, email, phone, isPhoneNull);
  }

  /** */
  public UserDto2 withPhone(String phone) {
    return new UserDto2(id, username, isUsernamePresent, email, phone, isPhoneNull);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || this.getClass() != obj.getClass()) return false;
    final UserDto2 other = (UserDto2) obj;
    return Objects.equals(id, other.id)
        && Objects.equals(username, other.username)
        && Objects.equals(isUsernamePresent, other.isUsernamePresent)
        && Objects.equals(email, other.email)
        && Objects.equals(phone, other.phone)
        && Objects.equals(isPhoneNull, other.isPhoneNull);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, username, isUsernamePresent, email, phone, isPhoneNull);
  }

  @Override
  public String toString() {
    return "UserDto{"
        + "id="
        + id
        + ", username="
        + username
        + ", isUsernamePresent="
        + isUsernamePresent
        + ", email="
        + email
        + ", phone="
        + phone
        + ", isPhoneNull="
        + isPhoneNull
        + "}";
  }

  @JsonPOJOBuilder(withPrefix = "set")
  public static final class Builder {

    private Builder() {}

    private String id;
    private String username;
    private boolean isUsernamePresent = false;
    private String email;
    private String phone;
    private boolean isPhoneNull = false;

    /** */
    @JsonProperty("id")
    private Builder setId(String id) {
      this.id = id;
      return this;
    }

    /** */
    @JsonProperty("username")
    private Builder setUsername(String username) {
      this.username = username;
      this.isUsernamePresent = true;
      return this;
    }

    /** */
    private Builder setUsername(Optional<String> username) {
      this.username = username.orElse(null);
      this.isUsernamePresent = true;
      return this;
    }

    /** */
    @JsonProperty("email")
    public Builder setEmail(String email) {
      this.email = email;
      return this;
    }

    /** */
    public Builder setEmail(Optional<String> email) {
      this.email = email.orElse(null);
      return this;
    }

    /** */
    @JsonProperty("phone")
    public Builder setPhone(String phone) {
      this.phone = phone;
      if (phone == null) {
        this.isPhoneNull = true;
      }
      return this;
    }

    /** */
    public Builder setPhone(Tristate<String> phone) {
      this.phone = phone.onValue(val -> val).onNull(() -> null).onAbsent(() -> null);
      this.isPhoneNull = phone.onValue(ignore -> false).onNull(() -> true).onAbsent(() -> false);
      return this;
    }

    public UserDto2 build() {
      return new UserDto2(id, username, isUsernamePresent, email, phone, isPhoneNull);
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
    public Builder1 setId(String id) {
      return new Builder1(builder.setId(id));
    }
  }

  public static final class Builder1 {
    private final Builder builder;

    private Builder1(Builder builder) {
      this.builder = builder;
    }

    /** */
    public Builder2 setUsername(String username) {
      return new Builder2(builder.setUsername(username));
    }

    /** */
    public Builder2 setUsername(Optional<String> username) {
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

    public UserDto2 build() {
      return builder.build();
    }
  }

  public static final class OptBuilder0 {
    private final Builder builder;

    private OptBuilder0(Builder builder) {
      this.builder = builder;
    }

    /** */
    public OptBuilder1 setEmail(String email) {
      return new OptBuilder1(builder.setEmail(email));
    }

    /** */
    public OptBuilder1 setEmail(Optional<String> email) {
      return new OptBuilder1(email.map(builder::setEmail).orElse(builder));
    }
  }

  public static final class OptBuilder1 {
    private final Builder builder;

    private OptBuilder1(Builder builder) {
      this.builder = builder;
    }

    /** */
    public OptBuilder2 setPhone(String phone) {
      return new OptBuilder2(builder.setPhone(phone));
    }

    /** */
    public OptBuilder2 setPhone(Tristate<String> phone) {
      return new OptBuilder2(builder.setPhone(phone));
    }
  }

  public static final class OptBuilder2 {
    private final Builder builder;

    private OptBuilder2(Builder builder) {
      this.builder = builder;
    }

    public UserDto2 build() {
      return builder.build();
    }
  }
}
