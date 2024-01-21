package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition;

import static com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames.Composition.OneOf.isValidAgainstMoreThanOneSchemaMethodName;
import static com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames.Composition.getValidCountMethodName;
import static com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames.Composition.isValidAgainstNoSchemaMethodName;
import static io.github.muehmar.codegenerator.Generator.newLine;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.DeprecatedMethodGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.SettingsFunctions;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.DiscriminatableJavaComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import java.util.function.Function;

public class ValidCountValidationMethod {
  private ValidCountValidationMethod() {}

  public static Generator<JavaObjectPojo, PojoSettings> validCountValidationMethodGenerator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(
            isValidAgainstNoSchemaMethod(),
            JavaObjectPojo::getDiscriminatableCompositions,
            newLine())
        .appendOptional(isValidAgainstMoreThanOneSchema(), JavaObjectPojo::getOneOfComposition)
        .filter(Filters.isValidationEnabled());
  }

  private static Generator<DiscriminatableJavaComposition, PojoSettings>
      isValidAgainstNoSchemaMethod() {
    final Function<DiscriminatableJavaComposition, String> message =
        composition ->
            String.format(
                "Is not valid against one of the schemas [%s]",
                composition.getPojos().map(JavaObjectPojo::getSchemaName).toPList().mkString(", "));
    final Generator<DiscriminatableJavaComposition, PojoSettings> annotation =
        ValidationAnnotationGenerator.assertFalse(message);
    final MethodGen<DiscriminatableJavaComposition, PojoSettings> method =
        MethodGenBuilder.<DiscriminatableJavaComposition, PojoSettings>create()
            .modifiers(SettingsFunctions::validationMethodModifiers)
            .noGenericTypes()
            .returnType("boolean")
            .methodName(
                composition -> isValidAgainstNoSchemaMethodName(composition.getType()).asString())
            .noArguments()
            .doesNotThrow()
            .content(
                (composition, s, w) ->
                    w.println("return %s() == 0;", getValidCountMethodName(composition.getType())))
            .build();
    return DeprecatedMethodGenerator
        .<DiscriminatableJavaComposition>deprecatedJavaDocAndAnnotationForValidationMethod()
        .append(annotation)
        .append(JacksonAnnotationGenerator.jsonIgnore())
        .append(method);
  }

  private static <T extends DiscriminatableJavaComposition>
      Generator<T, PojoSettings> isValidAgainstMoreThanOneSchema() {
    final Function<T, String> message =
        composition ->
            String.format(
                "Is valid against more than one of the schemas [%s]",
                composition.getPojos().map(JavaPojo::getSchemaName).toPList().mkString(", "));
    final Generator<T, PojoSettings> annotation =
        ValidationAnnotationGenerator.assertFalse(message);
    final MethodGen<T, PojoSettings> method =
        MethodGenBuilder.<T, PojoSettings>create()
            .modifiers(SettingsFunctions::validationMethodModifiers)
            .noGenericTypes()
            .returnType("boolean")
            .methodName(isValidAgainstMoreThanOneSchemaMethodName().asString())
            .noArguments()
            .doesNotThrow()
            .content(
                composition ->
                    String.format(
                        "return %s() > 1;", getValidCountMethodName(composition.getType())))
            .build();
    return DeprecatedMethodGenerator.<T>deprecatedJavaDocAndAnnotationForValidationMethod()
        .append(annotation)
        .append(JacksonAnnotationGenerator.jsonIgnore())
        .append(method)
        .prependNewLine();
  }
}
