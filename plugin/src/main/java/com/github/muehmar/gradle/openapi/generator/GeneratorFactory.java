package com.github.muehmar.gradle.openapi.generator;

import com.github.muehmar.gradle.openapi.generator.java.JavaPojoGenerator;
import com.github.muehmar.gradle.openapi.generator.settings.Language;
import com.github.muehmar.gradle.openapi.writer.FileWriter;

public class GeneratorFactory {
  private GeneratorFactory() {}

  public static PojoGenerator createGenerator(Language language, String outputDir) {
    if (language.equals(Language.JAVA)) {
      return new JavaPojoGenerator(() -> new FileWriter(outputDir));
    }

    throw new IllegalArgumentException("Not supported language " + language);
  }
}
