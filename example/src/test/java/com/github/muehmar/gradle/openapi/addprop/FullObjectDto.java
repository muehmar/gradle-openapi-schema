package com.github.muehmar.gradle.openapi.addprop;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

@JsonDeserialize(builder = FullObjectDto.Builder.class)
public class FullObjectDto {
  private final String message;
  private final BaseDataDto.ColorEnum color;
  private final String type;
  private final String adminname;
  private final String username;
  private final Map<String, Object> additionalProperties;

  public FullObjectDto(
      String message,
      BaseDataDto.ColorEnum color,
      String type,
      String adminname,
      String username,
      Map<String, Object> additionalProperties) {
    this.message = message;
    this.color = color;
    this.type = type;
    this.adminname = adminname;
    this.username = username;
    this.additionalProperties = Collections.unmodifiableMap(additionalProperties);
  }

  @JsonIgnore
  public Optional<String> getMessageOpt() {
    return Optional.ofNullable(message);
  }

  @JsonIgnore
  public String getMessageOr(String defaultValue) {
    return this.message == null ? defaultValue : this.message;
  }

  @JsonProperty("message")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String getMessageRaw() {
    return message;
  }

  @NotNull
  public BaseDataDto.ColorEnum getColor() {
    return color;
  }

  @JsonProperty("type")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String getType() {
    return type;
  }

  @JsonProperty("adminname")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String getAdminname() {
    return adminname;
  }

  @JsonProperty("username")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String getUsername() {
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

  public FullObjectDto withMessage(String message) {
    return new FullObjectDto(message, color, type, adminname, username, additionalProperties);
  }

  public FullObjectDto withMessage(Optional<String> message) {
    return new FullObjectDto(
        message.orElse(null), color, type, adminname, username, additionalProperties);
  }

  /** Returns the number of present properties of this object. */
  @JsonIgnore
  public int getPropertyCount() {
    return (message != null ? 1 : 0) + additionalProperties.size();
  }

  /**
   * Folds the oneOf part of this instance using the given mapping functions for the DTO's. If this
   * instance is valid against exactly one of the specified schemas, its corresponding mapping
   * function gets executed with the DTO as input and its result is returned.<br>
   * <br>
   * I.e. if the JSON was valid against the schema 'Admin', the mapping method {@code onAdminDto}
   * gets executed with the {@link AdminDto} as argument.<br>
   * <br>
   * Unlike {@link FullObjectDto#foldOneOf(Function, Function)}, this method accepts as last
   * parameter a {@link Supplier} which gets called in case this instance is not valid against
   * exactly one of the defined oneOf schemas and its value is returned.
   */
  public <T> T foldOneOf(
      Function<AdminDto, T> onAdminDto, Function<UserDto, T> onUserDto, Supplier<T> onInvalid) {
    if ("Admin".equals(type) && isValidAgainstAdminDto()) {
      return onAdminDto.apply(asAdminDto());
    } else if ("User".equals(type) && isValidAgainstUserDto()) {
      return onUserDto.apply(asUserDto());
    } else {
      return onInvalid.get();
    }
  }

  /**
   * Folds the oneOf part of this instance using the given mapping functions for the DTO's. If this
   * instance is valid against exactly one of the specified schemas, its corresponding mapping
   * function gets executed with the DTO as input and its result is returned.<br>
   * <br>
   * I.e. if the JSON was valid against the schema 'Admin', the mapping method {@code onAdminDto}
   * gets executed with the {@link AdminDto} as argument.<br>
   * <br>
   * This method assumes this instance is either manually or automatically validated, i.e. the JSON
   * is valid against exactly one of the oneOf schemas. If it is either valid against no schema or
   * multiple schemas, it will throw an {@link IllegalStateException}.
   */
  public <T> T foldOneOf(Function<AdminDto, T> onAdminDto, Function<UserDto, T> onUserDto) {
    return foldOneOf(
        onAdminDto,
        onUserDto,
        () -> {
          throw new IllegalStateException(
              "Unable to fold the oneOf part of FullObjectDto: Not valid against one of the schemas [AdminDto, UserDto].");
        });
  }

  private AdminDto asAdminDto() {
    return new AdminDto(
        type,
        adminname,
        additionalProperties.entrySet().stream()
            .filter(e -> e.getValue() instanceof BaseDataDto)
            .collect(Collectors.toMap(Map.Entry::getKey, e -> (BaseDataDto) e.getValue())));
  }

  private UserDto asUserDto() {
    return new UserDto(type, username, additionalProperties);
  }

  private boolean isValidAgainstAdminDto() {
    return type != null && adminname != null;
  }

  private boolean isValidAgainstUserDto() {
    return type != null && username != null;
  }

  private int getOneOfValidCount() {
    return (isValidAgainstAdminDto() ? 1 : 0) + (isValidAgainstUserDto() ? 1 : 0);
  }

  @AssertFalse(message = "Is not valid against one of the schemas [Admin, User]")
  @JsonIgnore
  private boolean isValidAgainstNoOneOfSchema() {
    return getOneOfValidCount() == 0;
  }

  @AssertFalse(message = "Is valid against more than one of the schemas [Admin, User]")
  @JsonIgnore
  private boolean isValidAgainstMoreThanOneSchema() {
    return getOneOfValidCount() > 1;
  }

  @Valid
  @JsonIgnore
  private Object getOneOf() {
    if (getOneOfValidCount() != 1) {
      return null;
    }
    return foldOneOf(dto -> dto, dto -> dto, () -> null);
  }

  @AssertTrue(message = "Not valid against the schema described by the discriminator")
  @JsonIgnore
  private boolean isValidAgainstTheCorrectSchema() {
    if (type == null) {
      return false;
    }
    switch (type) {
      case "Admin":
        return isValidAgainstAdminDto();
      case "User":
        return isValidAgainstUserDto();
    }
    return false;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || this.getClass() != obj.getClass()) return false;
    final FullObjectDto other = (FullObjectDto) obj;
    return Objects.deepEquals(this.message, other.message)
        && Objects.deepEquals(this.color, other.color)
        && Objects.deepEquals(this.type, other.type)
        && Objects.deepEquals(this.adminname, other.adminname)
        && Objects.deepEquals(this.username, other.username)
        && Objects.deepEquals(this.additionalProperties, other.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, color, type, adminname, username, additionalProperties);
  }

  @Override
  public String toString() {
    return "FullObjectDto{"
        + "message="
        + "'"
        + message
        + "'"
        + ", "
        + "color="
        + color
        + ", "
        + "type="
        + "'"
        + type
        + "'"
        + ", "
        + "adminname="
        + "'"
        + adminname
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

    private String message;
    private BaseDataDto.ColorEnum color;
    private String type;
    private String adminname;
    private String username;
    private Map<String, Object> additionalProperties = new HashMap<>();

    @JsonProperty("message")
    public Builder setMessage(String message) {
      this.message = message;
      return this;
    }

    public Builder setMessage(Optional<String> message) {
      this.message = message.orElse(null);
      return this;
    }

    @JsonProperty("color")
    private Builder setColor(BaseDataDto.ColorEnum color) {
      this.color = color;
      return this;
    }

    @JsonProperty("type")
    private Builder setType(String type) {
      this.type = type;
      return this;
    }

    @JsonProperty("adminname")
    private Builder setAdminname(String adminname) {
      this.adminname = adminname;
      return this;
    }

    @JsonProperty("username")
    private Builder setUsername(String username) {
      this.username = username;
      return this;
    }

    private Builder setBaseDataDto(BaseDataDto dto) {
      setColor(dto.getColor());
      dto.getAdditionalProperties().forEach(this::addAdditionalProperty);
      return this;
    }

    private Builder setAdminDto(AdminDto dto) {
      setType("Admin");
      setAdminname(dto.getAdminname());
      dto.getAdditionalProperties().forEach(this::addAdditionalProperty);
      return this;
    }

    private Builder setUserDto(UserDto dto) {
      setType("User");
      setUsername(dto.getUsername());
      dto.getAdditionalProperties().forEach(this::addAdditionalProperty);
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

    public FullObjectDto build() {
      return new FullObjectDto(message, color, type, adminname, username, additionalProperties);
    }
  }

  public static AllOfBuilderBaseData0 newBuilder() {
    return new AllOfBuilderBaseData0(new Builder());
  }

  public static final class AllOfBuilderBaseData0 {
    private final Builder builder;

    private AllOfBuilderBaseData0(Builder builder) {
      this.builder = builder;
    }

    public OneOfBuilder0 setColor(BaseDataDto.ColorEnum color) {
      return new OneOfBuilder0(builder.setColor(color));
    }

    public OneOfBuilder0 setBaseDataDto(BaseDataDto dto) {
      return new OneOfBuilder0(builder.setBaseDataDto(dto));
    }
  }

  public static final class OneOfBuilder0 {
    private final Builder builder;

    private OneOfBuilder0(Builder builder) {
      this.builder = builder;
    }

    public PropertyBuilder0 setAdminDto(AdminDto dto) {
      return new PropertyBuilder0(builder.setAdminDto(dto));
    }

    public PropertyBuilder0 setUserDto(UserDto dto) {
      return new PropertyBuilder0(builder.setUserDto(dto));
    }
  }

  public static final class PropertyBuilder0 {
    private final Builder builder;

    private PropertyBuilder0(Builder builder) {
      this.builder = builder;
    }

    public OptPropertyBuilder0 andAllOptionals() {
      return new OptPropertyBuilder0(builder);
    }

    public Builder andOptionals() {
      return builder;
    }

    public FullObjectDto build() {
      return builder.build();
    }
  }

  public static final class OptPropertyBuilder0 {
    private final Builder builder;

    private OptPropertyBuilder0(Builder builder) {
      this.builder = builder;
    }

    public OptPropertyBuilder1 setMessage(String message) {
      return new OptPropertyBuilder1(builder.setMessage(message));
    }

    public OptPropertyBuilder1 setMessage(Optional<String> message) {
      return new OptPropertyBuilder1(builder.setMessage(message));
    }
  }

  public static final class OptPropertyBuilder1 {
    private final Builder builder;

    private OptPropertyBuilder1(Builder builder) {
      this.builder = builder;
    }

    public OptPropertyBuilder1 addAdditionalProperty(String key, Object value) {
      return new OptPropertyBuilder1(builder.addAdditionalProperty(key, value));
    }

    public OptPropertyBuilder1 setAdditionalProperties(Map<String, Object> additionalProperties) {
      return new OptPropertyBuilder1(builder.setAdditionalProperties(additionalProperties));
    }

    public FullObjectDto build() {
      return builder.build();
    }
  }
}
