package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;

import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

public class ValidationMethodGenerator {
  private ValidationMethodGenerator() {}

  public static Generator<JavaPojo, PojoSettings> isValidAgainstMethod() {
    return MethodGenBuilder.<JavaPojo, PojoSettings>create()
        .modifiers(PRIVATE)
        .noGenericTypes()
        .returnType("boolean")
        .methodName(javaPojo -> String.format("isValidAgainst%s", javaPojo.getName().getName()))
        .noArguments()
        .content("return true;")
        .build()
        .filter((pojos, settings) -> settings.isEnableConstraints());
  }
}
