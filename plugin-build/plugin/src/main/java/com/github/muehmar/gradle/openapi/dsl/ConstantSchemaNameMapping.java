package com.github.muehmar.gradle.openapi.dsl;

import com.github.muehmar.gradle.openapi.generator.settings.ConstantNameMapping;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ConstantSchemaNameMapping implements Serializable {
  private String constant;
  private String replacement;

  public void setConstant(String constant) {
    this.constant = constant;
  }

  public void setReplacement(String replacement) {
    this.replacement = replacement;
  }

  public ConstantNameMapping toConstantNameMapping() {
    return new ConstantNameMapping(constant, replacement);
  }
}
