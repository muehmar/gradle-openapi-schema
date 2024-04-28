package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.java.JavaModifier;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;
import lombok.Value;

@Value
class OptionalNullableMemberSetter implements MemberSetter {
  JavaPojoMember member;

  @Override
  public boolean shouldBeUsed() {
    return member.isOptionalAndNullable();
  }

  @Override
  public JavaModifier modifier(PojoSettings settings) {
    return JavaModifier.PUBLIC;
  }

  @Override
  public String argumentType() {
    return String.format("Tristate<%s>", member.getJavaType().getInternalParameterizedClassName());
  }

  @Override
  public String memberValue() {
    return String.format("%s.%s", member.getName(), member.tristateToProperty());
  }

  @Override
  public Optional<String> flagAssignment() {
    return Optional.of(FlagAssignments.wrappedOptionalNullableFlagAssignment(member));
  }

  @Override
  public Writer addRefs(Writer writer) {
    return writer.ref(OpenApiUtilRefs.TRISTATE);
  }
}
