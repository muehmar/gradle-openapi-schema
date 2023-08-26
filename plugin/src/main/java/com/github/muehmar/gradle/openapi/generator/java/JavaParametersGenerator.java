package com.github.muehmar.gradle.openapi.generator.java;

import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import com.github.muehmar.gradle.openapi.generator.ParametersGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.parameter.JavaParameter;
import com.github.muehmar.gradle.openapi.generator.java.generator.parameter.ParameterGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaFileName;
import com.github.muehmar.gradle.openapi.generator.model.Parameter;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.writer.GeneratedFile;

public class JavaParametersGenerator implements ParametersGenerator {

  @Override
  public GeneratedFile generate(Parameter parameter, PojoSettings settings) {
    return generateJavaParameter(JavaParameter.wrap(parameter), settings);
  }

  private GeneratedFile generateJavaParameter(JavaParameter parameter, PojoSettings settings) {

    final ParameterGenerator parameterGenerator = new ParameterGenerator();
    final String content =
        parameterGenerator.generate(parameter, settings, javaWriter()).asString();

    final JavaFileName javaFileName = JavaFileName.fromSettingsAndParameter(settings, parameter);
    return new GeneratedFile(javaFileName.asPath(), content);
  }
}
