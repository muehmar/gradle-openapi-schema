package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.AnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.JavaDocGenerators;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.SettingsFunctions;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.ValidationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaComposedPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import java.util.function.Function;

public class ValidCountValidationMethod {
  private ValidCountValidationMethod() {}

  public static Generator<JavaComposedPojo, PojoSettings> generator() {
    return Generator.<JavaComposedPojo, PojoSettings>emptyGen()
        .append(isValidAgainstNoSchemaMethod())
        .append(isValidAgainstMoreThanOneSchema())
        .filter(Filters.isValidationEnabled());
  }

  private static Generator<JavaComposedPojo, PojoSettings> isValidAgainstNoSchemaMethod() {
    final Function<JavaComposedPojo, String> message =
        pojo ->
            String.format(
                "Is not valid against one of the schemas [%s]",
                pojo.getJavaPojos().map(JavaPojo::getSchemaName).mkString(", "));
    final Generator<JavaComposedPojo, PojoSettings> annotation =
        ValidationGenerator.assertFalse(message);
    final MethodGen<JavaComposedPojo, PojoSettings> method =
        MethodGenBuilder.<JavaComposedPojo, PojoSettings>create()
            .modifiers(SettingsFunctions::validationMethodModifiers)
            .noGenericTypes()
            .returnType("boolean")
            .methodName("isValidAgainstNoSchema")
            .noArguments()
            .content("return getValidCount() == 0;")
            .build();
    return JavaDocGenerators.<JavaComposedPojo>deprecatedValidationMethodJavaDoc()
        .append(annotation)
        .append(AnnotationGenerator.deprecatedValidationMethod())
        .append(JacksonAnnotationGenerator.jsonIgnore())
        .append(method);
  }

  private static Generator<JavaComposedPojo, PojoSettings> isValidAgainstMoreThanOneSchema() {
    final Function<JavaComposedPojo, String> message =
        pojo ->
            String.format(
                "Is valid against more than one of the schemas [%s]",
                pojo.getJavaPojos().map(JavaPojo::getSchemaName).mkString(", "));
    final Generator<JavaComposedPojo, PojoSettings> annotation =
        ValidationGenerator.assertFalse(message);
    final MethodGen<JavaComposedPojo, PojoSettings> method =
        MethodGenBuilder.<JavaComposedPojo, PojoSettings>create()
            .modifiers(SettingsFunctions::validationMethodModifiers)
            .noGenericTypes()
            .returnType("boolean")
            .methodName("isValidAgainstMoreThanOneSchema")
            .noArguments()
            .content("return getValidCount() > 1;")
            .build();
    return JavaDocGenerators.<JavaComposedPojo>deprecatedValidationMethodJavaDoc()
        .append(annotation)
        .append(AnnotationGenerator.deprecatedValidationMethod())
        .append(JacksonAnnotationGenerator.jsonIgnore())
        .append(method)
        .prependNewLine()
        .filter(JavaComposedPojo::isOneOf);
  }
}
