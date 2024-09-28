package com.github.muehmar.gradle.openapi.generator.model.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.settings.PojoNameMapping;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class NumericType implements Type {

  private final Format format;
  private final Nullability nullability;
  private final Constraints constraints;

  private NumericType(Format format, Nullability nullability, Constraints constraints) {
    this.format = format;
    this.nullability = nullability;
    this.constraints = constraints;
  }

  public static NumericType ofFormat(Format format, Nullability nullability) {
    return new NumericType(format, nullability, Constraints.empty());
  }

  public static NumericType formatFloat() {
    return NumericType.ofFormat(Format.FLOAT, Nullability.NOT_NULLABLE);
  }

  public static NumericType formatDouble() {
    return NumericType.ofFormat(Format.DOUBLE, Nullability.NOT_NULLABLE);
  }

  public NumericType withConstraints(Constraints constraints) {
    return new NumericType(format, nullability, constraints);
  }

  public Format getFormat() {
    return format;
  }

  @Override
  public Constraints getConstraints() {
    return constraints;
  }

  @Override
  public NumericType applyMapping(PojoNameMapping pojoNameMapping) {
    return this;
  }

  @Override
  public Type makeNullable() {
    return new NumericType(format, Nullability.NULLABLE, constraints);
  }

  @Override
  public Type replaceObjectType(
      PojoName objectTypeName, String newObjectTypeDescription, Type newObjectType) {
    return this;
  }

  @Override
  public Nullability getNullability() {
    return nullability;
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
      Function<AnyType, T> onAnyType) {
    return onNumericType.apply(this);
  }

  public enum Format {
    FLOAT("float"),
    DOUBLE("double");

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
