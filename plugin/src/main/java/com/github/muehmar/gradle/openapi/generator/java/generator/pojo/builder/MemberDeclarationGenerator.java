package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties.additionalPropertiesName;
import static io.github.muehmar.codegenerator.Generator.constant;

import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import io.github.muehmar.codegenerator.Generator;

class MemberDeclarationGenerator {
  private MemberDeclarationGenerator() {}

  public static <B> Generator<JavaObjectPojo, B> memberDeclarationGenerator() {
    return Generator.<JavaObjectPojo, B>emptyGen()
        .appendList(memberDeclaration(), JavaObjectPojo::getAllMembers)
        .append(additionalPropertiesDeclaration(), JavaObjectPojo::getAdditionalProperties);
  }

  private static <B> Generator<JavaPojoMember, B> memberDeclaration() {
    return MemberDeclarationGenerator.<B>normalMemberDeclaration()
        .append(memberIsPresentFlagDeclaration())
        .append(memberIsNullFlagDeclaration());
  }

  private static <B> Generator<JavaPojoMember, B> normalMemberDeclaration() {
    return ((member, settings, writer) ->
        writer.println(
            "private %s %s;", member.getJavaType().getFullClassName(), member.getName()));
  }

  private static <B> Generator<JavaPojoMember, B> memberIsPresentFlagDeclaration() {
    final Generator<JavaPojoMember, B> generator =
        (member, settings, writer) ->
            writer.println("private boolean %s = false;", member.getIsPresentFlagName());
    return generator.filter(JavaPojoMember::isRequiredAndNullable);
  }

  private static <B> Generator<JavaPojoMember, B> memberIsNullFlagDeclaration() {
    final Generator<JavaPojoMember, B> generator =
        (member, settings, writer) ->
            writer.println("private boolean %s = false;", member.getIsNullFlagName());
    return generator.filter(JavaPojoMember::isOptionalAndNullable);
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
