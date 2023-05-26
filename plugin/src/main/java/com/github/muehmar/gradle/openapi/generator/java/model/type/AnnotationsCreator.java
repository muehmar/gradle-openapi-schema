package com.github.muehmar.gradle.openapi.generator.java.model.type;

import ch.bluecare.commons.data.PList;
import lombok.Value;

public interface AnnotationsCreator {

  Annotations createForType(JavaType javaType);

  static AnnotationsCreator empty() {
    return ignore -> Annotations.empty();
  }

  @Value
  class Annotations {
    String annotations;
    PList<String> imports;

    public static Annotations empty() {
      return new Annotations("", PList.empty());
    }
  }
}
