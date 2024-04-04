package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.nullableitemslist;

import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_OPTIONAL;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.MemberSetter;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.java.JavaModifier;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;

abstract class OptionalNotNullableMemberSetter implements MemberSetter {
  protected final JavaPojoMember member;

  public OptionalNotNullableMemberSetter(JavaPojoMember member) {
    this.member = member;
  }

  @Override
  public JavaPojoMember getMember() {
    return member;
  }

  @Override
  public boolean shouldBeUsed() {
    return member.isOptionalAndNotNullable() && member.getJavaType().isNullableItemsArrayType();
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
  public Optional<String> flagAssignment() {
    return Optional.of(String.format("this.%s = true;", member.getIsNotNullFlagName()));
  }

  @Override
  public Writer addRefs(Writer writer) {
    return writer.ref(JAVA_UTIL_OPTIONAL);
  }
}
