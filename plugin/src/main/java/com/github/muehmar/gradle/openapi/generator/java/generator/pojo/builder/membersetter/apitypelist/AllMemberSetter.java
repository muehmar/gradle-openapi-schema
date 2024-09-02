package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.apitypelist;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.ListAssigmentWriterBuilder.fullListAssigmentWriterBuilder;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.FlagAssignments;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.MemberSetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.SetterModifier;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.SetterModifier.SetterJavaType;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaArrayType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.java.JavaModifier;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;
import lombok.Value;

@Value
class AllMemberSetter implements MemberSetter {
  JavaPojoMember member;
  JavaArrayType javaArrayType;
  Writer listAssigmentWriter;

  public AllMemberSetter(JavaPojoMember member, JavaArrayType javaArrayType) {
    this.member = member;
    this.javaArrayType = javaArrayType;
    this.listAssigmentWriter =
        fullListAssigmentWriterBuilder()
            .member(member)
            .unwrapListNotNecessary()
            .unmapListType(javaArrayType)
            .unwrapListItemNotNecessary()
            .unmapListItemType(javaArrayType)
            .build();
  }

  public static Optional<MemberSetter> fromMember(JavaPojoMember member) {
    return member
        .getJavaType()
        .onArrayType()
        .map(javaArrayType -> new AllMemberSetter(member, javaArrayType));
  }

  @Override
  public boolean shouldBeUsed(PojoSettings settings) {
    return ApiTypeListConditions.groupCondition().test(member);
  }

  @Override
  public JavaModifier modifier(PojoSettings settings) {
    return SetterModifier.forMember(member, settings, SetterJavaType.API);
  }

  @Override
  public String argumentType() {
    return javaArrayType.getWriteableParameterizedClassName().asString();
  }

  @Override
  public Writer memberAssigment() {
    return listAssigmentWriter;
  }

  @Override
  public Optional<String> flagAssignment() {
    return FlagAssignments.forStandardMemberSetter(member);
  }

  @Override
  public PList<String> getRefs() {
    return listAssigmentWriter.getRefs().concat(Refs.forApiType(javaArrayType));
  }
}
