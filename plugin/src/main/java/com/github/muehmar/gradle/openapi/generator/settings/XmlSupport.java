package com.github.muehmar.gradle.openapi.generator.settings;

import ch.bluecare.commons.data.PList;
import java.io.Serializable;
import java.util.Optional;

public enum XmlSupport implements Serializable {
  JACKSON("jackson"),
  NONE("none");

  private final String value;

  XmlSupport(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static Optional<XmlSupport> fromString(String value) {
    return PList.of(values()).find(support -> support.value.equalsIgnoreCase(value));
  }
}
