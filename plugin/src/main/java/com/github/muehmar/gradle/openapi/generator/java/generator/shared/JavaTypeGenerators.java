package com.github.muehmar.gradle.openapi.generator.java.generator.shared;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator.validationAnnotationsForPropertyType;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.concurrent.atomic.AtomicReference;

public class JavaTypeGenerators {
  private JavaTypeGenerators() {}

  /**
   * Generator which generates a full class name of a type with deep validation annotations, i.e.
   * generic type parameters are annotated for arrays and maps.
   */
  public static Generator<ValidationAnnotationGenerator.PropertyType, PojoSettings>
      deepAnnotatedParameterizedClassName() {
    return JavaTypeGenerators::createDeepAnnotatedParameterizedClassName;
  }

  private static Writer createDeepAnnotatedParameterizedClassName(
      ValidationAnnotationGenerator.PropertyType propertyType, PojoSettings s, Writer w) {
    final AtomicReference<PList<String>> imports = new AtomicReference<>(PList.empty());
    final String parameterizedClassNameWithAnnotations =
        propertyType
            .getType()
            .getParameterizedClassName()
            .asStringWithValueTypeAnnotations(
                valueType -> createAnnotationsString(propertyType, s, valueType, imports));
    return imports.get().foldLeft(w, Writer::ref).println(parameterizedClassNameWithAnnotations);
  }

  private static String createAnnotationsString(
      ValidationAnnotationGenerator.PropertyType propertyType,
      PojoSettings s,
      JavaType valueType,
      AtomicReference<PList<String>> imports) {
    final ValidationAnnotationGenerator.PropertyType valuePropertyType =
        new ValidationAnnotationGenerator.PropertyType(
            propertyType.getPropertyInfoName(), valueType);
    final Writer annotationWriter =
        validationAnnotationsForPropertyType().generate(valuePropertyType, s, javaWriter());
    imports.set(annotationWriter.getRefs());
    return annotationWriter.asString().replaceAll("\\s+", " ").trim();
  }
}
