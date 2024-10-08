package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.nullableitemslist;

import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_FUNCTION;
import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_OPTIONAL;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.MemberSetter;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.java.JavaModifier;

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
  public boolean shouldBeUsed(PojoSettings settings) {
    return NullableItemsListConditions.groupCondition().test(member)
        && member.isOptionalAndNullable();
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
  public PList<String> getRefs() {
    return PList.of(JAVA_UTIL_OPTIONAL, JAVA_UTIL_FUNCTION);
  }
}
