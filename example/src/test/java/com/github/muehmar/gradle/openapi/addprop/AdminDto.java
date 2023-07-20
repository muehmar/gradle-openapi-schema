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
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@JsonDeserialize(builder = AdminDto.Builder.class)
public class AdminDto {
  private final String type;
  private final String adminname;
  private final Map<String, BaseDataDto> additionalProperties;

  public AdminDto(String type, String adminname, Map<String, BaseDataDto> additionalProperties) {
    this.type = type;
    this.adminname = adminname;
    this.additionalProperties = Collections.unmodifiableMap(additionalProperties);
  }

  @NotNull
  public String getType() {
    return type;
  }

  @NotNull
  public String getAdminname() {
    return adminname;
  }

  @JsonAnyGetter
  public Map<String, @Valid BaseDataDto> getAdditionalProperties() {
    return additionalProperties;
  }

  /**
   * Returns the additional property with {@code key} wrapped in an {@link Optional} if present,
   * {@link Optional#empty()} otherwise
   */
  public Optional<BaseDataDto> getAdditionalProperty(String key) {
    return Optional.ofNullable(additionalProperties.get(key));
  }

  public AdminDto withType(String type) {
    return new AdminDto(type, adminname, additionalProperties);
  }

  public AdminDto withAdminname(String adminname) {
    return new AdminDto(type, adminname, additionalProperties);
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
    final AdminDto other = (AdminDto) obj;
    return Objects.deepEquals(this.type, other.type)
        && Objects.deepEquals(this.adminname, other.adminname)
        && Objects.deepEquals(this.additionalProperties, other.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, adminname, additionalProperties);
  }

  @Override
  public String toString() {
    return "AdminDto{"
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
        + "additionalProperties="
        + additionalProperties
        + "}";
  }

  @JsonPOJOBuilder(withPrefix = "set")
  public static final class Builder {

    private Builder() {}

    private String type;
    private String adminname;
    private Map<String, BaseDataDto> additionalProperties = new HashMap<>();

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

    @JsonAnySetter
    public Builder addAdditionalProperty(String key, BaseDataDto value) {
      this.additionalProperties.put(key, value);
      return this;
    }

    public Builder setAdditionalProperties(Map<String, BaseDataDto> additionalProperties) {
      this.additionalProperties = new HashMap<>(additionalProperties);
      return this;
    }

    public AdminDto build() {
      return new AdminDto(type, adminname, additionalProperties);
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

    public PropertyBuilder2 setAdminname(String adminname) {
      return new PropertyBuilder2(builder.setAdminname(adminname));
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

    public AdminDto build() {
      return builder.build();
    }
  }

  public static final class OptPropertyBuilder0 {
    private final Builder builder;

    private OptPropertyBuilder0(Builder builder) {
      this.builder = builder;
    }

    public OptPropertyBuilder0 addAdditionalProperty(String key, BaseDataDto value) {
      return new OptPropertyBuilder0(builder.addAdditionalProperty(key, value));
    }

    public OptPropertyBuilder0 setAdditionalProperties(
        Map<String, BaseDataDto> additionalProperties) {
      return new OptPropertyBuilder0(builder.setAdditionalProperties(additionalProperties));
    }

    public AdminDto build() {
      return builder.build();
    }
  }
}
