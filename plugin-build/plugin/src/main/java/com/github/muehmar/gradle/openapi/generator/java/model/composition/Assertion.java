package com.github.muehmar.gradle.openapi.generator.java.model.composition;

import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.exception.OpenApiGeneratorException;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojo;

class Assertion {
  private Assertion() {}

  static NonEmptyList<JavaObjectPojo> assertAllObjectPojos(NonEmptyList<JavaPojo> pojos) {
    return pojos.map(
        pojo -> {
          if (pojo instanceof JavaObjectPojo) {
            return (JavaObjectPojo) pojo;
          } else {
            throw new OpenApiGeneratorException(
                String.format(
                    "Only object schemas are supported for compositions but '%s' is a non-object schema. See "
                        + "https://github.com/muehmar/gradle-openapi-schema/issues/265.",
                    pojo.getSchemaName()));
          }
        });
  }
}
