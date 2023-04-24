package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import static io.github.muehmar.codegenerator.Generator.constant;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.AnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.JavaDocGenerators;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.SettingsFunctions;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.ValidationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaComposedPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

public class AnyOfFoldValidationGenerator {

  private AnyOfFoldValidationGenerator() {}

  public static Generator<JavaComposedPojo, PojoSettings> generator() {
    final MethodGen<JavaComposedPojo, PojoSettings> method =
        MethodGenBuilder.<JavaComposedPojo, PojoSettings>create()
            .modifiers(SettingsFunctions::validationMethodModifiers)
            .noGenericTypes()
            .returnType("List<Object>")
            .methodName("getAnyOf")
            .noArguments()
            .content(methodContent())
            .build();

    return JavaDocGenerators.<JavaComposedPojo>deprecatedValidationMethodJavaDoc()
        .append(ValidationGenerator.validAnnotation())
        .append(AnnotationGenerator.deprecatedValidationMethod())
        .append(JacksonAnnotationGenerator.jsonIgnore())
        .append(method)
        .filter(JavaComposedPojo::isAnyOf);
  }

  private static Generator<JavaComposedPojo, PojoSettings> methodContent() {
    return Generator.<JavaComposedPojo, PojoSettings>emptyGen()
        .append(constant("if (getValidCount() == 0) {"))
        .append(constant("return null;"), 1)
        .append(constant("}"))
        .append(
            (p, s, w) ->
                w.println(
                    "return fold(%s);", p.getJavaPojos().map(name -> "dto -> dto").mkString(", ")));
  }
}
