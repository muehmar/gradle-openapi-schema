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
public class NumericType implements AdditionalPropertiesValueType, InlinableType {

  private final Format format;
  private final Optional<String> formatString;
  private final Nullability nullability;
  private final Constraints constraints;

  private NumericType(
      Format format,
      Optional<String> formatString,
      Nullability nullability,
      Constraints constraints) {
    this.format = format;
    this.formatString = formatString;
    this.nullability = nullability;
    this.constraints = constraints;
  }

  public static NumericType ofFormat(Format format, Nullability nullability) {
    return new NumericType(
        format, Optional.of(format.asString()), nullability, Constraints.empty());
  }

  /**
   * Creates a type for a schema which declares {@code formatString} as format, where {@code format}
   * is the normalized format the type is internally represented with. The declared format is kept
   * as it is the one a format-type-mapping is matched against.
   */
  public static NumericType ofFormatAndValue(
      Format format, String formatString, Nullability nullability) {
    return new NumericType(format, Optional.of(formatString), nullability, Constraints.empty());
  }

  /** Creates a type for a schema which declares no format at all. */
  public static NumericType noFormat(Format format, Nullability nullability) {
    return new NumericType(format, Optional.empty(), nullability, Constraints.empty());
  }

  public static NumericType formatFloat() {
    return NumericType.ofFormat(Format.FLOAT, Nullability.NOT_NULLABLE);
  }

  public static NumericType formatDouble() {
    return NumericType.ofFormat(Format.DOUBLE, Nullability.NOT_NULLABLE);
  }

  public NumericType withConstraints(Constraints constraints) {
    return new NumericType(format, formatString, nullability, constraints);
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
  public Constraints getConstraints() {
    return constraints;
  }

  @Override
  public NumericType applyMapping(PojoNameMapping pojoNameMapping) {
    return this;
  }

  @Override
  public NumericType makeNullable() {
    return new NumericType(format, formatString, Nullability.NULLABLE, constraints);
  }

  @Override
  public Type replaceObjectType(
      PojoName objectTypeName, String newObjectTypeDescription, InlinableType newType) {
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

  @Override
  public <T> T foldInlinableType(
      Function<NumericType, T> onNumericType,
      Function<IntegerType, T> onIntegerType,
      Function<StringType, T> onStringType,
      Function<BooleanType, T> onBooleanType,
      Function<ObjectType, T> onObjectType,
      Function<EnumType, T> onEnumType,
      Function<AnyType, T> onAnyType) {
    return onNumericType.apply(this);
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
    return onNumericType.apply(this);
  }
}
