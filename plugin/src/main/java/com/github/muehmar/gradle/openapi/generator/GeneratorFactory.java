package com.github.muehmar.gradle.openapi.generator;

import com.github.muehmar.gradle.openapi.generator.java.JavaPojoGenerator;
import com.github.muehmar.gradle.openapi.generator.java.JavaPojoMapper;
import com.github.muehmar.gradle.openapi.generator.java.NewJavaPojoGenerator;
import com.github.muehmar.gradle.openapi.generator.settings.Language;
import com.github.muehmar.gradle.openapi.writer.FileWriter;

public class GeneratorFactory {
  private GeneratorFactory() {}

  public static OpenApiGenerator create(Language language, String outputDir) {
    if (language.equals(Language.JAVA)) {
      final PojoGenerator pojoGenerator = new JavaPojoGenerator(() -> new FileWriter(outputDir));
      final PojoMapper pojoMapper = new JavaPojoMapper();
      return new OpenApiGenerator(pojoMapper, pojoGenerator);
    }

    throw new IllegalArgumentException("Not supported language " + language);
  }

  public static NewPojoGenerator createGenerator(Language language, String outputDir) {
    if (language.equals(Language.JAVA)) {
      return new NewJavaPojoGenerator(() -> new FileWriter(outputDir));
    }

    throw new IllegalArgumentException("Not supported language " + language);
  }
}
