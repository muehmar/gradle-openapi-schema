package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaModifier;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;

interface MemberSetter {

  static PList<MemberSetter> fromMember(JavaPojoMember member) {
    return PList.of(
        new StandardMemberSetter(member),
        new RequiredNullableMemberSetter(member),
        new OptionalNotNullableMemberSetter(member),
        new OptionalNullableMemberSetter(member));
  }

  boolean shouldBeUsed();

  JavaPojoMember getMember();

  default Generator<MemberSetter, PojoSettings> annotationGenerator() {
    return Generator.emptyGen();
  }

  JavaModifier modifier(PojoSettings settings);

  String argumentType();

  String memberValue();

  Optional<String> flagAssignment();

  Writer addRefs(Writer writer);
}
