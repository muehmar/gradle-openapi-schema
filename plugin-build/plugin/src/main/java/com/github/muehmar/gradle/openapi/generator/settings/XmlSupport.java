package com.github.muehmar.gradle.openapi.generator.settings;

import ch.bluecare.commons.data.PList;
import java.io.Serializable;
import java.util.Optional;

public enum XmlSupport implements Serializable {
  JACKSON_3("jackson-3", XmlSupportGroup.JACKSON),
  JACKSON_2("jackson-2", XmlSupportGroup.JACKSON),
  NONE("none", XmlSupportGroup.NONE);

  private final String value;
  private final XmlSupportGroup group;

  XmlSupport(String value, XmlSupportGroup group) {
    this.value = value;
    this.group = group;
  }

  public static Optional<XmlSupport> fromString(String value) {
    return PList.of(values()).find(support -> support.value.equalsIgnoreCase(value));
  }

  public String getValue() {
    return value;
  }

  public XmlSupportGroup getGroup() {
    return group;
  }
}
