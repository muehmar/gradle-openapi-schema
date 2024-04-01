package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.java.JavaModifier;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;
import lombok.Value;

@Value
class OptionalNotNullableMemberSetter implements MemberSetter {
  JavaPojoMember member;

  @Override
  public boolean shouldBeUsed() {
    return member.isOptionalAndNotNullable();
  }

  @Override
  public JavaModifier modifier(PojoSettings settings) {
    return JavaModifier.PUBLIC;
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
  public Writer addRefs(Writer writer) {
    return writer.ref(JavaRefs.JAVA_UTIL_OPTIONAL);
  }
}
