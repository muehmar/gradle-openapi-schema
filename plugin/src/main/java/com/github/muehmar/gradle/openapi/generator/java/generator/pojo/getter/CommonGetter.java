package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaTypeGenerators.deepAnnotatedFullClassName;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.SettingsFunctions;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.java.MethodGen.Argument;
import java.util.function.BiFunction;

class CommonGetter {
  private CommonGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> wrapNullableInOptionalGetterMethod() {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(f -> String.format("Optional<%s>", f.getJavaType().getFullClassName()))
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
        .returnType(f -> f.getJavaType().getFullClassName().asString())
        .methodName(f -> String.format("%sOr", f.getGetterName()))
        .singleArgument(
            f -> new Argument(f.getJavaType().getFullClassName().asString(), "defaultValue"))
        .content(
            f ->
                String.format(
                    "return this.%s == null ? defaultValue : this.%s;", f.getName(), f.getName()))
        .build()
        .append(w -> w.ref(JavaRefs.JAVA_UTIL_OPTIONAL));
  }

  public static BiFunction<JavaPojoMember, PojoSettings, String> getterName() {
    return (field, settings) -> field.getGetterNameWithSuffix(settings).asString();
  }

  public static Generator<JavaPojoMember, PojoSettings> rawGetterMethod() {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(SettingsFunctions::validationMethodModifiers)
        .noGenericTypes()
        .returnType(deepAnnotatedFullClassName().contraMap(JavaPojoMember::getJavaType))
        .methodName((f, s) -> f.getValidationGetterName(s).asString())
        .noArguments()
        .content(f -> String.format("return %s;", f.getName()))
        .build();
  }
}
