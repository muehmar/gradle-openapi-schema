package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.GeneratorUtil.noSettingsGen;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.AnnotationGenerator.deprecatedValidationMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters.isValidationEnabled;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator.javaDoc;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.ValidationGenerator.validationAnnotationsForMember;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIncludeNonNull;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonProperty;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.JavaDocGenerators;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterGenerator.GeneratorOption;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

class OptionalNotNullableGetter {
  private OptionalNotNullableGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> optionalNotNullableGetterGenerator(
      GeneratorOption option) {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(standardGetter())
        .append(alternateGetter())
        .append(rawGetter(option))
        .append(RefsGenerator.fieldRefs())
        .filter(JavaPojoMember::isOptionalAndNotNullable);
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

  private static Generator<JavaPojoMember, PojoSettings> rawGetter(GeneratorOption option) {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .appendNewLine()
        .append(JavaDocGenerators.deprecatedValidationMethodJavaDoc())
        .append(jsonProperty())
        .append(jsonIncludeNonNull())
        .append(validationAnnotationsForMember().filter(option.validationFilter()))
        .append(deprecatedValidationMethod())
        .append(CommonGetter.rawGetterMethod())
        .filter(Filters.<JavaPojoMember>isJacksonJson().or(isValidationEnabled()));
  }
}
