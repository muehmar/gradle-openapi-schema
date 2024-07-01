package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.apitypelist;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.ListAssigmentWriterBuilder.fullListAssigmentWriterBuilder;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.FlagAssignments;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.MemberSetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.SetterModifier;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedApiClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaArrayType;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.java.JavaModifier;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;
import lombok.Value;

@Value
class RequiredNullableMemberSetter implements MemberSetter {
  JavaPojoMember member;
  JavaArrayType javaArrayType;
  Writer listAssigmentWriter;

  public RequiredNullableMemberSetter(JavaPojoMember member, JavaArrayType javaArrayType) {
    this.member = member;
    this.javaArrayType = javaArrayType;
    this.listAssigmentWriter =
        fullListAssigmentWriterBuilder()
            .member(member)
            .unwrapOptionalList()
            .unmapListType(javaArrayType)
            .unwrapListItemNotNecessary()
            .unmapListItemType(javaArrayType)
            .build();
  }

  public static Optional<MemberSetter> fromMember(JavaPojoMember member) {
    return member
        .getJavaType()
        .onArrayType()
        .map(javaArrayType -> new RequiredNullableMemberSetter(member, javaArrayType));
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
  public Writer memberAssigment() {
    return listAssigmentWriter;
  }

  @Override
  public Optional<String> flagAssignment() {
    return Optional.of(FlagAssignments.Wrapped.requiredNullableFlagAssignment(member));
  }

  @Override
  public PList<String> getRefs() {
    return listAssigmentWriter
        .getRefs()
        .concat(Refs.forApiType(javaArrayType))
        .cons(JavaRefs.JAVA_UTIL_OPTIONAL);
  }
}
