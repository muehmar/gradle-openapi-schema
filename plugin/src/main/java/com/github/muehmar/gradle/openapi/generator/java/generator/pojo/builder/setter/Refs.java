package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.setter;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedApiClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;

public class Refs {
  private Refs() {}

  public static PList<String> forApiType(JavaType javaType) {
    return ParameterizedApiClassName.fromJavaType(javaType)
        .map(ParameterizedApiClassName::getAllQualifiedClassNames)
        .orElseGet(PList::empty)
        .map(QualifiedClassName::asString);
  }
}
