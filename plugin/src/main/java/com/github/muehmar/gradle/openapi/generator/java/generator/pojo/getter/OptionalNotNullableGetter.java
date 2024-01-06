package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.GeneratorUtil.noSettingsGen;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.DeprecatedMethodGenerator.deprecatedJavaDocAndAnnotationForValidationMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters.isValidationEnabled;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIncludeNonNull;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonProperty;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator.assertTrue;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator.validationAnnotationsForMember;
import static io.github.muehmar.codegenerator.java.JavaDocGenerator.javaDoc;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterGenerator.GeneratorOption;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.SettingsFunctions;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;

class OptionalNotNullableGetter {
  private OptionalNotNullableGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> optionalNotNullableGetterGenerator(
      GeneratorOption option) {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(standardGetter())
        .append(alternateGetter())
        .append(rawGetter(option))
        .append(notNullableValidationMethodWithAnnotation())
        .append(RefsGenerator.fieldRefs())
        .filter(JavaPojoMember::isOptionalAndNotNullable);
  }

  private static Generator<JavaPojoMember, PojoSettings> standardGetter() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(noSettingsGen(javaDoc()), JavaPojoMember::getDescription)
        .append(jsonIgnore())
        .append(CommonGetter.wrapNullableInOptionalGetterMethod(PUBLIC));
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
        .append(deprecatedJavaDocAndAnnotationForValidationMethod())
        .append(jsonProperty())
        .append(jsonIncludeNonNull())
        .append(validationAnnotationsForMember().filter(option.validationFilter()))
        .append(CommonGetter.rawGetterMethod())
        .filter(Filters.<JavaPojoMember>isJacksonJson().or(isValidationEnabled()));
  }

  private static Generator<JavaPojoMember, PojoSettings>
      notNullableValidationMethodWithAnnotation() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .appendNewLine()
        .append(deprecatedJavaDocAndAnnotationForValidationMethod())
        .append(
            assertTrue(
                f -> String.format("%s is required to be non-null but is null", f.getName())))
        .append(jsonIgnore())
        .append(notNullableValidationMethod())
        .filter(Filters.isValidationEnabled());
  }

  private static Generator<JavaPojoMember, PojoSettings> notNullableValidationMethod() {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(SettingsFunctions::validationMethodModifiers)
        .noGenericTypes()
        .returnType("boolean")
        .methodName(member -> member.getIsNotNullFlagName().asString())
        .noArguments()
        .doesNotThrow()
        .content(member -> String.format("return %s;", member.getIsNotNullFlagName()))
        .build();
  }
}
