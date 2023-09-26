package com.github.muehmar.gradle.openapi.generator.settings;

import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;

public interface PojoNameMapping {

  static PojoNameMapping replaceConstant(String constant, String replacement) {
    return pojoName ->
        PojoName.ofNameAndSuffix(
            pojoName.getName().asString().replace(constant, replacement), pojoName.getSuffix());
  }

  static PojoNameMapping noMapping() {
    return pojoName -> pojoName;
  }

  default PojoNameMapping andThen(PojoNameMapping other) {
    final PojoNameMapping self = this;
    return pojoName -> other.map(self.map(pojoName));
  }

  PojoName map(PojoName pojoName);
}
