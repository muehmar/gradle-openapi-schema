package com.github.muehmar.gradle.openapi.generator.java.model;

import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PropertyScope;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public enum PojoType {
  REQUEST(name -> name.prependSuffix("Request"), PropertyScope::isUsedInRequest),
  RESPONSE(name -> name.prependSuffix("Response"), PropertyScope::isUsedInResponse),
  DEFAULT(name -> name, ps -> true);

  private final UnaryOperator<PojoName> mapName;
  private final Predicate<PropertyScope> includesPropertyScope;

  PojoType(UnaryOperator<PojoName> mapName, Predicate<PropertyScope> includesPropertyScope) {
    this.mapName = mapName;
    this.includesPropertyScope = includesPropertyScope;
  }

  public PojoName mapName(PojoName name) {
    return mapName.apply(name);
  }

  public boolean includesPropertyScope(PropertyScope propertyScope) {
    return includesPropertyScope.test(propertyScope);
  }
}
