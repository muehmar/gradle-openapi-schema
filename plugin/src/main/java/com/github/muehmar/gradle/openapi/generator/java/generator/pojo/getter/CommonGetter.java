package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.DeprecatedMethodGenerator.deprecatedJavaDocAndAnnotationForValidationMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaTypeGenerators.deepAnnotatedParameterizedClassName;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator.assertTrue;

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
      GetterType getterType, JavaModifier... javaModifiers) {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(JavaModifiers.of(javaModifiers))
        .noGenericTypes()
        .returnType(getterMethodReturnType(getterType))
        .methodName(getterName())
        .noArguments()
        .doesNotThrow()
        .content(f -> String.format("return %s;", f.getName()))
        .build()
        .append(RefsGenerator.fieldRefs());
  }

  private static Generator<JavaPojoMember, PojoSettings> getterMethodReturnType(
      GetterType getterType) {
    final Generator<JavaPojoMember, PojoSettings> deepAnnotatedReturnType =
        deepAnnotatedParameterizedClassName()
            .contraMap(ValidationAnnotationGenerator.PropertyType::fromMember)
            .filter(getterType.validationFilter());

    final Generator<JavaPojoMember, PojoSettings> standardReturnType =
        Generator.<JavaPojoMember, PojoSettings>emptyGen()
            .append((m, s, w) -> w.println("%s", m.getJavaType().getParameterizedClassName()))
            .filter(getterType.<JavaPojoMember>validationFilter().negate());

    return deepAnnotatedReturnType.append(standardReturnType);
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
}
