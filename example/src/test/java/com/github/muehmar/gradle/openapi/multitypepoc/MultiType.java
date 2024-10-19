package com.github.muehmar.gradle.openapi.multitypepoc;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Objects;

@JsonDeserialize(using = MultiTypeDeserializer.class)
public class MultiType {
  private final String stringValue;
  private final Long longValue;
  private final Boolean booleanValue;
  private final List<String> list;

  private final SuperObject object;

  public MultiType(
      String stringValue,
      Long longValue,
      Boolean booleanValue,
      List<String> list,
      SuperObject object) {
    this.stringValue = stringValue;
    this.longValue = longValue;
    this.booleanValue = booleanValue;
    this.list = list;
    this.object = object;
  }

  public static MultiType fromString(String stringValue) {
    return new MultiType(stringValue, null, null, null, null);
  }

  public static MultiType fromLong(Long longValue) {
    return new MultiType(null, longValue, null, null, null);
  }

  public static MultiType fromBoolean(Boolean booleanValue) {
    return new MultiType(null, null, booleanValue, null, null);
  }

  public static MultiType fromList(List<String> list) {
    return new MultiType(null, null, null, list, null);
  }

  public static MultiType fromObject(SuperObject object) {
    return new MultiType(null, null, null, null, object);
  }

  @JsonValue
  private Object getValue() {
    if (stringValue != null) {
      return stringValue;
    } else if (longValue != null) {
      return longValue;
    } else if (booleanValue != null) {
      return booleanValue;
    } else if (list != null) {
      return list;
    } else if (object != null) {
      return object;
    }
    return null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final MultiType multiType = (MultiType) o;
    return Objects.equals(stringValue, multiType.stringValue)
        && Objects.equals(longValue, multiType.longValue)
        && Objects.equals(booleanValue, multiType.booleanValue)
        && Objects.equals(list, multiType.list)
        && Objects.equals(object, multiType.object);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stringValue, longValue, booleanValue, list, object);
  }

  @Override
  public String toString() {
    return "MultiType{"
        + "stringValue='"
        + stringValue
        + '\''
        + ", longValue="
        + longValue
        + ", booleanValue="
        + booleanValue
        + ", list="
        + list
        + ", object="
        + object
        + '}';
  }
}
