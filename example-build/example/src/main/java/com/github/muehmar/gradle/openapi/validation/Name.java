package com.github.muehmar.gradle.openapi.validation;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Objects;

public class Name {
  @JsonValue private final String val;

  public Name(String val) {
    this.val = val;
  }

  public String getVal() {
    return val;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final Name name = (Name) o;
    return Objects.equals(val, name.val);
  }

  @Override
  public int hashCode() {
    return Objects.hash(val);
  }
}
