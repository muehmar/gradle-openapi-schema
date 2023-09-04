package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.name;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.allof.AllOfBuilderName;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property.OptionalPropertyBuilderName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;

public interface BuilderName {
  String currentName();

  static BuilderName initial(SafeBuilderVariant builderVariant, JavaObjectPojo pojo) {
    if (pojo.isSimpleMapPojo()) {
      return OptionalPropertyBuilderName.initial(builderVariant, pojo);
    } else {
      return AllOfBuilderName.initial(builderVariant, pojo);
    }
  }
}
