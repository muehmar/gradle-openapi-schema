package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.nullableitemslist;

import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_OPTIONAL;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.MemberSetter;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.java.JavaModifier;
import io.github.muehmar.codegenerator.writer.Writer;

abstract class OptionalNullableMemberSetter implements MemberSetter {
  protected final JavaPojoMember member;

  public OptionalNullableMemberSetter(JavaPojoMember member) {
    this.member = member;
  }

  @Override
  public JavaPojoMember getMember() {
    return member;
  }

  @Override
  public boolean shouldBeUsed() {
    return member.isOptionalAndNullable() && member.getJavaType().isNullableItemsArrayType();
  }

  @Override
  public JavaModifier modifier(PojoSettings settings) {
    return JavaModifier.PUBLIC;
  }

  @Override
  public String methodSuffix() {
    return "_";
  }

  @Override
  public Writer addRefs(Writer writer) {
    return writer.ref(JAVA_UTIL_OPTIONAL);
  }
}
