package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition;

import static com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames.Composition.OneOf.foldOneOfMethodName;
import static com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames.Composition.OneOf.getOneOfMethodName;
import static com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames.Composition.OneOf.getOneOfValidCountMethodName;
import static io.github.muehmar.codegenerator.Generator.constant;

import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.JavaDocGenerators;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.AnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.SettingsFunctions;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

public class OneOfFoldValidationGenerator {
  private OneOfFoldValidationGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> oneOfFoldValidationGenerator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendOptional(
            annotatedMethod(),
            pojo -> pojo.getOneOfComposition().map(JavaOneOfComposition::getPojos));
  }

  private static Generator<NonEmptyList<JavaObjectPojo>, PojoSettings> annotatedMethod() {
    final MethodGen<NonEmptyList<JavaObjectPojo>, PojoSettings> method =
        MethodGenBuilder.<NonEmptyList<JavaObjectPojo>, PojoSettings>create()
            .modifiers(SettingsFunctions::validationMethodModifiers)
            .noGenericTypes()
            .returnType("Object")
            .methodName(getOneOfMethodName().asString())
            .noArguments()
            .content(methodContent())
            .build();

    return JavaDocGenerators.<NonEmptyList<JavaObjectPojo>>deprecatedValidationMethodJavaDoc()
        .append(ValidationAnnotationGenerator.validAnnotation())
        .append(AnnotationGenerator.deprecatedValidationMethod())
        .append(JacksonAnnotationGenerator.jsonIgnore())
        .append(method);
  }

  private static Generator<NonEmptyList<JavaObjectPojo>, PojoSettings> methodContent() {
    return Generator.<NonEmptyList<JavaObjectPojo>, PojoSettings>emptyGen()
        .append(w -> w.println("if (%s() != 1) {", getOneOfValidCountMethodName()))
        .append(constant("return null;"), 1)
        .append(constant("}"))
        .append(
            (pojos, s, w) ->
                w.println(
                    "return %s(%s, () -> null);",
                    foldOneOfMethodName(),
                    pojos.map(name -> "dto -> dto").toPList().mkString(", ")));
  }
}
