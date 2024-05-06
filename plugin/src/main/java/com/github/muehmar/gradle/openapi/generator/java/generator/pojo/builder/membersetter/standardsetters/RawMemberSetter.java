package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.standardsetters;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.FlagAssignments;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.MemberSetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.SetterModifier;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.SetterModifier.SetterJavaType;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.apitypelist.ApiTypeListConditions;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.java.JavaModifier;
import java.util.Optional;
import lombok.Value;

/**
 * Setter which uses the actual type directly without any wrapping. Used for all {@link Necessity}
 * and {@link Nullability} combinations.
 */
@Value
class RawMemberSetter implements MemberSetter {
  JavaPojoMember member;

  public RawMemberSetter(JavaPojoMember member) {
    this.member = member;
  }

  @Override
  public boolean shouldBeUsed(PojoSettings settings) {
    return member.getJavaType().hasNoApiType()
        && not(ApiTypeListConditions.groupCondition().test(member));
  }

  @Override
  public JavaModifier modifier(PojoSettings settings) {
    return SetterModifier.forMember(member, settings, SetterJavaType.DEFAULT);
  }

  @Override
  public String argumentType() {
    return member.getJavaType().getParameterizedClassName().asString();
  }

  @Override
  public String memberValue() {
    return member.getName().asString();
  }

  @Override
  public Optional<String> flagAssignment() {
    return FlagAssignments.forStandardMemberSetter(member);
  }
}
