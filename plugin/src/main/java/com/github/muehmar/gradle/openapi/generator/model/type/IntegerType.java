package com.github.muehmar.gradle.openapi.generator.model.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class IntegerType implements Type {
  private final Format format;
  private final Constraints constraints;

  private IntegerType(Format format, Constraints constraints) {
    this.format = format;
    this.constraints = constraints;
  }

  public static IntegerType ofFormat(Format format) {
    return new IntegerType(format, Constraints.empty());
  }

  public static IntegerType formatInteger() {
    return IntegerType.ofFormat(Format.INTEGER);
  }

  public static IntegerType formatLong() {
    return IntegerType.ofFormat(Format.LONG);
  }

  public IntegerType withConstraints(Constraints constraints) {
    return new IntegerType(format, constraints);
  }

  @Override
  public Constraints getConstraints() {
    return constraints;
  }

  public Format getFormat() {
    return format;
  }

  @Override
  public <T> T fold(
      Function<NumericType, T> onNumericType,
      Function<IntegerType, T> onIntegerType,
      Function<StringType, T> onStringType,
      Function<ArrayType, T> onArrayType,
      Function<BooleanType, T> onBooleanType,
      Function<ObjectType, T> onObjectType,
      Function<EnumType, T> onEnumType,
      Function<MapType, T> onMapType,
      Function<NoType, T> onNoType) {
    return onIntegerType.apply(this);
  }

  public enum Format {
    INTEGER("int32"),
    LONG("int64");

    private final String value;

    Format(String value) {
      this.value = value;
    }

    public String asString() {
      return value;
    }

    public static Optional<Format> parseString(String value) {
      return PList.fromArray(values()).find(f -> f.value.equals(value));
    }
  }
}
