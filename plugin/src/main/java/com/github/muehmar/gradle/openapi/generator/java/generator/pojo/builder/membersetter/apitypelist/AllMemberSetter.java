package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.apitypelist;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.apitypelist.Writers.itemMappingWriter;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.apitypelist.Writers.noNullCheckListArgumentConversionWriter;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.FlagAssignments;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.MemberSetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.SetterModifier;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.SetterModifier.SetterJavaType;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.maplistitem.MapListItemMethod;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedApiClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaArrayType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.java.JavaModifier;
import java.util.Optional;
import lombok.Value;

@Value
class AllMemberSetter implements MemberSetter {
  JavaPojoMember member;
  JavaArrayType javaArrayType;

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
    return ParameterizedApiClassName.fromJavaType(javaArrayType)
        .map(ParameterizedApiClassName::asString)
        .orElseGet(() -> javaArrayType.getParameterizedClassName().asString());
  }

  @Override
  public String memberValue() {
    return String.format(
        "%s(%s, %s)",
        MapListItemMethod.METHOD_NAME,
        noNullCheckListArgumentConversionWriter(member.getName().asString(), javaArrayType)
            .asString(),
        itemMappingWriter(member, javaArrayType).asString());
  }

  @Override
  public Optional<String> flagAssignment() {
    return FlagAssignments.forStandardMemberSetter(member);
  }

  @Override
  public PList<String> getRefs() {
    return noNullCheckListArgumentConversionWriter(member.getName().asString(), javaArrayType)
        .getRefs()
        .concat(itemMappingWriter(member, javaArrayType).getRefs())
        .concat(
            ParameterizedApiClassName.fromJavaType(javaArrayType)
                .map(ParameterizedApiClassName::getAllQualifiedClassNames)
                .orElseGet(PList::empty)
                .map(QualifiedClassName::asString));
  }
}
