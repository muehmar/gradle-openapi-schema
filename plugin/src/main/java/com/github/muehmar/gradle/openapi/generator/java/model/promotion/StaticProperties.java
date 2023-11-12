package com.github.muehmar.gradle.openapi.generator.java.model.promotion;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAllOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaRequiredAdditionalProperty;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import lombok.Value;

@Value
class StaticProperties {
  PList<JavaPojoMember> members;
  PList<JavaRequiredAdditionalProperty> requiredAdditionalProperties;

  static StaticProperties extractDeep(JavaObjectPojo pojo) {
    final PList<JavaPojoMember> staticMembers = pojo.getMembers();
    final PList<JavaRequiredAdditionalProperty> staticAddProps =
        pojo.getRequiredAdditionalProperties();
    final StaticProperties initial = new StaticProperties(staticMembers, staticAddProps);
    return pojo.getAllOfComposition()
        .map(JavaAllOfComposition::getPojos)
        .map(NonEmptyList::toPList)
        .map(pojos -> pojos.map(StaticProperties::extractDeep))
        .orElse(PList.empty())
        .foldLeft(initial, StaticProperties::concat);
  }

  private StaticProperties concat(StaticProperties other) {
    return new StaticProperties(
        this.members.concat(other.members),
        this.requiredAdditionalProperties.concat(other.requiredAdditionalProperties));
  }

  PList<JavaPojoMember> getStaticallyPromotableMembers() {
    return requiredAdditionalProperties
        .filter(JavaRequiredAdditionalProperty::isAnyType)
        .flatMapOptional(props -> members.find(member -> member.getName().equals(props.getName())))
        .map(m -> m.withNecessity(Necessity.REQUIRED));
  }
}
