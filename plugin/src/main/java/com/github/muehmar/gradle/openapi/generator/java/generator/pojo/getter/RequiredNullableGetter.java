package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.GeneratorUtil.noSettingsGen;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.AnnotationGenerator.deprecatedValidationMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator.javaDoc;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.ValidationGenerator.assertTrue;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.ValidationGenerator.validationAnnotations;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonProperty;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.JavaDocGenerators;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterGenerator.RequiredNullableGetterGen;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.SettingsFunctions;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import java.util.function.BiPredicate;

public class RequiredNullableGetter {
  private RequiredNullableGetter() {}

  public static RequiredNullableGetterGen getter() {
    final BiPredicate<JavaPojoMember, PojoSettings> isJacksonJsonOrValidation =
        Filters.<JavaPojoMember>isJacksonJson().or(Filters.isValidationEnabled());

    final Generator<JavaPojoMember, PojoSettings> gen =
        Generator.<JavaPojoMember, PojoSettings>emptyGen()
            .append(standardGetter())
            .append(alternateGetter())
            .append(nullableGetterMethodWithAnnotations(isJacksonJsonOrValidation))
            .append(requiredValidationMethodWithAnnotation())
            .append(RefsGenerator.fieldRefs());
    return RequiredNullableGetterGen.wrap(gen);
  }

  private static Generator<JavaPojoMember, PojoSettings> standardGetter() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(noSettingsGen(javaDoc()), JavaPojoMember::getDescription)
        .append(jsonIgnore())
        .append(CommonGetter.wrapNullableInOptionalGetterMethod());
  }

  private static Generator<JavaPojoMember, PojoSettings> alternateGetter() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .appendNewLine()
        .append(noSettingsGen(javaDoc()), JavaPojoMember::getDescription)
        .append(jsonIgnore())
        .append(CommonGetter.wrapNullableInOptionalGetterOrMethod());
  }

  private static Generator<JavaPojoMember, PojoSettings> nullableGetterMethodWithAnnotations(
      BiPredicate<JavaPojoMember, PojoSettings> isJacksonJsonOrValidation) {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .appendNewLine()
        .append(JavaDocGenerators.deprecatedValidationMethodJavaDoc())
        .append(validationAnnotations())
        .append(jsonProperty())
        .append(deprecatedValidationMethod())
        .append(CommonGetter.rawGetterMethod())
        .filter(isJacksonJsonOrValidation);
  }

  private static Generator<JavaPojoMember, PojoSettings> requiredValidationMethodWithAnnotation() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .appendNewLine()
        .append(JavaDocGenerators.deprecatedValidationMethodJavaDoc())
        .append(assertTrue(f -> String.format("%s is required but it is not present", f.getName())))
        .append(deprecatedValidationMethod())
        .append(requiredValidationMethod())
        .filter(Filters.isValidationEnabled());
  }

  private static Generator<JavaPojoMember, PojoSettings> requiredValidationMethod() {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(SettingsFunctions::validationMethodModifiers)
        .noGenericTypes()
        .returnType("boolean")
        .methodName(field -> String.format("is%sPresent", field.getName().startUpperCase()))
        .noArguments()
        .content(field -> String.format("return is%sPresent;", field.getName().startUpperCase()))
        .build();
  }
}
