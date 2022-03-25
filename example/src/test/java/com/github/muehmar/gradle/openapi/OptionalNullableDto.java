package com.github.muehmar.gradle.openapi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@JsonDeserialize(builder = OptionalNullableDto.Builder.class)
public class OptionalNullableDto {
  // Required, not nullable
  private final String prop1;

  // Required, nullable
  private final String prop2;
  private final boolean isProp2Present;

  // Optional, not nullable
  private final String prop3;

  // Optional, nullable
  private final String prop4;
  private final boolean isProp4Null;

  OptionalNullableDto(
      String prop1,
      String prop2,
      boolean isProp2Present,
      String prop3,
      String prop4,
      boolean isProp4Null) {
    this.prop1 = prop1;
    this.prop2 = prop2;
    this.isProp2Present = isProp2Present;
    this.prop3 = prop3;
    this.prop4 = prop4;
    this.isProp4Null = isProp4Null;
  }

  @NotNull
  public String getProp1() {
    return prop1;
  }

  @JsonIgnore
  public Optional<String> getProp2() {
    return Optional.ofNullable(prop2);
  }

  @JsonProperty("prop2")
  @JsonInclude(JsonInclude.Include.ALWAYS)
  private String getProp2Nullable() {
    return prop2;
  }

  @AssertTrue(message = "Prop2 is absent but is required")
  private boolean isProp2Present() {
    return isProp2Present;
  }

  @JsonIgnore
  public Optional<String> getProp3() {
    return Optional.ofNullable(prop3);
  }

  @JsonProperty("prop3")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Pattern(regexp = "World")
  private String getProp3Nullable() {
    return prop3;
  }

  @JsonIgnore
  public Tristate<String> getProp4() {
    return Tristate.ofNullableAndNullFlag(prop4, isProp4Null);
  }

  @JsonProperty("prop4")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Object getProp4Jackson() {
    return isProp4Null ? new JacksonNullContainer<>(prop4) : prop4;
  }

  @Pattern(regexp = "World")
  private String getProp4Nullable() {
    return prop4;
  }

  @JsonPOJOBuilder(withPrefix = "")
  static class Builder {
    private String prop1;
    private String prop2;
    private boolean isProp2Present = false;
    private String prop3;
    private String prop4;
    private boolean isProp4Null = false;

    private Builder() {}

    Builder prop1(String prop1) {
      this.prop1 = prop1;
      return this;
    }

    Builder prop2(String prop2) {
      this.prop2 = prop2;
      this.isProp2Present = true;
      return this;
    }

    Builder prop3(String prop3) {
      this.prop3 = prop3;
      return this;
    }

    Builder prop4(String prop4) {
      this.prop4 = prop4;
      if (prop4 == null) {
        this.isProp4Null = true;
      }
      return this;
    }

    public OptionalNullableDto build() {
      return new OptionalNullableDto(prop1, prop2, isProp2Present, prop3, prop4, isProp4Null);
    }
  }

  public static class JacksonNullContainer<T> {
    private final T value;

    public JacksonNullContainer(T value) {
      this.value = value;
    }

    @JsonValue
    public T getValue() {
      return value;
    }
  }

  public static class Tristate<T> {
    private final Optional<T> value;
    private final boolean isNull;

    private Tristate(Optional<T> value, boolean isNull) {
      this.value = value;
      this.isNull = isNull;
    }

    public static <T> Tristate<T> ofNullableAndNullFlag(T nullableValue, boolean isNull) {
      return new Tristate<>(Optional.ofNullable(nullableValue), isNull);
    }

    public static <T> Tristate<T> ofNull() {
      return new Tristate<>(Optional.empty(), true);
    }

    public static <T> Tristate<T> ofValue(T value) {
      return new Tristate<>(Optional.of(value), false);
    }

    public static <T> Tristate<T> ofAbsent() {
      return new Tristate<>(Optional.empty(), false);
    }

    public <R> OnValue<R> onValue(Function<T, R> onValue) {
      return onNull ->
          onAbsent -> value.map(onValue).orElseGet(() -> isNull ? onNull.get() : onAbsent.get());
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Tristate<?> tristate = (Tristate<?>) o;
      return isNull == tristate.isNull && Objects.equals(value, tristate.value);
    }

    @Override
    public String toString() {
      return "Tristate{" + "value=" + value + ", isNull=" + isNull + '}';
    }

    @Override
    public int hashCode() {
      return Objects.hash(value, isNull);
    }

    @FunctionalInterface
    public interface OnValue<R> {
      OnNull<R> onNull(Supplier<R> onNull);
    }

    @FunctionalInterface
    public interface OnNull<R> {
      R onAbsent(Supplier<R> onAbsent);
    }
  }
}
