package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.GeneratorUtil.noSettingsGen;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.AnnotationGenerator.deprecatedRawGetter;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.NewValidationGenerator.assertTrue;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.NewValidationGenerator.validationAnnotations;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator.javaDoc;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonProperty;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.Filters;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.NewRefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterGenerator.RequiredNullableGetterGen;
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
            .append(NewRefsGenerator.fieldRefs());
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
        .append(validationAnnotations())
        .append(jsonProperty())
        .append(deprecatedRawGetter())
        .append(CommonGetter.rawGetterMethod())
        .filter(isJacksonJsonOrValidation);
  }

  private static Generator<JavaPojoMember, PojoSettings> requiredValidationMethodWithAnnotation() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .appendNewLine()
        .append(assertTrue(f -> String.format("%s is required but it is not present", f.getName())))
        .append(deprecatedRawGetter())
        .append(requiredValidationMethod())
        .filter(Filters.isValidationEnabled());
  }

  private static Generator<JavaPojoMember, PojoSettings> requiredValidationMethod() {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers((f, s) -> s.getRawGetter().getModifier().asJavaModifiers())
        .noGenericTypes()
        .returnType("boolean")
        .methodName(field -> String.format("is%sPresent", field.getName().startUpperCase()))
        .noArguments()
        .content(field -> String.format("return is%sPresent;", field.getName().startUpperCase()))
        .build();
  }
}