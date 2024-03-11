package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.nullableitemslist;

import static com.github.muehmar.gradle.openapi.generator.java.GeneratorUtil.noSettingsGen;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.CommonGetter.requiredValidationMethodWithAnnotation;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.DeprecatedMethodGenerator.deprecatedJavaDocAndAnnotationForValidationMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters.isJacksonJsonOrValidation;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonProperty;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator.validationAnnotationsForMember;
import static io.github.muehmar.codegenerator.java.JavaDocGenerator.javaDoc;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.CommonGetter;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

public class RequiredNullableGetter {
  private RequiredNullableGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> requiredNullableGetterGenerator() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(standardGetter())
        .appendSingleBlankLine()
        .append(alternateGetter())
        .appendSingleBlankLine()
        .append(frameworkGetter())
        .appendSingleBlankLine()
        .append(requiredValidationMethodWithAnnotation())
        .filter(JavaPojoMember::isRequiredAndNullable);
  }

  private static Generator<JavaPojoMember, PojoSettings> standardGetter() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(noSettingsGen(javaDoc()), JavaPojoMember::getDescription)
        .append(jsonIgnore())
        .append(NullableItemsListCommonGetter.wrapNullableInOptionalGetterMethod());
  }

  private static Generator<JavaPojoMember, PojoSettings> alternateGetter() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(noSettingsGen(javaDoc()), JavaPojoMember::getDescription)
        .append(jsonIgnore())
        .append(NullableItemsListCommonGetter.wrapNullableInOptionalGetterOrMethod());
  }

  private static Generator<JavaPojoMember, PojoSettings> frameworkGetter() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .appendNewLine()
        .append(deprecatedJavaDocAndAnnotationForValidationMethod())
        .append(validationAnnotationsForMember())
        .append(jsonProperty())
        .append(CommonGetter.rawGetterMethod())
        .filter(isJacksonJsonOrValidation());
  }
}
