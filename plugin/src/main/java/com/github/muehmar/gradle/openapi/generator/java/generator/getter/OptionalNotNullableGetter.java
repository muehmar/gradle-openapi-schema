package com.github.muehmar.gradle.openapi.generator.java.generator.getter;

import static com.github.muehmar.gradle.openapi.generator.java.GeneratorUtil.noSettingsGen;
import static com.github.muehmar.gradle.openapi.generator.java.generator.AnnotationGenerator.deprecatedValidationGetter;
import static com.github.muehmar.gradle.openapi.generator.java.generator.Filters.isValidationEnabled;
import static com.github.muehmar.gradle.openapi.generator.java.generator.JavaDocGenerator.javaDoc;
import static com.github.muehmar.gradle.openapi.generator.java.generator.ValidationGenerator.validationAnnotations;
import static com.github.muehmar.gradle.openapi.generator.java.generator.getter.CommonGetter.nullableGetterMethodForValidation;
import static com.github.muehmar.gradle.openapi.generator.java.generator.getter.CommonGetter.wrapNullableInOptionalGetterMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.getter.CommonGetter.wrapNullableInOptionalGetterOrMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static com.github.muehmar.gradle.openapi.generator.java.generator.jackson.JacksonAnnotationGenerator.jsonIncludeNonNull;
import static com.github.muehmar.gradle.openapi.generator.java.generator.jackson.JacksonAnnotationGenerator.jsonProperty;

import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import com.github.muehmar.gradle.openapi.generator.java.generator.Filters;
import com.github.muehmar.gradle.openapi.generator.java.generator.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.Generator;

public class OptionalNotNullableGetter {
  private OptionalNotNullableGetter() {}

  public static Generator<PojoMember, PojoSettings> getter() {
    return Generator.<PojoMember, PojoSettings>emptyGen()
        .append(standardGetter())
        .append(alternateGetter())
        .append(jacksonOrValidationGetter())
        .append(RefsGenerator.fieldRefs());
  }

  private static Generator<PojoMember, PojoSettings> standardGetter() {
    return Generator.<PojoMember, PojoSettings>emptyGen()
        .append(noSettingsGen(javaDoc()), PojoMember::getDescription)
        .append(jsonIgnore())
        .append(wrapNullableInOptionalGetterMethod());
  }

  private static Generator<PojoMember, PojoSettings> alternateGetter() {
    return Generator.<PojoMember, PojoSettings>emptyGen()
        .appendNewLine()
        .append(noSettingsGen(javaDoc()), PojoMember::getDescription)
        .append(jsonIgnore())
        .append(wrapNullableInOptionalGetterOrMethod());
  }

  private static Generator<PojoMember, PojoSettings> jacksonOrValidationGetter() {
    return Generator.<PojoMember, PojoSettings>emptyGen()
        .appendNewLine()
        .append(jsonProperty())
        .append(jsonIncludeNonNull())
        .append(validationAnnotations())
        .append(deprecatedValidationGetter())
        .append(nullableGetterMethodForValidation())
        .filter(Filters.<PojoMember>isJacksonJson().or(isValidationEnabled()));
  }
}
