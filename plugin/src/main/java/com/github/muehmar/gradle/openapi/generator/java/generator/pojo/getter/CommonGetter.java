package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.DeprecatedMethodGenerator.deprecatedJavaDocAndAnnotationForValidationMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters.isJacksonJson;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaTypeGenerators.deepAnnotatedParameterizedClassName;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIncludeNonNull;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonProperty;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator.assertTrue;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;
import static io.github.muehmar.codegenerator.java.MethodGen.Argument.argument;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.SettingsFunctions;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.java.JavaModifier;
import io.github.muehmar.codegenerator.java.JavaModifiers;
import java.util.function.BiFunction;

public class CommonGetter {
  private CommonGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> getterMethod(
      JavaModifier... javaModifiers) {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(JavaModifiers.of(javaModifiers))
        .noGenericTypes()
        .returnType(
            deepAnnotatedParameterizedClassName()
                .contraMap(ValidationAnnotationGenerator.PropertyType::fromMember))
        .methodName(getterName())
        .noArguments()
        .doesNotThrow()
        .content(f -> String.format("return %s;", f.getName()))
        .build()
        .append(RefsGenerator.fieldRefs());
  }

  public static Generator<JavaPojoMember, PojoSettings> wrapNullableInOptionalGetterMethod(
      JavaModifier... javaModifiers) {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(JavaModifiers.of(javaModifiers))
        .noGenericTypes()
        .returnType(f -> String.format("Optional<%s>", f.getJavaType().getParameterizedClassName()))
        .methodName(getterName())
        .noArguments()
        .doesNotThrow()
        .content(f -> String.format("return Optional.ofNullable(%s);", f.getName()))
        .build()
        .append(w -> w.ref(JavaRefs.JAVA_UTIL_OPTIONAL));
  }

  public static Generator<JavaPojoMember, PojoSettings> wrapNullableInOptionalGetterOrMethod() {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(f -> f.getJavaType().getParameterizedClassName())
        .methodName(f -> String.format("%sOr", f.getGetterName()))
        .singleArgument(f -> argument(f.getJavaType().getParameterizedClassName(), "defaultValue"))
        .doesNotThrow()
        .content(
            f ->
                String.format(
                    "return this.%s == null ? defaultValue : this.%s;", f.getName(), f.getName()))
        .build()
        .append(w -> w.ref(JavaRefs.JAVA_UTIL_OPTIONAL));
  }

  public static Generator<JavaPojoMember, PojoSettings> tristateGetterMethod(
      JavaModifier... javaModifiers) {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(JavaModifiers.of(javaModifiers))
        .noGenericTypes()
        .returnType(f -> String.format("Tristate<%s>", f.getJavaType().getParameterizedClassName()))
        .methodName(getterName())
        .noArguments()
        .doesNotThrow()
        .content(
            f ->
                String.format(
                    "return Tristate.ofNullableAndNullFlag(%s, %s);",
                    f.getName(), f.getIsNullFlagName()))
        .build()
        .append(w -> w.ref(OpenApiUtilRefs.TRISTATE));
  }

  public static BiFunction<JavaPojoMember, PojoSettings, String> getterName() {
    return (member, settings) -> member.getGetterNameWithSuffix(settings).asString();
  }

  public static Generator<JavaPojoMember, PojoSettings> rawGetterMethod(GetterType getterType) {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(SettingsFunctions::validationMethodModifiers)
        .noGenericTypes()
        .returnType(returnTypeForRawGetter(getterType))
        .methodName((f, s) -> f.getValidationGetterName(s).asString())
        .noArguments()
        .doesNotThrow()
        .content(f -> String.format("return %s;", f.getName()))
        .build();
  }

  private static Generator<JavaPojoMember, PojoSettings> returnTypeForRawGetter(
      GetterType getterType) {
    final Generator<JavaPojoMember, PojoSettings> noValidationReturnType =
        Generator.<JavaPojoMember, PojoSettings>emptyGen()
            .append((m, s, w) -> w.println("%s", m.getJavaType().getParameterizedClassName()))
            .filter(getterType.<JavaPojoMember>validationFilter().negate());
    final Generator<JavaPojoMember, PojoSettings> validationReturnType =
        deepAnnotatedParameterizedClassName()
            .contraMap(ValidationAnnotationGenerator.PropertyType::fromMember)
            .filter(getterType.validationFilter());
    return validationReturnType.append(noValidationReturnType);
  }

  public static Generator<JavaPojoMember, PojoSettings>
      notNullableValidationMethodWithAnnotation() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
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

  public static Generator<JavaPojoMember, PojoSettings> requiredValidationMethodWithAnnotation() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(deprecatedJavaDocAndAnnotationForValidationMethod())
        .append(assertTrue(f -> String.format("%s is required but it is not present", f.getName())))
        .append(jsonIgnore())
        .append(requiredValidationMethod())
        .filter(Filters.isValidationEnabled());
  }

  private static Generator<JavaPojoMember, PojoSettings> requiredValidationMethod() {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(SettingsFunctions::validationMethodModifiers)
        .noGenericTypes()
        .returnType("boolean")
        .methodName(member -> member.getIsPresentFlagName().asString())
        .noArguments()
        .doesNotThrow()
        .content(member -> String.format("return %s;", member.getIsPresentFlagName()))
        .build();
  }

  public static Generator<JavaPojoMember, PojoSettings> jacksonSerialisationMethod() {
    final Generator<JavaPojoMember, PojoSettings> method =
        JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
            .modifiers(PRIVATE)
            .noGenericTypes()
            .returnType("Object")
            .methodName(f -> String.format("%sJackson", f.getGetterName()))
            .noArguments()
            .doesNotThrow()
            .content(
                f ->
                    String.format(
                        "return %s ? new JacksonNullContainer<>(%s) : %s;",
                        f.getIsNullFlagName(), f.getName(), f.getName()))
            .build()
            .append(RefsGenerator.fieldRefs())
            .append(w -> w.ref(OpenApiUtilRefs.JACKSON_NULL_CONTAINER));

    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(jsonProperty())
        .append(jsonIncludeNonNull())
        .append(method)
        .filter(isJacksonJson());
  }
}
