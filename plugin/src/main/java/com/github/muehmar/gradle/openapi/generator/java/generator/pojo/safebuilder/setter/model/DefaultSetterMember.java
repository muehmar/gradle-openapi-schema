package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.setter.model;

import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;

public abstract class DefaultSetterMember implements SetterMember {
  @Override
  public String builderMethodName(PojoSettings settings) {
    return getMember().prefixedMethodName(settings.getBuilderMethodPrefix()).asString();
  }

  @Override
  public String argumentType() {
    return getMember().getJavaType().getParameterizedClassName().asString();
  }
}
