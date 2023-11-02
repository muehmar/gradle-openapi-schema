package com.github.muehmar.gradle.openapi.generator.java.model.composition;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TechnicalPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import java.util.function.UnaryOperator;
import lombok.Value;

@Value
class JavaComposition {
  NonEmptyList<JavaObjectPojo> pojos;

  public PList<JavaPojoMember> getMembers(UnaryOperator<JavaPojoMember> deviateMember) {
    return pojos
        .toPList()
        .flatMap(JavaObjectPojo::getAllMembersForComposition)
        .map(deviateMember)
        .distinct(JavaPojoMember::getTechnicalMemberKey);
  }

  public PList<TechnicalPojoMember> getPojosAsTechnicalMembers() {
    return pojos.toPList().map(TechnicalPojoMember::wrapJavaObjectPojo);
  }
}
