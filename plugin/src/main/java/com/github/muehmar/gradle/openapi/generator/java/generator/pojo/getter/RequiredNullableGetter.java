package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.GeneratorUtil.noSettingsGen;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.AnnotationGenerator.deprecatedValidationMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonProperty;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationGenerator.assertTrue;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationGenerator.validationAnnotationsForMember;
import static io.github.muehmar.codegenerator.java.JavaDocGenerator.javaDoc;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.JavaDocGenerators;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterGenerator.GeneratorOption;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.SettingsFunctions;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import java.util.function.BiPredicate;

class RequiredNullableGetter {
  private RequiredNullableGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> requiredNullableGetterGenerator(
      GeneratorOption option) {
    final BiPredicate<JavaPojoMember, PojoSettings> isJacksonJsonOrValidation =
        Filters.<JavaPojoMember>isJacksonJson().or(Filters.isValidationEnabled());

    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(standardGetter())
        .append(alternateGetter())
        .append(nullableGetterMethodWithAnnotations(isJacksonJsonOrValidation, option))
        .append(requiredValidationMethodWithAnnotation())
        .append(RefsGenerator.fieldRefs())
        .filter(JavaPojoMember::isRequiredAndNullable);
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
      BiPredicate<JavaPojoMember, PojoSettings> isJacksonJsonOrValidation, GeneratorOption option) {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .appendNewLine()
        .append(JavaDocGenerators.deprecatedValidationMethodJavaDoc())
        .append(validationAnnotationsForMember().filter(option.validationFilter()))
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
        .methodName(field -> field.getIsPresentFlagName().asString())
        .noArguments()
        .content(field -> String.format("return %s;", field.getIsPresentFlagName()))
        .build();
  }
}
