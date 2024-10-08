package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.nullableitemslist;

import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_FUNCTION;
import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_OPTIONAL;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.FlagAssignments;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.MemberSetter;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.java.JavaModifier;
import java.util.Optional;

abstract class RequiredNullableMemberSetter implements MemberSetter {
  protected final JavaPojoMember member;

  public RequiredNullableMemberSetter(JavaPojoMember member) {
    this.member = member;
  }

  @Override
  public boolean shouldBeUsed(PojoSettings settings) {
    return NullableItemsListConditions.groupCondition().test(member)
        && member.isRequiredAndNullable();
  }

  @Override
  public JavaPojoMember getMember() {
    return member;
  }

  @Override
  public JavaModifier modifier(PojoSettings settings) {
    return settings.isEnableStagedBuilder() ? PRIVATE : PUBLIC;
  }

  @Override
  public String methodSuffix() {
    return "_";
  }

  @Override
  public Optional<String> flagAssignment() {
    return Optional.of(FlagAssignments.Raw.requiredNullableFlagAssignment(member));
  }

  @Override
  public PList<String> getRefs() {
    return PList.of(JAVA_UTIL_OPTIONAL, JAVA_UTIL_FUNCTION);
  }
}
