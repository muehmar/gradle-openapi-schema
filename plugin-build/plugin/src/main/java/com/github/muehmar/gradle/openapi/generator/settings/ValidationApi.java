package com.github.muehmar.gradle.openapi.generator.settings;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import java.util.Optional;

public enum ValidationApi {
  JAKARTA_2_0("jakarta-2", "jakarta-2.0"),
  JAKARTA_3_0("jakarta-3", "jakarta-3.0");

  private final NonEmptyList<String> values;

  ValidationApi(String value, String... more) {
    this.values = NonEmptyList.of(value, more);
  }

  public String getValue() {
    return values.head();
  }

  public static Optional<ValidationApi> fromString(String value) {
    return PList.of(values()).find(api -> api.values.toPList().exists(value::equals));
  }
}
