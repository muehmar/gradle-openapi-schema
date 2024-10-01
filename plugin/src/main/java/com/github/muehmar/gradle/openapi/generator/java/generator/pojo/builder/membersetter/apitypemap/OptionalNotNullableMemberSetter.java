package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.apitypemap;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.map.MapAssignmentWriterBuilder.fullMapAssignmentWriterBuilder;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.FlagAssignments;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.MemberSetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.SetterModifier;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.apitypelist.Refs;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaMapType;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.java.JavaModifier;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;
import lombok.Value;

@Value
class OptionalNotNullableMemberSetter implements MemberSetter {
  JavaPojoMember member;
  JavaMapType javaMapType;
  Writer mapAssigmentWriter;

  public OptionalNotNullableMemberSetter(JavaPojoMember member, JavaMapType javaMapType) {
    this.member = member;
    this.javaMapType = javaMapType;
    this.mapAssigmentWriter =
        fullMapAssignmentWriterBuilder()
            .member(member)
            .fieldAssigment()
            .unwrapOptionalMap()
            .unmapMapType(javaMapType)
            .unwrapMapItemNotNecessary()
            .unmapMapItemType(javaMapType)
            .build();
  }

  public static Optional<MemberSetter> fromMember(JavaPojoMember member) {
    return member
        .getJavaType()
        .onMapType()
        .map(javaMapType -> new OptionalNotNullableMemberSetter(member, javaMapType));
  }

  @Override
  public boolean shouldBeUsed(PojoSettings settings) {
    return ApiTypeMapConditions.groupCondition().test(member) && member.isOptionalAndNotNullable();
  }

  @Override
  public JavaModifier modifier(PojoSettings settings) {
    return SetterModifier.forMember(member, settings, SetterModifier.SetterJavaType.API);
  }

  @Override
  public String argumentType() {
    final String parameterizedType = javaMapType.getWriteableParameterizedClassName().asString();
    return String.format("Optional<%s>", parameterizedType);
  }

  @Override
  public Writer memberAssigment() {
    return mapAssigmentWriter;
  }

  @Override
  public Optional<String> flagAssignment() {
    return Optional.of(FlagAssignments.Wrapped.optionalNotNullableFlagAssignment(member));
  }

  @Override
  public PList<String> getRefs() {
    return mapAssigmentWriter
        .getRefs()
        .concat(Refs.forApiType(javaMapType))
        .cons(JavaRefs.JAVA_UTIL_OPTIONAL);
  }
}
