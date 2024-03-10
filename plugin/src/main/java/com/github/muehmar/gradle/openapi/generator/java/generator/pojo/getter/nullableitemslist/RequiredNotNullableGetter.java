package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.nullableitemslist;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.CommonGetter.getterName;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.DeprecatedMethodGenerator.deprecatedJavaDocAndAnnotationForValidationMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters.isJacksonJsonOrValidation;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIncludeNonNull;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonProperty;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator.validationAnnotationsForMember;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.CommonGetter;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;

public class RequiredNotNullableGetter {
  private RequiredNotNullableGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> requiredNotNullableGetterGenerator() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(getterMethod())
        .appendSingleBlankLine()
        .append(frameworkGetter())
        .filter(JavaPojoMember::isRequiredAndNotNullable);
  }

  private static Generator<JavaPojoMember, PojoSettings> getterMethod() {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(
            member ->
                member
                    .getJavaType()
                    .getParameterizedClassName()
                    .asStringWrappingNullableValueType())
        .methodName(getterName())
        .noArguments()
        .doesNotThrow()
        .content(
            f ->
                String.format(
                    "return %s(%s);", WrapNullableItemsListMethod.METHOD_NAME, f.getName()))
        .build()
        .append(RefsGenerator.fieldRefs());
  }

  private static Generator<JavaPojoMember, PojoSettings> frameworkGetter() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .appendNewLine()
        .append(deprecatedJavaDocAndAnnotationForValidationMethod())
        .append(validationAnnotationsForMember())
        .append(jsonProperty())
        .append(jsonIncludeNonNull())
        .append(CommonGetter.rawGetterMethod())
        .filter(isJacksonJsonOrValidation());
  }
}
