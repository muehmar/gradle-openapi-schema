package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.apitypelist;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.apitypelist.Writers.itemMappingWriter;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.FlagAssignments;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.MemberSetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.SetterModifier;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.maplistitem.MapListItemMethod;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedApiClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaArrayType;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.java.JavaModifier;
import java.util.Optional;
import lombok.Value;

@Value
public class ApiTypeListRequiredNullableMemberSetter implements MemberSetter {
  JavaPojoMember member;
  JavaArrayType javaArrayType;

  public static Optional<MemberSetter> fromMember(JavaPojoMember member) {
    return member
        .getJavaType()
        .onArrayType()
        .map(javaArrayType -> new ApiTypeListRequiredNullableMemberSetter(member, javaArrayType));
  }

  @Override
  public boolean shouldBeUsed(PojoSettings settings) {
    return ApiTypeListConditions.groupCondition().test(member) && member.isRequiredAndNullable();
  }

  @Override
  public JavaModifier modifier(PojoSettings settings) {
    return SetterModifier.forMember(member, settings, SetterModifier.SetterJavaType.API);
  }

  @Override
  public String argumentType() {
    final String parameterizedType =
        ParameterizedApiClassName.fromJavaType(javaArrayType)
            .map(ParameterizedApiClassName::asString)
            .orElseGet(() -> javaArrayType.getParameterizedClassName().asString());
    return String.format("Optional<%s>", parameterizedType);
  }

  @Override
  public String memberValue() {
    return String.format(
        "%s(%s, %s)",
        MapListItemMethod.METHOD_NAME,
        Writers.optionalListArgumentConversionWriter(member, javaArrayType).asString(),
        itemMappingWriter(member, javaArrayType).asString());
  }

  @Override
  public Optional<String> flagAssignment() {
    return Optional.of(FlagAssignments.Wrapped.requiredNullableFlagAssignment(member));
  }

  @Override
  public PList<String> getRefs() {
    return Writers.optionalListArgumentConversionWriter(member, javaArrayType)
        .getRefs()
        .concat(itemMappingWriter(member, javaArrayType).getRefs())
        .concat(Refs.forApiType(javaArrayType))
        .cons(JavaRefs.JAVA_UTIL_OPTIONAL);
  }
}
