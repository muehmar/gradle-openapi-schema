package com.github.muehmar.gradle.openapi.generator.java.generator.shared.builder;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;

import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
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
            "private %s %s;",
            member.getJavaType().getFullClassName(), member.getNameAsIdentifier()));
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
            (props, settings, writer) ->
                writer.println(
                    "private Map<String, %s> %s = new HashMap<>();",
                    props.getType().getFullClassName(), JavaAdditionalProperties.getPropertyName()))
        .append(RefsGenerator.javaTypeRefs(), JavaAdditionalProperties::getType)
        .append(ref(JavaRefs.JAVA_UTIL_MAP))
        .append(ref(JavaRefs.JAVA_UTIL_HASH_MAP));
  }
}
