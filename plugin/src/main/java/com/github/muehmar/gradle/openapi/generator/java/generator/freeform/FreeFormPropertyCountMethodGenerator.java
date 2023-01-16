package com.github.muehmar.gradle.openapi.generator.java.generator.freeform;

import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.ValidationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaFreeFormPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

public class FreeFormPropertyCountMethodGenerator {
  private FreeFormPropertyCountMethodGenerator() {}

  private static final String JAVA_DOC = "Returns the number of present properties of this object.";

  public static Generator<JavaFreeFormPojo, PojoSettings> propertyCountMethod() {
    final MethodGen<JavaFreeFormPojo, PojoSettings> method =
        MethodGenBuilder.<JavaFreeFormPojo, PojoSettings>create()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType("int")
            .methodName("getPropertyCount")
            .noArguments()
            .content(pojo -> String.format("return %s.size();", pojo.getMember().getName()))
            .build();
    return JavaDocGenerator.<JavaFreeFormPojo, PojoSettings>ofJavaDocString(JAVA_DOC)
        .append(
            ValidationGenerator.minAnnotationForPropertyCount(), JavaFreeFormPojo::getConstraints)
        .append(
            ValidationGenerator.maxAnnotationForPropertyCount(), JavaFreeFormPojo::getConstraints)
        .append(JacksonAnnotationGenerator.jsonIgnore())
        .append(method);
  }
}
