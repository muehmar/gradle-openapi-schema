package com.github.muehmar.gradle.openapi.generator.java.model;

import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaArrayPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaEnumPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.NewPojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.function.Function;

public interface JavaPojo {

  PojoName getName();

  <T> T fold(
      Function<JavaArrayPojo, T> onArrayPojo,
      Function<JavaEnumPojo, T> onEnumPojo,
      Function<JavaObjectPojo, T> onObjectPojo);

  static JavaPojo wrap(NewPojo pojo, TypeMappings typeMappings) {
    return pojo.fold(
        objectPojo -> JavaObjectPojo.wrap(objectPojo, typeMappings),
        arrayPojo -> JavaArrayPojo.wrap(arrayPojo, typeMappings),
        JavaEnumPojo::wrap);
  }

  default boolean isArray() {
    return fold(arrayPojo -> true, enumPojo -> false, objectPojo -> false);
  }
}
