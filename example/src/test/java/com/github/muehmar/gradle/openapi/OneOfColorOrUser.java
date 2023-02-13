package com.github.muehmar.gradle.openapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.github.muehmar.openapi.util.JacksonNullContainer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.validation.Valid;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;

@JsonDeserialize(builder = OneOfColorOrUser.Builder.class)
public class OneOfColorOrUser {
  private final Integer colorKey;
  private final String colorName;

  private final String id;
  private final String username;
  private final boolean isUsernamePresent;
  private final String email;
  private final String phone;
  private final boolean isPhoneNull;

  private final String propertyType;

  public OneOfColorOrUser(
      Integer colorKey,
      String colorName,
      String id,
      String username,
      boolean isUsernamePresent,
      String email,
      String phone,
      boolean isPhoneNull,
      String propertyType) {
    this.colorKey = colorKey;
    this.colorName = colorName;
    this.id = id;
    this.username = username;
    this.isUsernamePresent = isUsernamePresent;
    this.email = email;
    this.phone = phone;
    this.isPhoneNull = isPhoneNull;
    this.propertyType = propertyType;
  }

  @JsonProperty("colorKey")
  @JsonInclude(JsonInclude.Include.NON_NULL) // For required props
  private Integer getColorKey() {
    return colorKey;
  }

  @JsonProperty("colorName")
  @JsonInclude(JsonInclude.Include.NON_NULL) // For required props
  private String getColorName() {
    return colorName;
  }

  @JsonProperty("id")
  @JsonInclude(JsonInclude.Include.NON_NULL) // For required props
  private String getId() {
    return id;
  }

  @JsonProperty("username")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Object getUsername() {
    // Serialize requiredNullable only if is present
    return isUsernamePresent ? new JacksonNullContainer<>(username) : null;
  }

  @JsonProperty("email")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String getEmail() {
    return email;
  }

  @JsonProperty("phone")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Object getPhone() {
    // In case its not a user, phone will be null but the flag is false, thus its not serialized
    return isPhoneNull ? new JacksonNullContainer<>(phone) : phone;
  }

  @JsonProperty("propertyType")
  private String getPropertyType() {
    return propertyType;
  }

  public static OneOfColorOrUser fromColor(ColorDto2 color) {
    // Initialize all with null, flags all with false
    return new OneOfColorOrUser(
        color.getColorKey(), color.getColorName(), null, null, false, null, null, false, "color");
  }

  public static OneOfColorOrUser fromUser(UserDto2 user) {
    return new OneOfColorOrUser(
        null,
        null,
        user.getId(),
        user.getUsernameOr(null),
        true,
        user.getEmailOr(null),
        user.getPhoneTristate()
            .onValue(Function.identity())
            .onNull(() -> null)
            .onAbsent(() -> null),
        user.getPhoneTristate().onValue(ignore -> false).onNull(() -> true).onAbsent(() -> false),
        "user");
  }

  @Valid
  private Object getOneOf() {
    // Only needed for validation
    final long count = getValidCount();
    return fold(c -> c, u -> u, () -> null);
  }

  @AssertFalse(message = "Either Color or Flags must be present")
  private boolean isValidAgainstNoSchema() {
    final long count = getValidCount();
    return count == 0;
  }

  @AssertFalse(message = "Not a valid onOf as it matches more than one schema")
  private boolean isValidAgainstMoreThanOne() {
    final long count = getValidCount();
    return count > 1;
  }

  @AssertTrue(message = "Not valid against the schema described by propertyType")
  private boolean isValidAgainstTheCorrectSchema() {
    // Only needed for discriminator
    if (propertyType == null) {
      return false;
    }
    switch (propertyType) {
      case "user":
        return isValidAgainstUser();
      case "color":
        return isValidAgainstColor();
    }
    return false;
  }

  public <T> T fold(
      Function<ColorDto2, T> onColor, Function<UserDto2, T> onUserDto, Supplier<T> onInvalid) {
    if ("color".equals(propertyType) && isValidAgainstColor()) {
      return onColor.apply(asColor());
    } else if ("user".equals(propertyType) && isValidAgainstUser()) {
      return onUserDto.apply(asUser());
    } else {
      return onInvalid.get();
    }
  }

  private boolean isValidAgainstColor() {
    return colorKey != null && colorName != null;
  }

  private boolean isValidAgainstUser() {
    return id != null && isUsernamePresent;
  }

  private long getValidCount() {
    return Stream.of(isValidAgainstColor(), isValidAgainstUser()).filter(val -> val).count();
  }

  private ColorDto2 asColor() {
    return new ColorDto2(colorKey, colorName);
  }

  private UserDto2 asUser() {
    return new UserDto2(id, username, isUsernamePresent, email, phone, isPhoneNull);
  }

  @JsonPOJOBuilder(withPrefix = "set")
  public static final class Builder {

    private Builder() {}

    private Integer colorKey;
    private String colorName;

    private String id;
    private String username;
    private boolean isUsernamePresent = false;
    private String email;
    private String phone;
    private boolean isPhoneNull = false;

    private String propertyType;

    @JsonProperty("colorKey")
    private Builder setColorKey(Integer colorKey) {
      this.colorKey = colorKey;
      return this;
    }

    @JsonProperty("colorName")
    private Builder serColorName(String colorName) {
      this.colorName = colorName;
      return this;
    }

    @JsonProperty("id")
    private Builder setId(String id) {
      this.id = id;
      return this;
    }

    @JsonProperty("username")
    private Builder setUsername(String username) {
      this.username = username;
      this.isUsernamePresent = true;
      return this;
    }

    @JsonProperty("email")
    public Builder setEmail(String email) {
      this.email = email;
      return this;
    }

    @JsonProperty("phone")
    public Builder setPhone(String phone) {
      this.phone = phone;
      if (phone == null) {
        this.isPhoneNull = true;
      }
      return this;
    }

    @JsonProperty("propertyType")
    public Builder setPropertyType(String propertyType) {
      this.propertyType = propertyType;
      return this;
    }

    public OneOfColorOrUser build() {
      return new OneOfColorOrUser(
          colorKey,
          colorName,
          id,
          username,
          isUsernamePresent,
          email,
          phone,
          isPhoneNull,
          propertyType);
    }
  }
}
