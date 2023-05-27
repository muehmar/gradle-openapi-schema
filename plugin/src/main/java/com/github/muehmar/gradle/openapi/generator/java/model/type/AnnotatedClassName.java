package com.github.muehmar.gradle.openapi.generator.java.model.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import lombok.Value;

@Value
public class AnnotatedClassName {
  Name className;
  PList<String> imports;

  public static AnnotatedClassName fromClassName(Name name) {
    return new AnnotatedClassName(name, PList.empty());
  }

  public static AnnotatedClassName fromClassNameAndImports(Name name, PList<String> imports) {
    return new AnnotatedClassName(name, imports);
  }
}
