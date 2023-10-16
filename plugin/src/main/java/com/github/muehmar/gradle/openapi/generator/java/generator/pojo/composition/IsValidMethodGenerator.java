package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition;

import static io.github.muehmar.codegenerator.Generator.constant;

import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;

public class IsValidMethodGenerator {
  private IsValidMethodGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> isValidMethodGenerator() {
    return JavaGenerators.<JavaObjectPojo, PojoSettings>methodGen()
        .modifiers()
        .noGenericTypes()
        .returnType("boolean")
        .methodName("isValid")
        .noArguments()
        .content(constant("return new Validator().isValid();"))
        .build();
  }
}
