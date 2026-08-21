package com.github.muehmar.gradle.openapi.generator.settings;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ConstantNameMapping implements Serializable {
  private final String constant;
  private final String replacement;

  public ConstantNameMapping(String constant, String replacement) {
    this.constant = constant;
    this.replacement = replacement;
  }

  public String getConstant() {
    return constant;
  }

  public String getReplacement() {
    return replacement;
  }
}
