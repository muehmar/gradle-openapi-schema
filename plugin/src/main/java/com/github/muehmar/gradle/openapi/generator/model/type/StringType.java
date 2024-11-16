package com.github.muehmar.gradle.openapi.generator.model.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.settings.PojoNameMapping;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class StringType implements Type {
  private final Format format;
  private final String formatString;
  private final Nullability nullability;
  private final Constraints constraints;

  private StringType(
      Format format, String formatString, Nullability nullability, Constraints constraints) {
    this.format = format;
    this.formatString = formatString;
    this.nullability = nullability;
    this.constraints = constraints;
  }

  public static StringType noFormat() {
    return ofFormat(Format.NONE);
  }

  public static StringType uuid() {
    return ofFormat(Format.UUID);
  }

  public static StringType ofFormat(Format format) {
    return new StringType(format, format.value, Nullability.NOT_NULLABLE, Constraints.empty());
  }

  public static StringType ofFormatAndValue(Format format, String formatString) {
    return new StringType(format, formatString, Nullability.NOT_NULLABLE, Constraints.empty());
  }

  public Format getFormat() {
    return format;
  }

  public String getFormatString() {
    return formatString;
  }

  public StringType withNullability(Nullability nullability) {
    return new StringType(format, formatString, nullability, constraints);
  }

  public StringType withConstraints(Constraints constraints) {
    return new StringType(format, formatString, nullability, constraints);
  }

  public StringType addConstraints(Constraints constraints) {
    return new StringType(format, formatString, nullability, this.constraints.and(constraints));
  }

  @Override
  public Constraints getConstraints() {
    return constraints;
  }

  @Override
  public StringType applyMapping(PojoNameMapping pojoNameMapping) {
    return this;
  }

  @Override
  public Type makeNullable() {
    return withNullability(Nullability.NULLABLE);
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
      Function<AnyType, T> onAnyType,
      Function<MultiType, T> onMultiType) {
    return onStringType.apply(this);
  }

  public enum Format {
    DATE("date"),
    DATE_TIME("date-time"),
    TIME("partial-time"),
    PASSWORD("password"),
    BYTE("byte"),
    BINARY("binary"),
    EMAIL("email"),
    UUID("uuid"),
    URI("uri"),
    URL("url"),
    HOSTNAME("hostname"),
    IPV4("ipv4"),
    IPV6("ipv6"),
    NONE("none"),
    OTHER("other");

    private final String value;

    Format(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    public static Format parseString(String value) {
      return PList.fromArray(values()).find(f -> f.value.equals(value)).orElse(OTHER);
    }
  }
}
