package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.standardsetters;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.MemberSetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.SetterModifier;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.SetterModifier.SetterJavaType;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.java.JavaModifier;
import java.util.Optional;
import lombok.Value;

@Value
class OptionalNotNullableMemberSetter implements MemberSetter {
  JavaPojoMember member;

  @Override
  public boolean shouldBeUsed(PojoSettings settings) {
    return member.isOptionalAndNotNullable()
        && StandardSetterConditions.groupCondition().test(member);
  }

  @Override
  public JavaModifier modifier(PojoSettings settings) {
    return SetterModifier.forMember(member, settings, SetterJavaType.DEFAULT);
  }

  @Override
  public String argumentType() {
    return String.format("Optional<%s>", member.getJavaType().getParameterizedClassName());
  }

  @Override
  public String memberValue() {
    return String.format("%s.orElse(null)", member.getName());
  }

  @Override
  public Optional<String> flagAssignment() {
    return Optional.of(String.format("this.%s = true;", member.getIsNotNullFlagName()));
  }

  @Override
  public PList<String> getRefs() {
    return PList.single(JavaRefs.JAVA_UTIL_OPTIONAL);
  }
}
