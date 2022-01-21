package com.github.muehmar.gradle.openapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.Objects;
import javax.validation.constraints.NotNull;

/** */
@JsonDeserialize(builder = ColorDto2.Builder.class)
public class ColorDto2 {
  private final Integer colorKey;
  private final String colorName;

  public ColorDto2(Integer colorKey, String colorName) {
    this.colorKey = colorKey;
    this.colorName = colorName;
  }

  /** */
  @NotNull
  public Integer getColorKey() {
    return colorKey;
  }

  /** */
  @NotNull
  public String getColorName() {
    return colorName;
  }

  /** */
  public ColorDto2 withColorKey(Integer colorKey) {
    return new ColorDto2(colorKey, colorName);
  }

  /** */
  public ColorDto2 withColorName(String colorName) {
    return new ColorDto2(colorKey, colorName);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || this.getClass() != obj.getClass()) return false;
    final ColorDto2 other = (ColorDto2) obj;
    return Objects.equals(colorKey, other.colorKey) && Objects.equals(colorName, other.colorName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(colorKey, colorName);
  }

  @Override
  public String toString() {
    return "ColorDto{" + "colorKey=" + colorKey + ", colorName=" + colorName + "}";
  }

  @JsonPOJOBuilder(withPrefix = "set")
  public static final class Builder {

    private Builder() {}

    private Integer colorKey;
    private String colorName;

    /** */
    @JsonProperty("colorKey")
    private Builder setColorKey(Integer colorKey) {
      this.colorKey = colorKey;
      return this;
    }

    /** */
    @JsonProperty("colorName")
    private Builder setColorName(String colorName) {
      this.colorName = colorName;
      return this;
    }

    public ColorDto2 build() {
      return new ColorDto2(colorKey, colorName);
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
    public Builder1 setColorKey(Integer colorKey) {
      return new Builder1(builder.setColorKey(colorKey));
    }
  }

  public static final class Builder1 {
    private final Builder builder;

    private Builder1(Builder builder) {
      this.builder = builder;
    }

    /** */
    public Builder2 setColorName(String colorName) {
      return new Builder2(builder.setColorName(colorName));
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

    public ColorDto2 build() {
      return builder.build();
    }
  }

  public static final class OptBuilder0 {
    private final Builder builder;

    private OptBuilder0(Builder builder) {
      this.builder = builder;
    }

    public ColorDto2 build() {
      return builder.build();
    }
  }
}
