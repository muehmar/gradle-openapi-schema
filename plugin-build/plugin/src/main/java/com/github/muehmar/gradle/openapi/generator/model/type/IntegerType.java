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
public class IntegerType implements AdditionalPropertiesValueType, InlinableType {
  private final Format format;
  private final Optional<String> formatString;
  private final Nullability nullability;
  private final Constraints constraints;

  private IntegerType(
      Format format,
      Optional<String> formatString,
      Nullability nullability,
      Constraints constraints) {
    this.format = format;
    this.formatString = formatString;
    this.nullability = nullability;
    this.constraints = constraints;
  }

  public static IntegerType ofFormat(Format format, Nullability nullability) {
    return new IntegerType(
        format, Optional.of(format.asString()), nullability, Constraints.empty());
  }

  /**
   * Creates a type for a schema which declares {@code formatString} as format, where {@code format}
   * is the normalized format the type is internally represented with. The declared format is kept
   * as it is the one a format-type-mapping is matched against.
   */
  public static IntegerType ofFormatAndValue(
      Format format, String formatString, Nullability nullability) {
    return new IntegerType(format, Optional.of(formatString), nullability, Constraints.empty());
  }

  /** Creates a type for a schema which declares no format at all. */
  public static IntegerType noFormat(Format format, Nullability nullability) {
    return new IntegerType(format, Optional.empty(), nullability, Constraints.empty());
  }

  public static IntegerType formatInteger() {
    return IntegerType.ofFormat(Format.INTEGER, Nullability.NOT_NULLABLE);
  }

  public static IntegerType formatLong() {
    return IntegerType.ofFormat(Format.LONG, Nullability.NOT_NULLABLE);
  }

  public IntegerType withConstraints(Constraints constraints) {
    return new IntegerType(format, formatString, nullability, constraints);
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
  public IntegerType makeNullable() {
    return new IntegerType(format, formatString, Nullability.NULLABLE, constraints);
  }

  @Override
  public Type replaceObjectType(
      PojoName objectTypeName, String newObjectTypeDescription, InlinableType newType) {
    return this;
  }

  public Format getFormat() {
    return format;
  }

  /**
   * The format as declared in the specification, empty if the specification declares no format. Use
   * this for format-type-mapping lookups, as {@link #getFormat()} is the normalized format which
   * may differ from the declared one.
   */
  public Optional<String> getFormatString() {
    return formatString;
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

  @Override
  public <T> T foldInlinableType(
      Function<NumericType, T> onNumericType,
      Function<IntegerType, T> onIntegerType,
      Function<StringType, T> onStringType,
      Function<BooleanType, T> onBooleanType,
      Function<ObjectType, T> onObjectType,
      Function<EnumType, T> onEnumType,
      Function<AnyType, T> onAnyType) {
    return onIntegerType.apply(this);
  }

  @Override
  public <T> T foldAdditionalPropertiesValueType(
      Function<NumericType, T> onNumericType,
      Function<IntegerType, T> onIntegerType,
      Function<StringType, T> onStringType,
      Function<BooleanType, T> onBooleanType,
      Function<ObjectType, T> onObjectType,
      Function<EnumType, T> onEnumType,
      Function<AnyType, T> onAnyType) {
    return onIntegerType.apply(this);
  }
}
