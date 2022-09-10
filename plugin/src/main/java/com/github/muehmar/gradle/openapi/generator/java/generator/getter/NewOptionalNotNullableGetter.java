package com.github.muehmar.gradle.openapi.generator.java.generator.getter;

import static com.github.muehmar.gradle.openapi.generator.java.GeneratorUtil.noSettingsGen;
import static com.github.muehmar.gradle.openapi.generator.java.generator.AnnotationGenerator.deprecatedRawGetter;
import static com.github.muehmar.gradle.openapi.generator.java.generator.Filters.isValidationEnabled;
import static com.github.muehmar.gradle.openapi.generator.java.generator.JavaDocGenerator.javaDoc;
import static com.github.muehmar.gradle.openapi.generator.java.generator.NewValidationGenerator.validationAnnotations;
import static com.github.muehmar.gradle.openapi.generator.java.generator.getter.NewCommonGetter.rawGetterMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.getter.NewCommonGetter.wrapNullableInOptionalGetterMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.getter.NewCommonGetter.wrapNullableInOptionalGetterOrMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.jackson.NewJacksonAnnotationGenerator.jsonIgnore;
import static com.github.muehmar.gradle.openapi.generator.java.generator.jackson.NewJacksonAnnotationGenerator.jsonIncludeNonNull;
import static com.github.muehmar.gradle.openapi.generator.java.generator.jackson.NewJacksonAnnotationGenerator.jsonProperty;

import com.github.muehmar.gradle.openapi.generator.java.generator.Filters;
import com.github.muehmar.gradle.openapi.generator.java.generator.NewRefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

public class NewOptionalNotNullableGetter {
  private NewOptionalNotNullableGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> getter() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(standardGetter())
        .append(alternateGetter())
        .append(rawGetter())
        .append(NewRefsGenerator.fieldRefs());
  }

  private static Generator<JavaPojoMember, PojoSettings> standardGetter() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(noSettingsGen(javaDoc()), JavaPojoMember::getDescription)
        .append(jsonIgnore())
        .append(wrapNullableInOptionalGetterMethod());
  }

  private static Generator<JavaPojoMember, PojoSettings> alternateGetter() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .appendNewLine()
        .append(noSettingsGen(javaDoc()), JavaPojoMember::getDescription)
        .append(jsonIgnore())
        .append(wrapNullableInOptionalGetterOrMethod());
  }

  private static Generator<JavaPojoMember, PojoSettings> rawGetter() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .appendNewLine()
        .append(jsonProperty())
        .append(jsonIncludeNonNull())
        .append(validationAnnotations())
        .append(deprecatedRawGetter())
        .append(rawGetterMethod())
        .filter(Filters.<JavaPojoMember>isJacksonJson().or(isValidationEnabled()));
  }
}
