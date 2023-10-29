package com.github.muehmar.gradle.openapi.warnings;

import lombok.Value;

/** Warning which may occur during the generation of the code for the schemas. */
@Value
public class Warning {
  WarningType type;
  String message;
}
