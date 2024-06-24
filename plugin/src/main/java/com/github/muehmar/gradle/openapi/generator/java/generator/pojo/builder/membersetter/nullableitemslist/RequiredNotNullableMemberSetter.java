package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.nullableitemslist;

import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.MemberSetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.nullableitemslist.UnwrapNullableItemsListMethod;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.java.JavaModifier;
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
  public String memberValue() {
    return String.format("%s(%s)", UnwrapNullableItemsListMethod.METHOD_NAME, member.getName());
  }

  @Override
  public Optional<String> flagAssignment() {
    return Optional.empty();
  }

  @Override
  public PList<String> getRefs() {
    return PList.single(JavaRefs.JAVA_UTIL_OPTIONAL);
  }
}
