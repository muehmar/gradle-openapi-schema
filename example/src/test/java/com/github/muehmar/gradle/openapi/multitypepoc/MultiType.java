package com.github.muehmar.gradle.openapi.multitypepoc;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Objects;

@JsonDeserialize(using = MultiTypeDeserializer.class)
public class MultiType {
  private final String stringValue;
  private final Long longValue;
  private final Integer intValue;
  private final Double doubleValue;
  private final Float floatValue;
  private final Boolean booleanValue;
  private final List<String> list;

  private final SuperObject object;

  public MultiType(
      String stringValue,
      Long longValue,
      Integer intValue,
      Double doubleValue,
      Float floatValue,
      Boolean booleanValue,
      List<String> list,
      SuperObject object) {
    this.stringValue = stringValue;
    this.longValue = longValue;
    this.intValue = intValue;
    this.doubleValue = doubleValue;
    this.floatValue = floatValue;
    this.booleanValue = booleanValue;
    this.list = list;
    this.object = object;
  }

  public static MultiType fromNull() {
    return new MultiType(null, null, null, null, null, null, null, null);
  }

  public static MultiType fromString(String stringValue) {
    return new MultiType(stringValue, null, null, null, null, null, null, null);
  }

  public static MultiType fromInt(Integer intValue) {
    return new MultiType(null, null, intValue, null, null, null, null, null);
  }

  public static MultiType fromLong(Long longValue) {
    return new MultiType(null, longValue, null, null, null, null, null, null);
  }

  public static MultiType fromFloat(Float floatValue) {
    return new MultiType(null, null, null, null, floatValue, null, null, null);
  }

  public static MultiType fromDouble(Double doubleValue) {
    return new MultiType(null, null, null, doubleValue, null, null, null, null);
  }

  public static MultiType fromBoolean(Boolean booleanValue) {
    return new MultiType(null, null, null, null, null, booleanValue, null, null);
  }

  public static MultiType fromList(List<String> list) {
    return new MultiType(null, null, null, null, null, null, list, null);
  }

  public static MultiType fromObject(SuperObject object) {
    return new MultiType(null, null, null, null, null, null, null, object);
  }

  @JsonValue
  private Object getValue() {
    if (stringValue != null) {
      return stringValue;
    } else if (longValue != null) {
      return longValue;
    } else if (intValue != null) {
      return intValue;
    } else if (doubleValue != null) {
      return doubleValue;
    } else if (floatValue != null) {
      return floatValue;
    } else if (booleanValue != null) {
      return booleanValue;
    } else if (list != null) {
      return list;
    } else if (object != null) {
      return object;
    } else {
      return null;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final MultiType multiType = (MultiType) o;
    return Objects.equals(stringValue, multiType.stringValue)
        && Objects.equals(longValue, multiType.longValue)
        && Objects.equals(intValue, multiType.intValue)
        && Objects.equals(doubleValue, multiType.doubleValue)
        && Objects.equals(floatValue, multiType.floatValue)
        && Objects.equals(booleanValue, multiType.booleanValue)
        && Objects.equals(list, multiType.list)
        && Objects.equals(object, multiType.object);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        stringValue, longValue, intValue, doubleValue, floatValue, booleanValue, list, object);
  }

  @Override
  public String toString() {
    return "MultiType{"
        + "stringValue='"
        + stringValue
        + '\''
        + ", longValue="
        + longValue
        + ", intValue="
        + intValue
        + ", doubleValue="
        + doubleValue
        + ", floatValue="
        + floatValue
        + ", booleanValue="
        + booleanValue
        + ", list="
        + list
        + ", object="
        + object
        + '}';
  }
}
