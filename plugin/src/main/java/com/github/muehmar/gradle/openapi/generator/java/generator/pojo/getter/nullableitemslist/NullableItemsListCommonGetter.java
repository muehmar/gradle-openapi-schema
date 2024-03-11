package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.nullableitemslist;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.CommonGetter.getterName;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;
import static io.github.muehmar.codegenerator.java.MethodGen.Argument.argument;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;

class NullableItemsListCommonGetter {
  private NullableItemsListCommonGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> wrapNullableInOptionalGetterMethod() {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(
            f ->
                String.format(
                    "Optional<%s>",
                    f.getJavaType()
                        .getParameterizedClassName()
                        .asStringWrappingNullableValueType()))
        .methodName(getterName())
        .noArguments()
        .doesNotThrow()
        .content(
            f ->
                String.format(
                    "return Optional.ofNullable(%s(%s));",
                    WrapNullableItemsListMethod.METHOD_NAME, f.getName()))
        .build()
        .append(w -> w.ref(JavaRefs.JAVA_UTIL_OPTIONAL));
  }

  public static Generator<JavaPojoMember, PojoSettings> wrapNullableInOptionalGetterOrMethod() {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(
            f -> f.getJavaType().getParameterizedClassName().asStringWrappingNullableValueType())
        .methodName(f -> String.format("%sOr", f.getGetterName()))
        .singleArgument(
            f ->
                argument(
                    f.getJavaType().getParameterizedClassName().asStringWrappingNullableValueType(),
                    "defaultValue"))
        .doesNotThrow()
        .content(
            f ->
                String.format(
                    "return this.%s == null ? defaultValue : %s(this.%s);",
                    f.getName(), WrapNullableItemsListMethod.METHOD_NAME, f.getName()))
        .build()
        .append(w -> w.ref(JavaRefs.JAVA_UTIL_OPTIONAL));
  }
}
