package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.apitypelist.nullableitemslist;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.apitypelist.Writers.itemMappingWriter;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.apitypelist.Writers.noNullCheckListArgumentConversionWriter;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.FlagAssignments;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.MemberSetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.SetterModifier;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.apitypelist.ApiTypeListConditions;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.apitypelist.Refs;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.maplistitem.MapListItemMethod;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.nullableitemslist.UnwrapNullableItemsListMethod;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedApiClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaArrayType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.java.JavaModifier;
import java.util.Optional;
import lombok.Value;

@Value
class RequiredNullableStandardOverloadMemberSetter implements MemberSetter {
  JavaPojoMember member;
  JavaArrayType javaArrayType;

  public static Optional<MemberSetter> fromMember(JavaPojoMember member) {
    return member
        .getJavaType()
        .onArrayType()
        .map(
            javaArrayType ->
                new RequiredNullableStandardOverloadMemberSetter(member, javaArrayType));
  }

  @Override
  public boolean shouldBeUsed(PojoSettings settings) {
    return ApiTypeListConditions.groupCondition().test(member)
        && member.isRequiredAndNullable()
        && member.getJavaType().isNullableItemsArrayType();
  }

  @Override
  public String methodSuffix() {
    return "_";
  }

  @Override
  public JavaModifier modifier(PojoSettings settings) {
    return SetterModifier.forMember(member, settings, SetterModifier.SetterJavaType.API);
  }

  @Override
  public String argumentType() {
    return ParameterizedApiClassName.fromJavaType(javaArrayType)
        .map(ParameterizedApiClassName::asStringWrappingNullableValueType)
        .orElseGet(
            () -> javaArrayType.getParameterizedClassName().asStringWrappingNullableValueType());
  }

  @Override
  public String memberValue() {
    return String.format(
        "%s(%s(%s), %s)",
        MapListItemMethod.METHOD_NAME,
        UnwrapNullableItemsListMethod.METHOD_NAME,
        noNullCheckListArgumentConversionWriter(member, javaArrayType).asString(),
        itemMappingWriter(member, javaArrayType).asString());
  }

  @Override
  public Optional<String> flagAssignment() {
    return Optional.of(FlagAssignments.Raw.requiredNullableFlagAssignment(member));
  }

  @Override
  public PList<String> getRefs() {
    return noNullCheckListArgumentConversionWriter(member, javaArrayType)
        .getRefs()
        .concat(itemMappingWriter(member, javaArrayType).getRefs())
        .concat(Refs.forApiType(javaArrayType));
  }
}
