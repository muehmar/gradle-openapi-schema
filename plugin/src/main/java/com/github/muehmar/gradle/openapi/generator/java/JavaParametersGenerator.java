package com.github.muehmar.gradle.openapi.generator.java;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.ParametersGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.parameter.JavaParameter;
import com.github.muehmar.gradle.openapi.generator.java.generator.parameter.ParameterGenerator;
import com.github.muehmar.gradle.openapi.generator.model.Parameter;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.writer.Writer;
import java.util.function.Supplier;

public class JavaParametersGenerator implements ParametersGenerator {
  private final Supplier<Writer> createWriter;

  public JavaParametersGenerator(Supplier<Writer> createWriter) {
    this.createWriter = createWriter;
  }

  @Override
  public void generate(PList<Parameter> parameters, PojoSettings settings) {
    final PList<JavaParameter> javaParameters = parameters.map(JavaParameter::wrap);
    generateJavaParameters(javaParameters, settings);
  }

  private void generateJavaParameters(PList<JavaParameter> parameters, PojoSettings settings) {
    parameters.forEach(parameter -> writeParameterClass(parameter, settings));
  }

  private void writeParameterClass(JavaParameter parameter, PojoSettings settings) {
    final String packagePath = settings.getPackageName().replace(".", "/").replaceFirst("^/", "");

    final Writer writer = createWriter.get();

    final ParameterGenerator parameterGenerator = new ParameterGenerator();
    final String output =
        parameterGenerator
            .generate(
                parameter, settings, io.github.muehmar.codegenerator.writer.Writer.createDefault())
            .asString();

    writer.print(output);

    writer.close(packagePath + "/parameter/" + parameter.getParamClassName() + ".java");
  }
}
