package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.nullableitemslist;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.CommonGetter.getterName;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.listmapping.MemberMapWriterBuilder.fullMemberMapWriterBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.DeprecatedMethodGenerator.deprecatedJavaDocAndAnnotationForValidationMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters.isJacksonJsonOrValidation;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIncludeNonNull;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonProperty;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator.validationAnnotationsForMember;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.CommonGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;

class RequiredNotNullableGetter {
  private RequiredNotNullableGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> requiredNotNullableGetterGenerator(
      GetterGenerator.GeneratorOption option) {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(getterMethod())
        .appendSingleBlankLine()
        .append(frameworkGetter(option))
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
        .content(getterMethodContent())
        .build()
        .append(RefsGenerator.fieldRefs());
  }

  private static Generator<JavaPojoMember, PojoSettings> getterMethodContent() {
    return (member, settings, writer) ->
        fullMemberMapWriterBuilder()
            .member(member)
            .prefix("return ")
            .mapListItemTypeNotNecessary()
            .wrapOptionalListItem()
            .mapListTypeNotNecessary()
            .wrapListNotNecessary()
            .trailingSemicolon()
            .build();
  }

  private static Generator<JavaPojoMember, PojoSettings> frameworkGetter(
      GetterGenerator.GeneratorOption option) {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .appendNewLine()
        .append(deprecatedJavaDocAndAnnotationForValidationMethod())
        .append(validationAnnotationsForMember().filter(option.validationFilter()))
        .append(jsonProperty())
        .append(jsonIncludeNonNull())
        .append(CommonGetter.rawGetterMethod(option))
        .filter(isJacksonJsonOrValidation());
  }
}
