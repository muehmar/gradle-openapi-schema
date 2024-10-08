package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaModifier;
import java.util.Optional;
import lombok.Value;

/** The standard setter is used for every property as JSON setter. */
@Value
class JacksonMemberSetter implements MemberSetter {
  JavaPojoMember member;

  public JacksonMemberSetter(JavaPojoMember member) {
    this.member = member;
  }

  @Override
  public boolean shouldBeUsed(PojoSettings settings) {
    return settings.isJacksonJson();
  }

  @Override
  public Generator<MemberSetter, PojoSettings> annotationGenerator() {
    return JacksonAnnotationGenerator.jsonProperty().contraMap(MemberSetter::getMember);
  }

  @Override
  public String methodSuffix() {
    return "Json";
  }

  @Override
  public JavaModifier modifier(PojoSettings settings) {
    return JavaModifier.PRIVATE;
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
