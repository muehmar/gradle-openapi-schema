package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.nullableitemslist;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.ListAssigmentWriterBuilder.fullListAssigmentWriterBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_FUNCTION;
import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_OPTIONAL;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.MemberSetter;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.java.JavaModifier;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;
import lombok.Value;

@Value
class RequiredNotNullableMemberSetter implements MemberSetter {
  JavaPojoMember member;

  @Override
  public boolean shouldBeUsed(PojoSettings settings) {
    return NullableItemsListConditions.groupCondition().test(member)
        && member.isRequiredAndNotNullable();
  }

  @Override
  public JavaModifier modifier(PojoSettings settings) {
    return settings.isEnableStagedBuilder() ? PRIVATE : PUBLIC;
  }

  @Override
  public String methodSuffix() {
    return "_";
  }

  @Override
  public String argumentType() {
    return member.getJavaType().getParameterizedClassName().asStringWrappingNullableValueType();
  }

  @Override
  public Writer memberAssigment() {
    return fullListAssigmentWriterBuilder()
        .member(member)
        .fieldAssigment()
        .unwrapListNotNecessary()
        .unmapListTypeNotNecessary()
        .unwrapOptionalListItem()
        .unmapListItemTypeNotNecessary()
        .build();
  }

  @Override
  public Optional<String> flagAssignment() {
    return Optional.empty();
  }

  @Override
  public PList<String> getRefs() {
    return PList.of(JAVA_UTIL_OPTIONAL, JAVA_UTIL_FUNCTION);
  }
}
