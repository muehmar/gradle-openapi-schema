package com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliaryy;

import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoName;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import lombok.Value;

@Value
public class OneOfContainer {
  private static final String NAME_SUFFIX = "OneOfContainer";
  JavaPojoName pojoName;
  JavaOneOfComposition composition;

  public JavaPojoName getContainerName() {
    return pojoName.appendToName(NAME_SUFFIX);
  }
}
