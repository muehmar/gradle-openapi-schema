package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaTypeGenerators.deepAnnotatedParameterizedClassName;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;
import static io.github.muehmar.codegenerator.java.MethodGen.Argument.argument;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
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

class CommonGetter {
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
        .content(
            f ->
                String.format(
                    "return Tristate.ofNullableAndNullFlag(%s, %s);",
                    f.getName(), f.getIsNullFlagName()))
        .build()
        .append(w -> w.ref(OpenApiUtilRefs.TRISTATE));
  }

  public static BiFunction<JavaPojoMember, PojoSettings, String> getterName() {
    return (field, settings) -> field.getGetterNameWithSuffix(settings).asString();
  }

  public static Generator<JavaPojoMember, PojoSettings> rawGetterMethod() {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(SettingsFunctions::validationMethodModifiers)
        .noGenericTypes()
        .returnType(
            deepAnnotatedParameterizedClassName()
                .contraMap(ValidationAnnotationGenerator.PropertyType::fromMember))
        .methodName((f, s) -> f.getValidationGetterName(s).asString())
        .noArguments()
        .content(f -> String.format("return %s;", f.getName()))
        .build();
  }
}
