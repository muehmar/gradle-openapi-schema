package com.github.muehmar.gradle.openapi.warnings;

import lombok.Value;

@Value
public class Warning {
  WarningType type;
  String message;
}
