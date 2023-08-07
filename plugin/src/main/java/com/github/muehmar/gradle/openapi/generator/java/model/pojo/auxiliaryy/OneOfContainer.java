package com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliaryy;

import static com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties.anyTypeAllowed;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberContentBuilder;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoName;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import lombok.Value;

@Value
public class OneOfContainer {
  private static final String NAME_SUFFIX = "OneOfContainer";
  JavaPojoName pojoName;
  JavaOneOfComposition composition;

  public JavaPojoName getContainerName() {
    return pojoName.appendToName(NAME_SUFFIX);
  }

  public MemberGenerator.MemberContent memberContent() {
    return MemberContentBuilder.fullMemberContentBuilder()
        .isArrayPojo(false)
        .members(composition.getMembers())
        .additionalProperties(anyTypeAllowed())
        .build();
  }
}
