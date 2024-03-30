package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.setter.model;

import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;

public abstract class NullableListItemsSetterMember implements SetterMember {

  @Override
  public String builderMethodName(PojoSettings settings) {
    return getMember().prefixedMethodName(settings.getBuilderMethodPrefix()).asString().concat("_");
  }

  @Override
  public String argumentType() {
    return getMember()
        .getJavaType()
        .getParameterizedClassName()
        .asStringWrappingNullableValueType();
  }
}
