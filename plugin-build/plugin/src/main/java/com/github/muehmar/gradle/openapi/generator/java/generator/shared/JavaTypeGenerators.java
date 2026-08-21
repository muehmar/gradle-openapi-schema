package com.github.muehmar.gradle.openapi.generator.java.generator.shared;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator.validationAnnotationsForPropertyType;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator.PropertyType;
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
  public static Generator<PropertyType, PojoSettings> deepAnnotatedParameterizedClassName() {
    return JavaTypeGenerators::createDeepAnnotatedParameterizedClassName;
  }

  private static Writer createDeepAnnotatedParameterizedClassName(
      PropertyType propertyType, PojoSettings settings, Writer writer) {
    final AtomicReference<PList<String>> imports = new AtomicReference<>(PList.empty());
    final String parameterizedClassNameWithAnnotations =
        propertyType
            .getType()
            .getParameterizedClassName()
            .asStringWithValueTypeAnnotations(
                valueType -> createAnnotationsString(propertyType, settings, valueType, imports));
    return imports
        .get()
        .foldLeft(writer, Writer::ref)
        .println(parameterizedClassNameWithAnnotations);
  }

  private static String createAnnotationsString(
      PropertyType propertyType,
      PojoSettings settings,
      JavaType valueType,
      AtomicReference<PList<String>> imports) {
    final PropertyType valuePropertyType =
        new PropertyType(propertyType.getPropertyInfoName(), valueType);
    final Writer annotationWriter =
        validationAnnotationsForPropertyType().generate(valuePropertyType, settings, javaWriter());
    imports.set(annotationWriter.getRefs());
    return annotationWriter.asString().replaceAll("\\s+", " ").trim();
  }
}
