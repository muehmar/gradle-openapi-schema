package com.github.muehmar.gradle.openapi.generator.java.model.composition;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TechnicalPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaPojoName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.promotion.PojoPromotionResult;
import com.github.muehmar.gradle.openapi.generator.java.model.promotion.PromotableMembers;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import lombok.Value;

@Value
class JavaComposition {
  NonEmptyList<JavaObjectPojo> pojos;

  public JavaPojoMembers getMembers(UnaryOperator<JavaPojoMember> deviateMember) {
    return pojos
        .toPList()
        .map(JavaObjectPojo::getAllMembersForComposition)
        .map(JavaPojoMembers::leastRestrictive)
        .map(members -> members.map(deviateMember))
        .reduce(JavaPojoMembers::add)
        .orElse(JavaPojoMembers.emptyLeastRestrictive());
  }

  public PList<TechnicalPojoMember> getPojosAsTechnicalMembers() {
    return pojos.toPList().map(TechnicalPojoMember::wrapJavaObjectPojo);
  }

  public CompositionPromotionResult promote(
      JavaPojoName rootName, Function<JavaObjectPojo, PromotableMembers> promotableMembers) {
    final NonEmptyList<PojoPromotionResult> promotedPojoResults =
        pojos.map(pojo -> pojo.promote(rootName, promotableMembers.apply(pojo)));
    final JavaComposition promotedComposition =
        new JavaComposition(promotedPojoResults.map(PojoPromotionResult::getPromotedPojo));
    final PList<JavaObjectPojo> newPojos =
        promotedPojoResults.toPList().flatMap(PojoPromotionResult::getNewPojos);
    return new CompositionPromotionResult(promotedComposition, newPojos);
  }

  @Value
  public static class CompositionPromotionResult {
    JavaComposition composition;
    PList<JavaObjectPojo> newPojos;
  }
}
