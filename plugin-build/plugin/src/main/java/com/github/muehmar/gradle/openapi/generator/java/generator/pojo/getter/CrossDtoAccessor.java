package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberAndNameScope;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;

/**
 * The accessors a composed dto reads on its member dtos, returning the internal representation of a
 * property. Their names are context-free, as the call site in the parent derives the name from a
 * different {@link JavaPojoMember} instance than the declaration here.
 */
public class CrossDtoAccessor {
  private CrossDtoAccessor() {}

  /** Reads the value of the property in its internal representation. */
  public static Generator<MemberAndNameScope, PojoSettings> valueAccessorGenerator() {
    return JavaGenerators.<MemberAndNameScope, PojoSettings>methodGen()
        .modifiers()
        .noGenericTypes()
        .returnType(mas -> mas.getMember().getJavaType().getParameterizedClassName().asString())
        .methodName(mas -> MethodNames.CrossDto.valueAccessorName(mas.getMember()).asString())
        .noArguments()
        .doesNotThrow()
        .content(mas -> String.format("return %s;", mas.getMember().getName()))
        .build()
        .append(RefsGenerator.fieldRefs(), MemberAndNameScope::getMember);
  }

  /**
   * Whether the property is set, which the internal value alone cannot express: absent and
   * explicitly {@code null} both hold {@code null}. The parent skips the assignment if unset.
   */
  public static Generator<MemberAndNameScope, PojoSettings> flagAccessorGenerator() {
    return JavaGenerators.<MemberAndNameScope, PojoSettings>methodGen()
        .modifiers()
        .noGenericTypes()
        .returnType("boolean")
        .methodName(mas -> MethodNames.CrossDto.flagAccessorName(mas.getMember()).asString())
        .noArguments()
        .doesNotThrow()
        .content(CrossDtoAccessor::flagAccessorContent)
        .build()
        .append(RefsGenerator.fieldRefs(), MemberAndNameScope::getMember);
  }

  private static String flagAccessorContent(MemberAndNameScope mas) {
    final JavaPojoMember member = mas.getMember();
    if (member.isRequiredAndNullable()) {
      return String.format("return %s;", mas.getIsPresentFlagName());
    } else if (member.isOptionalAndNotNullable()) {
      return String.format("return %s;", mas.getIsNotNullFlagName());
    } else {
      return String.format("return %s != null || %s;", member.getName(), mas.getIsNullFlagName());
    }
  }
}
