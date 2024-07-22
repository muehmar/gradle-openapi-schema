package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.GeneratorUtil.noSettingsGen;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.CommonGetter.requiredValidationMethodWithAnnotation;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.DeprecatedMethodGenerator.deprecatedJavaDocAndAnnotationForValidationMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters.isJacksonJsonOrValidation;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonProperty;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator.validationAnnotationsForMember;
import static io.github.muehmar.codegenerator.java.JavaDocGenerator.javaDoc;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

class RequiredNullableGetter {
  private RequiredNullableGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> requiredNullableGetterGenerator(
      GetterType getterType) {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(standardGetter())
        .appendSingleBlankLine()
        .append(alternateGetter())
        .appendSingleBlankLine()
        .append(nullableGetterMethodWithAnnotations(getterType))
        .appendSingleBlankLine()
        .append(requiredValidationMethodWithAnnotation().filter(getterType.validationFilter()))
        .append(RefsGenerator.fieldRefs())
        .filter(JavaPojoMember::isRequiredAndNullable);
  }

  private static Generator<JavaPojoMember, PojoSettings> standardGetter() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(noSettingsGen(javaDoc()), JavaPojoMember::getDescription)
        .append(jsonIgnore())
        .append(CommonGetter.wrapNullableInOptionalGetterMethod(PUBLIC));
  }

  private static Generator<JavaPojoMember, PojoSettings> alternateGetter() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(noSettingsGen(javaDoc()), JavaPojoMember::getDescription)
        .append(jsonIgnore())
        .append(CommonGetter.wrapNullableInOptionalGetterOrMethod());
  }

  private static Generator<JavaPojoMember, PojoSettings> nullableGetterMethodWithAnnotations(
      GetterType getterType) {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(deprecatedJavaDocAndAnnotationForValidationMethod())
        .append(validationAnnotationsForMember().filter(getterType.validationFilter()))
        .append(jsonProperty())
        .append(CommonGetter.rawGetterMethod(getterType))
        .filter(isJacksonJsonOrValidation());
  }
}
