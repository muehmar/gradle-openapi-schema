package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties.additionalPropertiesName;
import static io.github.muehmar.codegenerator.Generator.constant;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberAndFlagFieldScope;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import io.github.muehmar.codegenerator.Generator;

class MemberDeclarationGenerator {
  private MemberDeclarationGenerator() {}

  public static <B> Generator<JavaObjectPojo, B> memberDeclarationGenerator() {
    return Generator.<JavaObjectPojo, B>emptyGen()
        .appendList(memberDeclaration(), MemberAndFlagFieldScope::fromPojo)
        .append(additionalPropertiesDeclaration(), JavaObjectPojo::getAdditionalProperties);
  }

  private static <B> Generator<MemberAndFlagFieldScope, B> memberDeclaration() {
    return MemberDeclarationGenerator.<B>normalMemberDeclaration()
        .append(memberIsPresentFlagDeclaration())
        .append(memberIsNullFlagDeclaration())
        .append(memberIsNotNullFlagDeclaration());
  }

  private static <B> Generator<MemberAndFlagFieldScope, B> normalMemberDeclaration() {
    return ((mas, settings, writer) ->
        writer.println(
            "private %s %s;",
            mas.getMember().getJavaType().getParameterizedClassName(), mas.getMember().getName()));
  }

  private static <B> Generator<MemberAndFlagFieldScope, B> memberIsPresentFlagDeclaration() {
    final Generator<MemberAndFlagFieldScope, B> generator =
        (mas, settings, writer) ->
            writer.println("private boolean %s = false;", mas.getIsPresentFlagName());
    return generator.filter(mas -> mas.getMember().isRequiredAndNullable());
  }

  private static <B> Generator<MemberAndFlagFieldScope, B> memberIsNullFlagDeclaration() {
    final Generator<MemberAndFlagFieldScope, B> generator =
        (mas, settings, writer) ->
            writer.println("private boolean %s = false;", mas.getIsNullFlagName());
    return generator.filter(mas -> mas.getMember().isOptionalAndNullable());
  }

  private static <B> Generator<MemberAndFlagFieldScope, B> memberIsNotNullFlagDeclaration() {
    final Generator<MemberAndFlagFieldScope, B> generator =
        (mas, settings, writer) ->
            writer.println("private boolean %s = true;", mas.getIsNotNullFlagName());
    return generator.filter(mas -> mas.getMember().isOptionalAndNotNullable());
  }

  private static <B> Generator<JavaAdditionalProperties, B> additionalPropertiesDeclaration() {
    return Generator.<JavaAdditionalProperties, B>emptyGen()
        .append(
            constant(
                "private Map<String, Object> %s = new HashMap<>();", additionalPropertiesName()))
        .append(ref(JavaRefs.JAVA_UTIL_MAP))
        .append(ref(JavaRefs.JAVA_UTIL_HASH_MAP));
  }
}
