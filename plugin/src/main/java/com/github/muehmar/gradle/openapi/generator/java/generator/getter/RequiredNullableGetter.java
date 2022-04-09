package com.github.muehmar.gradle.openapi.generator.java.generator.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.ValidationGenerator.assertTrue;
import static com.github.muehmar.gradle.openapi.generator.java.generator.ValidationGenerator.validationAnnotations;
import static com.github.muehmar.gradle.openapi.generator.java.generator.getter.CommonGetter.nullableGetterMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.getter.CommonGetter.wrapNullableInOptionalGetterMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static com.github.muehmar.gradle.openapi.generator.java.generator.jackson.JacksonAnnotationGenerator.jsonProperty;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PRIVATE;

import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import com.github.muehmar.gradle.openapi.generator.java.JavaResolver;
import com.github.muehmar.gradle.openapi.generator.java.generator.Filters;
import com.github.muehmar.gradle.openapi.generator.java.generator.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGenBuilder;
import java.util.function.BiPredicate;

public class RequiredNullableGetter {

  private static final JavaResolver RESOLVER = new JavaResolver();

  private RequiredNullableGetter() {}

  public static Generator<PojoMember, PojoSettings> getter() {
    final BiPredicate<PojoMember, PojoSettings> isJacksonJsonOrValidation =
        Filters.<PojoMember>isJacksonJson().or(Filters.isValidationEnabled());

    return Generator.<PojoMember, PojoSettings>emptyGen()
        .append(jsonIgnore())
        .append(wrapNullableInOptionalGetterMethod())
        .append(nullableGetterMethodWithAnnotations(isJacksonJsonOrValidation))
        .append(requiredValidationMethodWithAnnotation())
        .append(RefsGenerator.fieldRefs());
  }

  private static Generator<PojoMember, PojoSettings> nullableGetterMethodWithAnnotations(
      BiPredicate<PojoMember, PojoSettings> isJacksonJsonOrValidation) {
    return Generator.<PojoMember, PojoSettings>emptyGen()
        .appendNewLine()
        .append(validationAnnotations())
        .append(jsonProperty())
        .append(nullableGetterMethod())
        .filter(isJacksonJsonOrValidation);
  }

  private static Generator<PojoMember, PojoSettings> requiredValidationMethodWithAnnotation() {
    return Generator.<PojoMember, PojoSettings>emptyGen()
        .appendNewLine()
        .append(
            assertTrue(
                f -> String.format("%s is required but it is not present", f.memberName(RESOLVER))))
        .append(requiredValidationMethod())
        .filter(Filters.isValidationEnabled());
  }

  private static Generator<PojoMember, PojoSettings> requiredValidationMethod() {
    return MethodGenBuilder.<PojoMember, PojoSettings>create()
        .modifiers(PRIVATE)
        .noGenericTypes()
        .returnType("boolean")
        .methodName(
            field -> String.format("is%sPresent", field.memberName(RESOLVER).startUpperCase()))
        .noArguments()
        .content(
            field ->
                String.format("return is%sPresent;", field.memberName(RESOLVER).startUpperCase()))
        .build();
  }
}
