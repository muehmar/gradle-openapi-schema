package com.github.muehmar.gradle.openapi.generator.settings;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ClassTypeMapping implements Serializable {
  private final String fromClass;
  private final String toClass;

  public ClassTypeMapping(String fromClass, String toClass) {
    this.fromClass = fromClass;
    this.toClass = toClass;
  }

  public String getFromClass() {
    return fromClass;
  }

  public String getToClass() {
    return toClass;
  }
}
