package com.github.muehmar.gradle.openapi.dsl;

import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMapping;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ClassMapping implements Serializable {
  private String fromClass;
  private String toClass;

  public void setFromClass(String fromClass) {
    this.fromClass = fromClass;
  }

  public void setToClass(String toClass) {
    this.toClass = toClass;
  }

  public ClassTypeMapping toSettingsClassMapping() {
    return new ClassTypeMapping(fromClass, toClass);
  }
}
