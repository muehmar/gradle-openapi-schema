package com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation;

import static io.github.muehmar.codegenerator.Generator.constant;

import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;

public class IsValidMethodGenerator {
  private IsValidMethodGenerator() {}

  public static <T> Generator<T, PojoSettings> isValidMethodGenerator() {
    return JavaGenerators.<T, PojoSettings>methodGen()
        .modifiers()
        .noGenericTypes()
        .returnType("boolean")
        .methodName("isValid")
        .noArguments()
        .doesNotThrow()
        .content(constant("return new Validator().isValid();"))
        .build();
  }
}
