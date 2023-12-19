package com.github.muehmar.gradle.openapi.generator.java.generator.shared;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.JavaDocGenerators;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

public class DeprecatedMethodGenerator {
  private DeprecatedMethodGenerator() {}

  public static <A> Generator<A, PojoSettings> deprecatedJavaDocAndAnnotationForValidationMethod() {
    return JavaDocGenerators.<A>deprecatedValidationMethodJavaDoc()
        .append(AnnotationGenerator.deprecatedAnnotationForValidationMethod())
        .filter((a, settings) -> settings.getValidationMethods().isDeprecatedAnnotation());
  }
}
