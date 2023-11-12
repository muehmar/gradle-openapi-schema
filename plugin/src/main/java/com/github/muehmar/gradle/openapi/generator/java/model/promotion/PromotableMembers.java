package com.github.muehmar.gradle.openapi.generator.java.model.promotion;

import static com.github.muehmar.gradle.openapi.generator.java.model.promotion.PromotableMembersBuilder.fullPromotableMembersBuilder;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.util.Optional;
import lombok.Value;

/**
 * A collection of members which get promoted. A member gets promoted if it is defined required
 * without defining it explicitly as property but a property with the same name is defined in
 * another schema or one of its allOf sub-schemas.
 */
@Value
@PojoBuilder
public class PromotableMembers {
  /**
   * A member which gets promoted statically, i.e. the promotion is done via allOf composition and
   * applies independent of the send data.
   */
  PList<JavaPojoMember> staticallyPromotableMembers;

  /**
   * A member which gets promoted only dynamically, i.e. the promotion is done via anyOf or oneOf
   * and applies only dynamically depending on the send data. Therefore, it's only safe to promote
   * the corresponding members in the oneOf and anyOf subpojos.
   */
  PList<JavaPojoMember> dynamicallyPromotableMembers;

  public Optional<JavaPojoMember> findStaticByName(JavaName name) {
    return staticallyPromotableMembers.find(member -> member.getName().equals(name));
  }

  public boolean hasStaticPromotable(JavaName name) {
    return findStaticByName(name).isPresent();
  }

  public PromotableMembers integrateDynamicallyPromotableMembers() {
    return fullPromotableMembersBuilder()
        .staticallyPromotableMembers(
            staticallyPromotableMembers.concat(dynamicallyPromotableMembers))
        .dynamicallyPromotableMembers(PList.empty())
        .build();
  }
}
