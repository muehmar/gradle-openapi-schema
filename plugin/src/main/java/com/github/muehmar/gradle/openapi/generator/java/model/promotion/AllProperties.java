package com.github.muehmar.gradle.openapi.generator.java.model.promotion;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAllOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaRequiredAdditionalProperty;
import java.util.function.Function;
import lombok.Value;

@Value
class AllProperties {
  PList<JavaPojoMember> members;
  PList<JavaRequiredAdditionalProperty> requiredAdditionalProperties;

  static AllProperties extractDeep(JavaObjectPojo pojo) {
    final PList<JavaPojoMember> members = pojo.getMembers();
    final PList<JavaRequiredAdditionalProperty> requiredAdditionalProperties =
        pojo.getRequiredAdditionalProperties();
    final AllProperties initial = new AllProperties(members, requiredAdditionalProperties);
    return PList.of(
            pojo.getOneOfComposition().map(JavaOneOfComposition::getPojos),
            pojo.getAllOfComposition().map(JavaAllOfComposition::getPojos),
            pojo.getAnyOfComposition().map(JavaAnyOfComposition::getPojos))
        .flatMapOptional(Function.identity())
        .flatMap(NonEmptyList::toPList)
        .map(AllProperties::extractDeep)
        .foldLeft(initial, AllProperties::concat);
  }

  AllProperties concat(AllProperties other) {
    return new AllProperties(
        this.members.concat(other.members),
        this.requiredAdditionalProperties.concat(other.requiredAdditionalProperties));
  }
}
