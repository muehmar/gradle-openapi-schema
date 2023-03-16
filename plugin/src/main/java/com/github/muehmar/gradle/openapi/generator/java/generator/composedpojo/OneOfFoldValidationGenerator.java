package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import static io.github.muehmar.codegenerator.Generator.constant;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.AnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.SettingsFunctions;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.ValidationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaComposedPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

public class OneOfFoldValidationGenerator {
  private OneOfFoldValidationGenerator() {}

  public static Generator<JavaComposedPojo, PojoSettings> generator() {
    final MethodGen<JavaComposedPojo, PojoSettings> method =
        MethodGenBuilder.<JavaComposedPojo, PojoSettings>create()
            .modifiers(SettingsFunctions::validationMethodModifiers)
            .noGenericTypes()
            .returnType("Object")
            .methodName("getOneOf")
            .noArguments()
            .content(methodContent())
            .build();

    return ValidationGenerator.<JavaComposedPojo>validAnnotation()
        .append(AnnotationGenerator.deprecatedValidationMethod())
        .append(JacksonAnnotationGenerator.jsonIgnore())
        .append(method)
        .filter(JavaComposedPojo::isOneOf);
  }

  private static Generator<JavaComposedPojo, PojoSettings> methodContent() {
    return Generator.<JavaComposedPojo, PojoSettings>emptyGen()
        .append(constant("if (getValidCount() != 1) {"))
        .append(constant("return null;"), 1)
        .append(constant("}"))
        .append(
            (p, s, w) ->
                w.println(
                    "return fold(%s, () -> null);",
                    p.getJavaPojos()
                        .map(jp -> jp.getName().asString().toLowerCase())
                        .map(name -> String.format("%s -> %s", name, name))
                        .mkString(", ")));
  }
}
