package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition;

import static com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames.Composition.AnyOf.foldAnyOfMethodName;
import static com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames.Composition.AnyOf.getAnyOfMethodName;
import static com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames.Composition.AnyOf.getAnyOfValidCountMethodName;
import static io.github.muehmar.codegenerator.Generator.constant;

import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.JavaDocGenerators;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.AnnotationGenerator;
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

  public static Generator<JavaObjectPojo, PojoSettings> anyOfFoldValidationGenerator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendOptional(
            annotatedMethod(), p -> p.getAnyOfComposition().map(JavaAnyOfComposition::getPojos));
  }

  private static Generator<NonEmptyList<JavaObjectPojo>, PojoSettings> annotatedMethod() {
    final MethodGen<NonEmptyList<JavaObjectPojo>, PojoSettings> method =
        MethodGenBuilder.<NonEmptyList<JavaObjectPojo>, PojoSettings>create()
            .modifiers(SettingsFunctions::validationMethodModifiers)
            .noGenericTypes()
            .returnType("List<Object>")
            .methodName(getAnyOfMethodName().asString())
            .noArguments()
            .content(methodContent())
            .build();

    return JavaDocGenerators.<NonEmptyList<JavaObjectPojo>>deprecatedValidationMethodJavaDoc()
        .append(ValidationGenerator.validAnnotation())
        .append(AnnotationGenerator.deprecatedValidationMethod())
        .append(JacksonAnnotationGenerator.jsonIgnore())
        .append(method);
  }

  private static Generator<NonEmptyList<JavaObjectPojo>, PojoSettings> methodContent() {
    return Generator.<NonEmptyList<JavaObjectPojo>, PojoSettings>emptyGen()
        .append(w -> w.println("if (%s() == 0) {", getAnyOfValidCountMethodName()))
        .append(constant("return null;"), 1)
        .append(constant("}"))
        .append(
            (pojos, s, w) ->
                w.println(
                    "return %s(%s);",
                    foldAnyOfMethodName(),
                    pojos.map(name -> "dto -> dto").toPList().mkString(", ")));
  }
}
