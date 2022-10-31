package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.GeneratorUtil.noSettingsGen;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.AnnotationGenerator.deprecatedRawGetter;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.Filters.isValidationEnabled;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.JavaDocGenerator.javaDoc;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.NewValidationGenerator.validationAnnotations;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.jackson.JacksonAnnotationGenerator.jsonIncludeNonNull;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.jackson.JacksonAnnotationGenerator.jsonProperty;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.Filters;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.NewRefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterGenerator.OptionalNotNullableGetterGen;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

public class OptionalNotNullableGetter {
  private OptionalNotNullableGetter() {}

  public static OptionalNotNullableGetterGen getter() {
    final Generator<JavaPojoMember, PojoSettings> gen =
        Generator.<JavaPojoMember, PojoSettings>emptyGen()
            .append(standardGetter())
            .append(alternateGetter())
            .append(rawGetter())
            .append(NewRefsGenerator.fieldRefs());
    return OptionalNotNullableGetterGen.wrap(gen);
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

  private static Generator<JavaPojoMember, PojoSettings> rawGetter() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .appendNewLine()
        .append(jsonProperty())
        .append(jsonIncludeNonNull())
        .append(validationAnnotations())
        .append(deprecatedRawGetter())
        .append(CommonGetter.rawGetterMethod())
        .filter(Filters.<JavaPojoMember>isJacksonJson().or(isValidationEnabled()));
  }
}
