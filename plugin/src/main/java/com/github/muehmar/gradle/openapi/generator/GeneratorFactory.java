package com.github.muehmar.gradle.openapi.generator;

import com.github.muehmar.gradle.openapi.generator.java.JavaParametersGenerator;
import com.github.muehmar.gradle.openapi.generator.java.JavaPojoGenerator;
import com.github.muehmar.gradle.openapi.generator.java.JavaUtilsGenerator;
import com.github.muehmar.gradle.openapi.generator.settings.Language;

public class GeneratorFactory {
  private GeneratorFactory() {}

  public static Generators create(Language language) {
    if (language.equals(Language.JAVA)) {
      final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator();
      final JavaParametersGenerator parametersGenerator = new JavaParametersGenerator();
      final JavaUtilsGenerator utilsGenerator = new JavaUtilsGenerator();
      return new Generators(pojoGenerator, parametersGenerator, utilsGenerator);
    }

    throw new IllegalArgumentException("Not supported language " + language);
  }
}
