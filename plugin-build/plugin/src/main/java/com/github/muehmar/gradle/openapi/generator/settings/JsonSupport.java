package com.github.muehmar.gradle.openapi.generator.settings;

import ch.bluecare.commons.data.PList;
import java.io.Serializable;
import java.util.Optional;

public enum JsonSupport implements Serializable {
  JACKSON_3("jackson-3", JsonSupportGroup.JACKSON),
  JACKSON_2("jackson-2", JsonSupportGroup.JACKSON),
  NONE("none", JsonSupportGroup.NONE);

  private final String value;
  private final JsonSupportGroup group;

  JsonSupport(String value, JsonSupportGroup group) {
    this.value = value;
    this.group = group;
  }

  public static Optional<JsonSupport> fromString(String value) {
    return PList.of(values()).find(support -> support.value.equalsIgnoreCase(value));
  }

  public String getValue() {
    return value;
  }

  public JsonSupportGroup getGroup() {
    return group;
  }
}
