package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition;

import static com.github.muehmar.gradle.openapi.generator.java.model.composition.DiscriminatableJavaComposition.Type.ONE_OF;
import static com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames.Composition.foldCompositionMethodName;
import static com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames.Composition.getCompositionMethodName;
import static com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames.Composition.getValidCountMethodName;
import static io.github.muehmar.codegenerator.Generator.constant;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.DeprecatedMethodGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.SettingsFunctions;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.DiscriminatableJavaComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

/**
 * Creates a method for validation which returns all instances of the composition which are
 * considered valid. The returned instances are then validated by the validation framework.
 */
public class FoldValidationGenerator {
  private FoldValidationGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> foldValidationGenerator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(annotatedMethod(), JavaObjectPojo::getDiscriminatableCompositions);
  }

  private static Generator<DiscriminatableJavaComposition, PojoSettings> annotatedMethod() {
    final MethodGen<DiscriminatableJavaComposition, PojoSettings> method =
        MethodGenBuilder.<DiscriminatableJavaComposition, PojoSettings>create()
            .modifiers(SettingsFunctions::validationMethodModifiers)
            .noGenericTypes()
            .returnType("Object")
            .methodName(comp -> getCompositionMethodName(comp.getType()).asString())
            .noArguments()
            .doesNotThrow()
            .content(methodContent2())
            .build();

    return DeprecatedMethodGenerator
        .<DiscriminatableJavaComposition>deprecatedJavaDocAndAnnotationForValidationMethod()
        .append(ValidationAnnotationGenerator.validAnnotation())
        .append(JacksonAnnotationGenerator.jsonIgnore())
        .append(method);
  }

  private static Generator<DiscriminatableJavaComposition, PojoSettings> methodContent2() {
    return Generator.<DiscriminatableJavaComposition, PojoSettings>emptyGen()
        .append(
            (comp, s, w) ->
                w.println(
                    "if (%s() %s) {",
                    getValidCountMethodName(comp.getType()), getCountCondition(comp.getType())))
        .append(constant("return null;"), 1)
        .append(constant("}"))
        .append(
            (composition, s, w) ->
                w.println(
                    "return %s(%s%s);",
                    foldCompositionMethodName(composition.getType()),
                    composition.getPojos().map(name -> "dto -> dto").toPList().mkString(", "),
                    additionalFoldArguments(composition.getType())));
  }

  private static String getCountCondition(DiscriminatableJavaComposition.Type type) {
    return type.equals(ONE_OF) ? "!= 1" : "== 0";
  }

  private static String additionalFoldArguments(DiscriminatableJavaComposition.Type type) {
    return type.equals(ONE_OF) ? ", () -> null" : "";
  }
}
