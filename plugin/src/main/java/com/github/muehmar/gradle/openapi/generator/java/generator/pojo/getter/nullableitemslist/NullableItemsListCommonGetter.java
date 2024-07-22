package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.nullableitemslist;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.CommonGetter.getterName;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.listmapping.MemberMapWriterBuilder.fullMemberMapWriterBuilder;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;
import static io.github.muehmar.codegenerator.java.MethodGen.Argument.argument;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.writer.Writer;

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
        .content(wrapNullableInOptionalGetterMethodContent())
        .build()
        .append(w -> w.ref(JavaRefs.JAVA_UTIL_OPTIONAL));
  }

  public static Generator<JavaPojoMember, PojoSettings>
      wrapNullableInOptionalGetterMethodContent() {
    return (member, settings, writer) ->
        fullMemberMapWriterBuilder()
            .member(member)
            .prefix("return ")
            .mapListItemTypeNotNecessary()
            .wrapOptionalListItem()
            .mapListTypeNotNecessary()
            .wrapOptionalList()
            .trailingSemicolon()
            .build();
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
        .content(wrapNullableInOptionalGetterOrMethodContent())
        .build()
        .append(w -> w.ref(JavaRefs.JAVA_UTIL_OPTIONAL));
  }

  private static Generator<JavaPojoMember, PojoSettings>
      wrapNullableInOptionalGetterOrMethodContent() {
    return (member, settings, writer) -> {
      final Writer memberMapWriter =
          fullMemberMapWriterBuilder()
              .member(member)
              .prefix(": ")
              .mapListItemTypeNotNecessary()
              .wrapOptionalListItem()
              .mapListTypeNotNecessary()
              .wrapListNotNecessary()
              .trailingSemicolon()
              .build();
      return writer
          .println("return this.%s == null", member.getName())
          .tab(2)
          .println("? defaultValue")
          .append(2, memberMapWriter);
    };
  }
}
