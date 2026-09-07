package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;

/**
 * The accessors a composed dto reads on its member dtos. They return the internal representation of
 * a property, i.e. no api conversion and no {@code Optional}/{@code Tristate} wrapping is applied,
 * which is exactly what the parent's builder assigns back into its own field.
 *
 * <p>Their names are context-free, see {@link MethodNames.CrossDto}: the call site in the parent
 * derives the name from a different {@link JavaPojoMember} instance than the declaration here, so a
 * name resolved against the siblings of either side would let the two disagree.
 */
public class CrossDtoAccessor {
  private CrossDtoAccessor() {}

  /** Reads the value of the property in its internal representation. */
  public static Generator<JavaPojoMember, PojoSettings> valueAccessorGenerator() {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers()
        .noGenericTypes()
        .returnType(member -> member.getJavaType().getParameterizedClassName().asString())
        .methodName(member -> MethodNames.CrossDto.valueAccessorName(member).asString())
        .noArguments()
        .doesNotThrow()
        .content(member -> String.format("return %s;", member.getName()))
        .build()
        .append(RefsGenerator.fieldRefs());
  }

  /**
   * Reads whether the property is set, which the internal value alone cannot express: an absent
   * property and one explicitly set to {@code null} both hold {@code null} as their value. The
   * parent skips the assignment entirely when this returns {@code false}, so that its own field
   * keeps the absent state.
   */
  public static Generator<JavaPojoMember, PojoSettings> flagAccessorGenerator() {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers()
        .noGenericTypes()
        .returnType("boolean")
        .methodName(member -> MethodNames.CrossDto.flagAccessorName(member).asString())
        .noArguments()
        .doesNotThrow()
        .content(CrossDtoAccessor::flagAccessorContent)
        .build()
        .append(RefsGenerator.fieldRefs());
  }

  private static String flagAccessorContent(JavaPojoMember member) {
    if (member.isRequiredAndNullable()) {
      return String.format("return %s;", member.getIsPresentFlagName());
    } else if (member.isOptionalAndNotNullable()) {
      return String.format("return %s;", member.getIsNotNullFlagName());
    } else {
      // An optional and nullable property is set if it holds a value or is explicitly null.
      return String.format(
          "return %s != null || %s;", member.getName(), member.getIsNullFlagName());
    }
  }
}
