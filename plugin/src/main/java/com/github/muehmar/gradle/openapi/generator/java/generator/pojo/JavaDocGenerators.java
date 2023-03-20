package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

public class JavaDocGenerators {
  private JavaDocGenerators() {}

  public static <A> Generator<A, PojoSettings> deprecatedValidationMethodJavaDoc() {
    return JavaDocGenerator.<A, PojoSettings>ofJavaDocString(
            "@deprecated This method is intended to be used only by frameworks for validation or serialisation.")
        .filter((a, settings) -> settings.getValidationMethods().isDeprecatedAnnotation());
  }
}
