package com.github.muehmar.gradle.openapi.addprop;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.github.muehmar.openapi.util.JacksonNullContainer;
import com.github.muehmar.openapi.util.Tristate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.NotNull;

@JsonDeserialize(builder = AdminOrUserDto.Builder.class)
public class AdminOrUserDto {
  private final String name;
  private final String id;
  private final String adminname;
  private final Long level;
  private final AdminDto.ColorEnum color;
  private final String username;
  private final Integer age;
  private final String email;
  private final boolean isEmailNull;
  private final Map<String, Object> additionalProperties;

  public AdminOrUserDto(
      String name,
      String id,
      String adminname,
      Long level,
      AdminDto.ColorEnum color,
      String username,
      Integer age,
      String email,
      boolean isEmailNull,
      Map<String, Object> additionalProperties) {
    this.name = name;
    this.id = id;
    this.adminname = adminname;
    this.level = level;
    this.color = color;
    this.username = username;
    this.age = age;
    this.email = email;
    this.isEmailNull = isEmailNull;
    this.additionalProperties = Collections.unmodifiableMap(additionalProperties);
  }
  @NotNull
  private String getName() {
    return name;
  }

  @JsonProperty("id")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Object getId() {
    return id;
  }

  @JsonProperty("adminname")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Object getAdminname() {
    return adminname;
  }

  @JsonProperty("level")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Object getLevel() {
    return level;
  }

  @JsonProperty("color")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Object getColor() {
    return color;
  }

  @JsonProperty("username")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Object getUsername() {
    return username;
  }

  @JsonProperty("age")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Object getAge() {
    return age;
  }

  @JsonProperty("email")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Object getEmail() {
    return isEmailNull ? new JacksonNullContainer<>(email) : email;
  }

  @Valid
  @JsonIgnore
  private List<Object> getAnyOf() {
    if (getValidCount() == 0) {
      return null;
    }
    return fold(dto -> dto, dto -> dto);
  }

  private int getValidCount() {
    return (isValidAgainstAdminDto() ? 1 : 0) + (isValidAgainstUserDto() ? 1 : 0);
  }

  private boolean isValidAgainstAdminDto() {
    return id != null && adminname != null;
  }

  private boolean isValidAgainstUserDto() {
    return id != null && username != null;
  }

  @AssertFalse(message = "Is not valid against one of the schemas [Admin, User]")
  @JsonIgnore
  private boolean isValidAgainstNoSchema() {
    return getValidCount() == 0;
  }

  /**
   * Folds this instance using the given mapping functions for the DTO's. All mapping functions gets
   * executed with its corresponding DTO as input if this instance is valid against the
   * corresponding schema and the results are returned in a list. The order of the elements in the
   * returned list is deterministic: The order corresponds to the order of the mapping function
   * arguments, i.e. the result of the first mapping function will always be at the first position
   * in the list (if the function gets executed).<br>
   * <br>
   * I.e. if the JSON was valid against the schema 'Admin', the mapping method {@code onAdminDto}
   * gets executed with the {@link AdminDto} as argument.<br>
   * <br>
   * This method assumes this instance is either manually or automatically validated, i.e. the JSON
   * is valid against at least one of the schemas. If it is valid against no schema, it will simply
   * return an empty list.
   */
  public <T> List<T> fold(Function<AdminDto, T> onAdminDto, Function<UserDto, T> onUserDto) {
    final List<T> result = new ArrayList<>();
    if (isValidAgainstAdminDto()) {
      result.add(onAdminDto.apply(asAdminDto()));
    }
    if (isValidAgainstUserDto()) {
      result.add(onUserDto.apply(asUserDto()));
    }
    return result;
  }

  private AdminDto asAdminDto() {
    return new AdminDto(id, adminname, level, color, additionalProperties);
  }

  private UserDto asUserDto() {
    return new UserDto(id, username, age, email, isEmailNull, additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id, adminname, level, color, username, age, email, isEmailNull, additionalProperties);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || this.getClass() != obj.getClass()) return false;
    final AdminOrUserDto other = (AdminOrUserDto) obj;
    return Objects.deepEquals(this.id, other.id)
        && Objects.deepEquals(this.adminname, other.adminname)
        && Objects.deepEquals(this.level, other.level)
        && Objects.deepEquals(this.color, other.color)
        && Objects.deepEquals(this.username, other.username)
        && Objects.deepEquals(this.age, other.age)
        && Objects.deepEquals(this.email, other.email)
        && Objects.deepEquals(this.isEmailNull, other.isEmailNull)
        && Objects.deepEquals(this.additionalProperties, other.additionalProperties);
  }

  @Override
  public String toString() {
    return "AdminOrUserDto{"
        + "id="
        + "'"
        + id
        + "'"
        + ", "
        + "adminname="
        + "'"
        + adminname
        + "'"
        + ", "
        + "level="
        + level
        + ", "
        + "color="
        + color
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

    private String name;
    private String id;
    private String adminname;
    private Long level;
    private AdminDto.ColorEnum color;
    private String username;
    private Integer age;
    private String email;
    private boolean isEmailNull = false;
    private Map<String, Object> additionalProperties = new HashMap<>();

    @JsonProperty("name")
    private Builder setName(String name) {
      this.name = name;
      return this;
    }

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
    private Builder setLevel(Long level) {
      this.level = level;
      return this;
    }

    private Builder setLevel(Optional<Long> level) {
      this.level = level.orElse(null);
      return this;
    }

    @JsonProperty("color")
    private Builder setColor(AdminDto.ColorEnum color) {
      this.color = color;
      return this;
    }

    private Builder setColor(Optional<AdminDto.ColorEnum> color) {
      this.color = color.orElse(null);
      return this;
    }

    @JsonProperty("username")
    private Builder setUsername(String username) {
      this.username = username;
      return this;
    }

    @JsonProperty("age")
    private Builder setAge(Integer age) {
      this.age = age;
      return this;
    }

    private Builder setAge(Optional<Integer> age) {
      this.age = age.orElse(null);
      return this;
    }

    @JsonProperty("email")
    private Builder setEmail(String email) {
      this.email = email;
      this.isEmailNull = email == null;
      return this;
    }

    private Builder setEmail(Tristate<String> email) {
      this.email = email.onValue(val -> val).onNull(() -> null).onAbsent(() -> null);
      this.isEmailNull = email.onValue(ignore -> false).onNull(() -> true).onAbsent(() -> false);
      return this;
    }

    @JsonAnySetter
    private Builder addAdditionalProperty(String key, Object value) {
      this.additionalProperties.put(key, value);
      return this;
    }

    private Builder setAdditionalProperties(Map<String, Object> additionalProperties) {
      this.additionalProperties = new HashMap<>(additionalProperties);
      return this;
    }

    public AdminOrUserDto build() {
      return new AdminOrUserDto(
          name,
          id,
          adminname,
          level,
          color,
          username,
          age,
          email,
          isEmailNull,
          additionalProperties);
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

    public Builder1 setName(String name) {
      return new Builder1(builder.setName(name));
    }
  }

  public static final class Builder1 {
    private final Builder builder;

    private Builder1(Builder builder) {
      this.builder = builder;
    }

    public Builder2 setAdminDto(AdminDto adminDto) {
      adminDto.getAdditionalProperties().forEach(builder::addAdditionalProperty);
      return new Builder2(
          builder
              .setId(adminDto.getId())
              .setAdminname(adminDto.getAdminname())
              .setLevel(adminDto.getLevelOpt())
              .setColor(adminDto.getColorOpt()));
    }

    public Builder2 setUserDto(UserDto userDto) {
      userDto.getAdditionalProperties().forEach(builder::addAdditionalProperty);
      return new Builder2(
          builder
              .setId(userDto.getId())
              .setUsername(userDto.getUsername())
              .setAge(userDto.getAgeOpt())
              .setEmail(userDto.getEmailTristate()));
    }
  }

  public static final class Builder2 {
    private final Builder builder;

    private Builder2(Builder builder) {
      this.builder = builder;
    }

    public Builder2 setAdminDto(AdminDto adminDto) {
      adminDto.getAdditionalProperties().forEach(builder::addAdditionalProperty);
      return new Builder2(
          builder
              .setId(adminDto.getId())
              .setAdminname(adminDto.getAdminname())
              .setLevel(adminDto.getLevelOpt())
              .setColor(adminDto.getColorOpt()));
    }

    public Builder2 setUserDto(UserDto userDto) {
      userDto.getAdditionalProperties().forEach(builder::addAdditionalProperty);
      return new Builder2(
          builder
              .setId(userDto.getId())
              .setUsername(userDto.getUsername())
              .setAge(userDto.getAgeOpt())
              .setEmail(userDto.getEmailTristate()));
    }

    public OptBuilder0 andAllOptionals() {
      return new OptBuilder0(builder);
    }

    public Builder andOptionals() {
      return builder;
    }

    public AdminOrUserDto build() {
      return builder.build();
    }
  }

  public static final class OptBuilder0 {
    private final Builder builder;

    private OptBuilder0(Builder builder) {
      this.builder = builder;
    }

    public OptBuilder0 addAdditionalProperty(String key, Object value) {
      return new OptBuilder0(builder.addAdditionalProperty(key, value));
    }

    public OptBuilder0 setAdditionalProperties(Map<String, Object> additionalProperties) {
      return new OptBuilder0(builder.setAdditionalProperties(additionalProperties));
    }

    public AdminOrUserDto build() {
      return builder.build();
    }
  }
}
