package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import static io.github.muehmar.codegenerator.Generator.constant;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.AnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.JavaDocGenerators;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.SettingsFunctions;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.ValidationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

public class AnyOfFoldValidationGenerator {

  private AnyOfFoldValidationGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> generator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendOptional(
            annotatedMethod(), p -> p.getAnyOfComposition().map(JavaAnyOfComposition::getPojos));
  }

  private static Generator<PList<JavaObjectPojo>, PojoSettings> annotatedMethod() {
    final MethodGen<PList<JavaObjectPojo>, PojoSettings> method =
        MethodGenBuilder.<PList<JavaObjectPojo>, PojoSettings>create()
            .modifiers(SettingsFunctions::validationMethodModifiers)
            .noGenericTypes()
            .returnType("List<Object>")
            .methodName("getAnyOf")
            .noArguments()
            .content(methodContent())
            .build();

    return JavaDocGenerators.<PList<JavaObjectPojo>>deprecatedValidationMethodJavaDoc()
        .append(ValidationGenerator.validAnnotation())
        .append(AnnotationGenerator.deprecatedValidationMethod())
        .append(JacksonAnnotationGenerator.jsonIgnore())
        .append(method);
  }

  private static Generator<PList<JavaObjectPojo>, PojoSettings> methodContent() {
    return Generator.<PList<JavaObjectPojo>, PojoSettings>emptyGen()
        .append(constant("if (getValidCount() == 0) {"))
        .append(constant("return null;"), 1)
        .append(constant("}"))
        .append(
            (pojos, s, w) ->
                w.println("return fold(%s);", pojos.map(name -> "dto -> dto").mkString(", ")));
  }
}
