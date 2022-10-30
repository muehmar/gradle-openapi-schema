package com.github.muehmar.gradle.openapi.generator;

import com.github.muehmar.gradle.openapi.generator.java.JavaParametersGenerator;
import com.github.muehmar.gradle.openapi.generator.java.JavaPojoGenerator;
import com.github.muehmar.gradle.openapi.generator.settings.Language;
import com.github.muehmar.gradle.openapi.writer.FileWriter;

public class GeneratorFactory {
  private GeneratorFactory() {}

  public static Generators create(Language language, String outputDir) {
    if (language.equals(Language.JAVA)) {
      final JavaPojoGenerator pojoGenerator =
          new JavaPojoGenerator(() -> new FileWriter(outputDir));
      final JavaParametersGenerator parametersGenerator =
          new JavaParametersGenerator(() -> new FileWriter(outputDir));
      return new Generators(pojoGenerator, parametersGenerator);
    }

    throw new IllegalArgumentException("Not supported language " + language);
  }
}
