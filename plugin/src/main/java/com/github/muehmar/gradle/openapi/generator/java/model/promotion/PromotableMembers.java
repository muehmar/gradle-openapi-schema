package com.github.muehmar.gradle.openapi.generator.java.model.promotion;

import static com.github.muehmar.gradle.openapi.generator.java.model.promotion.PromotableMembersBuilder.fullPromotableMembersBuilder;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.util.Optional;
import lombok.Value;

/**
 * A collection of members which get promoted. A member gets promoted if the definition of the
 * property itself and the definition that a property with the same name is required are in
 * different schemas but are composed by allOf.
 */
@Value
@PojoBuilder
public class PromotableMembers {
  PermanentProperties permanentProperties;
  PList<JavaPojoMember> members;

  public static PromotableMembers fromPojo(JavaObjectPojo pojo) {
    final PermanentProperties permanentProperties = PermanentProperties.extractDeep(pojo);
    return fullPromotableMembersBuilder()
        .permanentProperties(permanentProperties)
        .members(permanentProperties.determinePromotableMembers())
        .build();
  }

  public Optional<JavaPojoMember> findByName(JavaName name) {
    return members.find(member -> member.getName().equals(name));
  }

  public boolean isPromotable(JavaName name) {
    return findByName(name).isPresent();
  }

  public PromotableMembers addSubPojo(JavaObjectPojo nextSubPojo) {
    final PermanentProperties newPermanentProperties =
        PermanentProperties.extractDeep(nextSubPojo).concat(permanentProperties);
    final PList<JavaPojoMember> newPromotableMembers =
        newPermanentProperties.determinePromotableMembers();

    final PList<JavaPojoMember> mergedPromotableMembers =
        newPromotableMembers.foldLeft(
            members,
            (currentPromotableMembers, newMember) -> {
              final boolean hasMemberWithSameKey =
                  currentPromotableMembers.exists(
                      member -> member.getMemberKey().equals(newMember.getMemberKey()));
              if (hasMemberWithSameKey) {
                return currentPromotableMembers.map(
                    member -> member.mergeToMostRestrictive(newMember).orElse(member));
              } else {
                return currentPromotableMembers.add(newMember);
              }
            });
    return new PromotableMembers(newPermanentProperties, mergedPromotableMembers);
  }
}
