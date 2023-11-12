package com.github.muehmar.gradle.openapi.generator.java.model.promotion;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaRequiredAdditionalProperty;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import java.util.function.Function;
import lombok.Value;

@Value
class DynamicProperties {
  PList<JavaPojoMember> members;
  PList<JavaRequiredAdditionalProperty> requiredAdditionalProperties;

  static DynamicProperties extractDeep(JavaObjectPojo pojo) {
    final PList<JavaPojoMember> members = pojo.getMembers();
    final PList<JavaRequiredAdditionalProperty> requiredAdditionalProperties =
        pojo.getRequiredAdditionalProperties();
    final AllProperties initial = new AllProperties(members, requiredAdditionalProperties);
    final AllProperties allProperties =
        PList.of(
                pojo.getOneOfComposition().map(JavaOneOfComposition::getPojos),
                pojo.getAnyOfComposition().map(JavaAnyOfComposition::getPojos))
            .flatMapOptional(Function.identity())
            .flatMap(NonEmptyList::toPList)
            .map(AllProperties::extractDeep)
            .foldLeft(initial, AllProperties::concat);
    return new DynamicProperties(
        allProperties.getMembers(), allProperties.getRequiredAdditionalProperties());
  }

  PList<JavaPojoMember> getDynamicallyPromotableMembers(StaticProperties staticProperties) {
    return findPromotableMembers(requiredAdditionalProperties, staticProperties.getMembers())
        .concat(findPromotableMembers(staticProperties.getRequiredAdditionalProperties(), members));
  }

  private PList<JavaPojoMember> findPromotableMembers(
      PList<JavaRequiredAdditionalProperty> requiredAdditionalProperties,
      PList<JavaPojoMember> members) {
    return requiredAdditionalProperties
        .filter(JavaRequiredAdditionalProperty::isAnyType)
        .flatMapOptional(prop -> members.find(m -> m.getName().equals(prop.getName())))
        .map(m -> m.withNecessity(Necessity.REQUIRED));
  }
}
