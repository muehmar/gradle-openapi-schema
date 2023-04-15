package com.github.muehmar.gradle.openapi.generator.java;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.UtilsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.TristateGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonNullContainerGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaFileName;
import com.github.muehmar.gradle.openapi.writer.GeneratedFile;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;

public class JavaUtilsGenerator implements UtilsGenerator {
  @Override
  public PList<GeneratedFile> generateUtils() {
    return PList.of(tristateClass(), jacksonContainerClass());
  }

  private static GeneratedFile tristateClass() {
    final Generator<Void, Void> tristateGen = TristateGenerator.tristateClass();
    final Writer writer = tristateGen.generate(noData(), noSettings(), Writer.createDefault());
    final JavaFileName javaFileName = JavaFileName.fromRef(OpenApiUtilRefs.TRISTATE);
    return new GeneratedFile(javaFileName.asPath(), writer.asString());
  }

  private static GeneratedFile jacksonContainerClass() {
    final Generator<Void, Void> jacksonContainerGen =
        JacksonNullContainerGenerator.containerClass();
    final Writer writer =
        jacksonContainerGen.generate(noData(), noSettings(), Writer.createDefault());
    final JavaFileName javaFileName = JavaFileName.fromRef(OpenApiUtilRefs.JACKSON_NULL_CONTAINER);
    return new GeneratedFile(javaFileName.asPath(), writer.asString());
  }

  private static Void noData() {
    return null;
  }

  private static Void noSettings() {
    return null;
  }
}