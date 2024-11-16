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
public class IntegerType implements Type {
  private final Format format;
  private final Nullability nullability;
  private final Constraints constraints;

  private IntegerType(Format format, Nullability nullability, Constraints constraints) {
    this.format = format;
    this.nullability = nullability;
    this.constraints = constraints;
  }

  public static IntegerType ofFormat(Format format, Nullability nullability) {
    return new IntegerType(format, nullability, Constraints.empty());
  }

  public static IntegerType formatInteger() {
    return IntegerType.ofFormat(Format.INTEGER, Nullability.NOT_NULLABLE);
  }

  public static IntegerType formatLong() {
    return IntegerType.ofFormat(Format.LONG, Nullability.NOT_NULLABLE);
  }

  public IntegerType withConstraints(Constraints constraints) {
    return new IntegerType(format, nullability, constraints);
  }

  @Override
  public Constraints getConstraints() {
    return constraints;
  }

  @Override
  public IntegerType applyMapping(PojoNameMapping pojoNameMapping) {
    return this;
  }

  @Override
  public Type makeNullable() {
    return new IntegerType(format, Nullability.NULLABLE, constraints);
  }

  @Override
  public Type replaceObjectType(
      PojoName objectTypeName, String newObjectTypeDescription, Type newObjectType) {
    return this;
  }

  public Format getFormat() {
    return format;
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
      Function<AnyType, T> onAnyType,
      Function<MultiType, T> onMultiType) {
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
