package com.github.muehmar.gradle.openapi.validation;

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
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@JsonDeserialize(builder = ManualAllValueObjectDto.Builder.class)
public class ManualAllValueObjectDto {
  private final Double doubleValue;
  private final Float floatValue;
  private final Integer intValue;
  private final Long longValue;
  private final String stringValue;
  private final String email;
  private final Map<String, Object> additionalProperties;

  public ManualAllValueObjectDto(
      Double doubleValue,
      Float floatValue,
      Integer intValue,
      Long longValue,
      String stringValue,
      String email,
      Map<String, Object> additionalProperties) {
    this.doubleValue = doubleValue;
    this.floatValue = floatValue;
    this.intValue = intValue;
    this.longValue = longValue;
    this.stringValue = stringValue;
    this.email = email;
    this.additionalProperties = Collections.unmodifiableMap(additionalProperties);
  }

  @JsonIgnore
  public Optional<Double> getDoubleValueOpt() {
    return Optional.ofNullable(doubleValue);
  }

  @JsonIgnore
  public Double getDoubleValueOr(Double defaultValue) {
    return this.doubleValue == null ? defaultValue : this.doubleValue;
  }

  @JsonProperty("doubleValue")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @DecimalMin(value = "5.1", inclusive = true)
  @DecimalMax(value = "100.5", inclusive = false)
  private Double getDoubleValueRaw() {
    return doubleValue;
  }

  @JsonIgnore
  public Optional<Float> getFloatValueOpt() {
    return Optional.ofNullable(floatValue);
  }

  @JsonIgnore
  public Float getFloatValueOr(Float defaultValue) {
    return this.floatValue == null ? defaultValue : this.floatValue;
  }

  @JsonProperty("floatValue")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @DecimalMin(value = "200.25", inclusive = true)
  @DecimalMax(value = "300.5", inclusive = false)
  private Float getFloatValueRaw() {
    return floatValue;
  }

  @JsonIgnore
  public Optional<Integer> getIntValueOpt() {
    return Optional.ofNullable(intValue);
  }

  @JsonIgnore
  public Integer getIntValueOr(Integer defaultValue) {
    return this.intValue == null ? defaultValue : this.intValue;
  }

  @JsonProperty("intValue")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Min(value = -5)
  @Max(value = 22)
  private Integer getIntValueRaw() {
    return intValue;
  }

  @JsonIgnore
  public Optional<Long> getLongValueOpt() {
    return Optional.ofNullable(longValue);
  }

  @JsonIgnore
  public Long getLongValueOr(Long defaultValue) {
    return this.longValue == null ? defaultValue : this.longValue;
  }

  @JsonProperty("longValue")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Min(value = -122)
  @Max(value = 249)
  private Long getLongValueRaw() {
    return longValue;
  }

  @JsonIgnore
  public Optional<String> getStringValueOpt() {
    return Optional.ofNullable(stringValue);
  }

  @JsonIgnore
  public String getStringValueOr(String defaultValue) {
    return this.stringValue == null ? defaultValue : this.stringValue;
  }

  @JsonProperty("stringValue")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Size(min = 2, max = 10)
  @Pattern(regexp = "[A-Za-z\\d]*")
  private String getStringValueRaw() {
    return stringValue;
  }

  @JsonIgnore
  public Optional<String> getEmailOpt() {
    return Optional.ofNullable(email);
  }

  @JsonIgnore
  public String getEmailOr(String defaultValue) {
    return this.email == null ? defaultValue : this.email;
  }

  @JsonProperty("email")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Email
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

  public ManualAllValueObjectDto withDoubleValue(Double doubleValue) {
    return new ManualAllValueObjectDto(
        doubleValue, floatValue, intValue, longValue, stringValue, email, additionalProperties);
  }

  public ManualAllValueObjectDto withDoubleValue(Optional<Double> doubleValue) {
    return new ManualAllValueObjectDto(
        doubleValue.orElse(null),
        floatValue,
        intValue,
        longValue,
        stringValue,
        email,
        additionalProperties);
  }

  public ManualAllValueObjectDto withFloatValue(Float floatValue) {
    return new ManualAllValueObjectDto(
        doubleValue, floatValue, intValue, longValue, stringValue, email, additionalProperties);
  }

  public ManualAllValueObjectDto withFloatValue(Optional<Float> floatValue) {
    return new ManualAllValueObjectDto(
        doubleValue,
        floatValue.orElse(null),
        intValue,
        longValue,
        stringValue,
        email,
        additionalProperties);
  }

  public ManualAllValueObjectDto withIntValue(Integer intValue) {
    return new ManualAllValueObjectDto(
        doubleValue, floatValue, intValue, longValue, stringValue, email, additionalProperties);
  }

  public ManualAllValueObjectDto withIntValue(Optional<Integer> intValue) {
    return new ManualAllValueObjectDto(
        doubleValue,
        floatValue,
        intValue.orElse(null),
        longValue,
        stringValue,
        email,
        additionalProperties);
  }

  public ManualAllValueObjectDto withLongValue(Long longValue) {
    return new ManualAllValueObjectDto(
        doubleValue, floatValue, intValue, longValue, stringValue, email, additionalProperties);
  }

  public ManualAllValueObjectDto withLongValue(Optional<Long> longValue) {
    return new ManualAllValueObjectDto(
        doubleValue,
        floatValue,
        intValue,
        longValue.orElse(null),
        stringValue,
        email,
        additionalProperties);
  }

  public ManualAllValueObjectDto withStringValue(String stringValue) {
    return new ManualAllValueObjectDto(
        doubleValue, floatValue, intValue, longValue, stringValue, email, additionalProperties);
  }

  public ManualAllValueObjectDto withStringValue(Optional<String> stringValue) {
    return new ManualAllValueObjectDto(
        doubleValue,
        floatValue,
        intValue,
        longValue,
        stringValue.orElse(null),
        email,
        additionalProperties);
  }

  public ManualAllValueObjectDto withEmail(String email) {
    return new ManualAllValueObjectDto(
        doubleValue, floatValue, intValue, longValue, stringValue, email, additionalProperties);
  }

  public ManualAllValueObjectDto withEmail(Optional<String> email) {
    return new ManualAllValueObjectDto(
        doubleValue,
        floatValue,
        intValue,
        longValue,
        stringValue,
        email.orElse(null),
        additionalProperties);
  }

  /** Returns the number of present properties of this object. */
  @JsonIgnore
  public int getPropertyCount() {
    return (doubleValue != null ? 1 : 0)
        + (floatValue != null ? 1 : 0)
        + (intValue != null ? 1 : 0)
        + (longValue != null ? 1 : 0)
        + (stringValue != null ? 1 : 0)
        + (email != null ? 1 : 0)
        + additionalProperties.size();
  }

  boolean isValid() {
    return new Validator().isValid();
  }

  private class Validator {
    @DecimalMin(value = "5.1", inclusive = true)
    @DecimalMax(value = "100.5", inclusive = false)
    private boolean isDoubleValueValid() {
      return 5.1d <= doubleValue && doubleValue < 100.5d;
    }

    @DecimalMin(value = "200.25", inclusive = true)
    @DecimalMax(value = "300.5", inclusive = false)
    private boolean isFloatValueValid() {
      return 200.25f <= floatValue && floatValue < 300.5f;
    }

    @Min(value = -5)
    @Max(value = 22)
    private boolean isIntValueValid() {
      return -5 <= intValue && intValue <= 22;
    }

    @Size(min = 2, max = 10)
    @Pattern(regexp = "[A-Za-z\\d]*")
    private boolean isStringValueValid() {
      return 2 <= stringValue.length()
          && stringValue.length() <= 10
          && java.util.regex.Pattern.matches("[A-Za-z\\d]*", stringValue);
    }

    private boolean isValid() {
      // Including everything what's already checked in validateBasic
      return isDoubleValueValid()
          && isFloatValueValid()
          && isIntValueValid()
          && isStringValueValid();
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || this.getClass() != obj.getClass()) return false;
    final ManualAllValueObjectDto other = (ManualAllValueObjectDto) obj;
    return Objects.deepEquals(this.doubleValue, other.doubleValue)
        && Objects.deepEquals(this.floatValue, other.floatValue)
        && Objects.deepEquals(this.intValue, other.intValue)
        && Objects.deepEquals(this.longValue, other.longValue)
        && Objects.deepEquals(this.stringValue, other.stringValue)
        && Objects.deepEquals(this.email, other.email)
        && Objects.deepEquals(this.additionalProperties, other.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        doubleValue, floatValue, intValue, longValue, stringValue, email, additionalProperties);
  }

  @Override
  public String toString() {
    return "AllValueObjectDto{"
        + "doubleValue="
        + doubleValue
        + ", "
        + "floatValue="
        + floatValue
        + ", "
        + "intValue="
        + intValue
        + ", "
        + "longValue="
        + longValue
        + ", "
        + "stringValue="
        + "'"
        + stringValue
        + "'"
        + ", "
        + "email="
        + "'"
        + email
        + "'"
        + ", "
        + "additionalProperties="
        + additionalProperties
        + "}";
  }

  @JsonPOJOBuilder(withPrefix = "set")
  public static final class Builder {

    private Builder() {}

    private Double doubleValue;
    private Float floatValue;
    private Integer intValue;
    private Long longValue;
    private String stringValue;
    private String email;
    private Map<String, Object> additionalProperties = new HashMap<>();

    @JsonProperty("doubleValue")
    public Builder setDoubleValue(Double doubleValue) {
      this.doubleValue = doubleValue;
      return this;
    }

    public Builder setDoubleValue(Optional<Double> doubleValue) {
      this.doubleValue = doubleValue.orElse(null);
      return this;
    }

    @JsonProperty("floatValue")
    public Builder setFloatValue(Float floatValue) {
      this.floatValue = floatValue;
      return this;
    }

    public Builder setFloatValue(Optional<Float> floatValue) {
      this.floatValue = floatValue.orElse(null);
      return this;
    }

    @JsonProperty("intValue")
    public Builder setIntValue(Integer intValue) {
      this.intValue = intValue;
      return this;
    }

    public Builder setIntValue(Optional<Integer> intValue) {
      this.intValue = intValue.orElse(null);
      return this;
    }

    @JsonProperty("longValue")
    public Builder setLongValue(Long longValue) {
      this.longValue = longValue;
      return this;
    }

    public Builder setLongValue(Optional<Long> longValue) {
      this.longValue = longValue.orElse(null);
      return this;
    }

    @JsonProperty("stringValue")
    public Builder setStringValue(String stringValue) {
      this.stringValue = stringValue;
      return this;
    }

    public Builder setStringValue(Optional<String> stringValue) {
      this.stringValue = stringValue.orElse(null);
      return this;
    }

    @JsonProperty("email")
    public Builder setEmail(String email) {
      this.email = email;
      return this;
    }

    public Builder setEmail(Optional<String> email) {
      this.email = email.orElse(null);
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

    public ManualAllValueObjectDto build() {
      return new ManualAllValueObjectDto(
          doubleValue, floatValue, intValue, longValue, stringValue, email, additionalProperties);
    }
  }

  public static FullOptPropertyBuilder0 fullBuilder() {
    return new FullOptPropertyBuilder0(new Builder());
  }

  public static FullOptPropertyBuilder0 fullAllValueObjectDtoBuilder() {
    return new FullOptPropertyBuilder0(new Builder());
  }

  public static final class FullOptPropertyBuilder0 {
    private final Builder builder;

    private FullOptPropertyBuilder0(Builder builder) {
      this.builder = builder;
    }

    public FullOptPropertyBuilder1 setDoubleValue(Double doubleValue) {
      return new FullOptPropertyBuilder1(builder.setDoubleValue(doubleValue));
    }

    public FullOptPropertyBuilder1 setDoubleValue(Optional<Double> doubleValue) {
      return new FullOptPropertyBuilder1(builder.setDoubleValue(doubleValue));
    }
  }

  public static final class FullOptPropertyBuilder1 {
    private final Builder builder;

    private FullOptPropertyBuilder1(Builder builder) {
      this.builder = builder;
    }

    public FullOptPropertyBuilder2 setFloatValue(Float floatValue) {
      return new FullOptPropertyBuilder2(builder.setFloatValue(floatValue));
    }

    public FullOptPropertyBuilder2 setFloatValue(Optional<Float> floatValue) {
      return new FullOptPropertyBuilder2(builder.setFloatValue(floatValue));
    }
  }

  public static final class FullOptPropertyBuilder2 {
    private final Builder builder;

    private FullOptPropertyBuilder2(Builder builder) {
      this.builder = builder;
    }

    public FullOptPropertyBuilder3 setIntValue(Integer intValue) {
      return new FullOptPropertyBuilder3(builder.setIntValue(intValue));
    }

    public FullOptPropertyBuilder3 setIntValue(Optional<Integer> intValue) {
      return new FullOptPropertyBuilder3(builder.setIntValue(intValue));
    }
  }

  public static final class FullOptPropertyBuilder3 {
    private final Builder builder;

    private FullOptPropertyBuilder3(Builder builder) {
      this.builder = builder;
    }

    public FullOptPropertyBuilder4 setLongValue(Long longValue) {
      return new FullOptPropertyBuilder4(builder.setLongValue(longValue));
    }

    public FullOptPropertyBuilder4 setLongValue(Optional<Long> longValue) {
      return new FullOptPropertyBuilder4(builder.setLongValue(longValue));
    }
  }

  public static final class FullOptPropertyBuilder4 {
    private final Builder builder;

    private FullOptPropertyBuilder4(Builder builder) {
      this.builder = builder;
    }

    public FullOptPropertyBuilder5 setStringValue(String stringValue) {
      return new FullOptPropertyBuilder5(builder.setStringValue(stringValue));
    }

    public FullOptPropertyBuilder5 setStringValue(Optional<String> stringValue) {
      return new FullOptPropertyBuilder5(builder.setStringValue(stringValue));
    }
  }

  public static final class FullOptPropertyBuilder5 {
    private final Builder builder;

    private FullOptPropertyBuilder5(Builder builder) {
      this.builder = builder;
    }

    public FullOptPropertyBuilder6 setEmail(String email) {
      return new FullOptPropertyBuilder6(builder.setEmail(email));
    }

    public FullOptPropertyBuilder6 setEmail(Optional<String> email) {
      return new FullOptPropertyBuilder6(builder.setEmail(email));
    }
  }

  public static final class FullOptPropertyBuilder6 {
    private final Builder builder;

    private FullOptPropertyBuilder6(Builder builder) {
      this.builder = builder;
    }

    public FullOptPropertyBuilder6 addAdditionalProperty(String key, Object value) {
      return new FullOptPropertyBuilder6(builder.addAdditionalProperty(key, value));
    }

    public FullOptPropertyBuilder6 setAdditionalProperties(
        Map<String, Object> additionalProperties) {
      return new FullOptPropertyBuilder6(builder.setAdditionalProperties(additionalProperties));
    }

    public ManualAllValueObjectDto build() {
      return builder.build();
    }
  }

  public static PropertyBuilder0 builder() {
    return new PropertyBuilder0(new Builder());
  }

  public static PropertyBuilder0 allValueObjectDtoBuilder() {
    return new PropertyBuilder0(new Builder());
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

    public ManualAllValueObjectDto build() {
      return builder.build();
    }
  }

  public static final class OptPropertyBuilder0 {
    private final Builder builder;

    private OptPropertyBuilder0(Builder builder) {
      this.builder = builder;
    }

    public OptPropertyBuilder1 setDoubleValue(Double doubleValue) {
      return new OptPropertyBuilder1(builder.setDoubleValue(doubleValue));
    }

    public OptPropertyBuilder1 setDoubleValue(Optional<Double> doubleValue) {
      return new OptPropertyBuilder1(builder.setDoubleValue(doubleValue));
    }
  }

  public static final class OptPropertyBuilder1 {
    private final Builder builder;

    private OptPropertyBuilder1(Builder builder) {
      this.builder = builder;
    }

    public OptPropertyBuilder2 setFloatValue(Float floatValue) {
      return new OptPropertyBuilder2(builder.setFloatValue(floatValue));
    }

    public OptPropertyBuilder2 setFloatValue(Optional<Float> floatValue) {
      return new OptPropertyBuilder2(builder.setFloatValue(floatValue));
    }
  }

  public static final class OptPropertyBuilder2 {
    private final Builder builder;

    private OptPropertyBuilder2(Builder builder) {
      this.builder = builder;
    }

    public OptPropertyBuilder3 setIntValue(Integer intValue) {
      return new OptPropertyBuilder3(builder.setIntValue(intValue));
    }

    public OptPropertyBuilder3 setIntValue(Optional<Integer> intValue) {
      return new OptPropertyBuilder3(builder.setIntValue(intValue));
    }
  }

  public static final class OptPropertyBuilder3 {
    private final Builder builder;

    private OptPropertyBuilder3(Builder builder) {
      this.builder = builder;
    }

    public OptPropertyBuilder4 setLongValue(Long longValue) {
      return new OptPropertyBuilder4(builder.setLongValue(longValue));
    }

    public OptPropertyBuilder4 setLongValue(Optional<Long> longValue) {
      return new OptPropertyBuilder4(builder.setLongValue(longValue));
    }
  }

  public static final class OptPropertyBuilder4 {
    private final Builder builder;

    private OptPropertyBuilder4(Builder builder) {
      this.builder = builder;
    }

    public OptPropertyBuilder5 setStringValue(String stringValue) {
      return new OptPropertyBuilder5(builder.setStringValue(stringValue));
    }

    public OptPropertyBuilder5 setStringValue(Optional<String> stringValue) {
      return new OptPropertyBuilder5(builder.setStringValue(stringValue));
    }
  }

  public static final class OptPropertyBuilder5 {
    private final Builder builder;

    private OptPropertyBuilder5(Builder builder) {
      this.builder = builder;
    }

    public OptPropertyBuilder6 setEmail(String email) {
      return new OptPropertyBuilder6(builder.setEmail(email));
    }

    public OptPropertyBuilder6 setEmail(Optional<String> email) {
      return new OptPropertyBuilder6(builder.setEmail(email));
    }
  }

  public static final class OptPropertyBuilder6 {
    private final Builder builder;

    private OptPropertyBuilder6(Builder builder) {
      this.builder = builder;
    }

    public OptPropertyBuilder6 addAdditionalProperty(String key, Object value) {
      return new OptPropertyBuilder6(builder.addAdditionalProperty(key, value));
    }

    public OptPropertyBuilder6 setAdditionalProperties(Map<String, Object> additionalProperties) {
      return new OptPropertyBuilder6(builder.setAdditionalProperties(additionalProperties));
    }

    public ManualAllValueObjectDto build() {
      return builder.build();
    }
  }
}
