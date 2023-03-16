package com.github.muehmar.gradle.openapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.Objects;
import javax.validation.constraints.NotNull;

/** */
@JsonDeserialize(builder = FlagsDto2.Builder.class)
public class FlagsDto2 {
  private final Boolean boolFlag;
  private final Integer intFlag;

  public FlagsDto2(Boolean boolFlag, Integer intFlag) {
    this.boolFlag = boolFlag;
    this.intFlag = intFlag;
  }

  /** */
  @NotNull
  public Boolean getBoolFlag() {
    return boolFlag;
  }

  /** */
  @NotNull
  public Integer getIntFlag() {
    return intFlag;
  }

  /** */
  public FlagsDto2 withBoolFlag(Boolean boolFlag) {
    return new FlagsDto2(boolFlag, intFlag);
  }

  /** */
  public FlagsDto2 withIntFlag(Integer intFlag) {
    return new FlagsDto2(boolFlag, intFlag);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || this.getClass() != obj.getClass()) return false;
    final FlagsDto2 other = (FlagsDto2) obj;
    return Objects.equals(boolFlag, other.boolFlag) && Objects.equals(intFlag, other.intFlag);
  }

  @Override
  public int hashCode() {
    return Objects.hash(boolFlag, intFlag);
  }

  @Override
  public String toString() {
    return "FlagsDto{" + "boolFlag=" + boolFlag + ", intFlag=" + intFlag + "}";
  }

  @JsonPOJOBuilder(withPrefix = "set")
  public static final class Builder {

    private Builder() {}

    private Boolean boolFlag;
    private Integer intFlag;

    /** */
    @JsonProperty("boolFlag")
    private Builder setBoolFlag(Boolean boolFlag) {
      this.boolFlag = boolFlag;
      return this;
    }

    /** */
    @JsonProperty("intFlag")
    private Builder setIntFlag(Integer intFlag) {
      this.intFlag = intFlag;
      return this;
    }

    public FlagsDto2 build() {
      return new FlagsDto2(boolFlag, intFlag);
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
    public Builder1 setBoolFlag(Boolean boolFlag) {
      return new Builder1(builder.setBoolFlag(boolFlag));
    }
  }

  public static final class Builder1 {
    private final Builder builder;

    private Builder1(Builder builder) {
      this.builder = builder;
    }

    /** */
    public Builder2 setIntFlag(Integer intFlag) {
      return new Builder2(builder.setIntFlag(intFlag));
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

    public FlagsDto2 build() {
      return builder.build();
    }
  }

  public static final class OptBuilder0 {
    private final Builder builder;

    private OptBuilder0(Builder builder) {
      this.builder = builder;
    }

    public FlagsDto2 build() {
      return builder.build();
    }
  }
}
