package com.github.muehmar.gradle.openapi.generator.settings;

import java.io.Serializable;
import lombok.Value;

@Value
public class TypeConversion implements Serializable {
  String fromCustomType;
  String toCustomType;
}
