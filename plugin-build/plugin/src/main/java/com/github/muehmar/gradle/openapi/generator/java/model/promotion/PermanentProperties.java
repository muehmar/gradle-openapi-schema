package com.github.muehmar.gradle.openapi.generator.java.model.promotion;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAllOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaRequiredAdditionalProperty;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import lombok.Value;

/**
 * Properties and required additional properties of a pojo which are permanent, i.e. either defined
 * in the pojo itself or one of the allOf subpojos.
 */
@Value
class PermanentProperties {
  PList<JavaPojoMember> members;
  PList<JavaRequiredAdditionalProperty> requiredAdditionalProperties;

  static PermanentProperties extractDeep(JavaObjectPojo pojo) {
    final PList<JavaPojoMember> staticMembers = pojo.getMembers();
    final PList<JavaRequiredAdditionalProperty> staticAddProps =
        pojo.getRequiredAdditionalProperties();
    final PermanentProperties initial = new PermanentProperties(staticMembers, staticAddProps);
    return pojo.getAllOfComposition()
        .map(JavaAllOfComposition::getPojos)
        .map(NonEmptyList::toPList)
        .map(pojos -> pojos.map(PermanentProperties::extractDeep))
        .orElse(PList.empty())
        .foldLeft(initial, PermanentProperties::concat);
  }

  public PermanentProperties concat(PermanentProperties other) {
    return new PermanentProperties(
        this.members.concat(other.members),
        this.requiredAdditionalProperties.concat(other.requiredAdditionalProperties));
  }

  PList<JavaPojoMember> determinePromotableMembers() {
    return requiredAdditionalProperties
        .filter(JavaRequiredAdditionalProperty::isAnyType)
        .flatMapOptional(props -> members.find(member -> member.getName().equals(props.getName())))
        .map(m -> m.withNecessity(Necessity.REQUIRED));
  }
}
