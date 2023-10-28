package com.github.muehmar.gradle.openapi.generator.java.generator.shared;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationGenerator.validationAnnotationsForType;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import com.github.muehmar.gradle.openapi.generator.java.model.type.AnnotatedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.AnnotationsCreator;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;

public class JavaTypeGenerators {
  private JavaTypeGenerators() {}

  /**
   * Generator which generates a full class name of a type with deep validation annotations, i.e.
   * generic type parameters are annotated for arrays and maps.
   */
  public static Generator<JavaType, PojoSettings> deepAnnotatedFullClassName() {
    return (type, s, w) -> {
      final AnnotationsCreator annotationsCreator = annotationsCreatorForSettings(s);
      final AnnotatedClassName annotatedClass = type.getFullAnnotatedClassName(annotationsCreator);
      return annotatedClass
          .getImports()
          .foldLeft(w, Writer::ref)
          .println(annotatedClass.getClassName().asString());
    };
  }

  private static AnnotationsCreator annotationsCreatorForSettings(PojoSettings settings) {
    return valueType -> {
      final Writer annotationWriter =
          validationAnnotationsForType().generate(valueType, settings, javaWriter());
      return new AnnotationsCreator.Annotations(
          annotationWriter.asString().replaceAll("\\s+", " ").trim(), annotationWriter.getRefs());
    };
  }
}
