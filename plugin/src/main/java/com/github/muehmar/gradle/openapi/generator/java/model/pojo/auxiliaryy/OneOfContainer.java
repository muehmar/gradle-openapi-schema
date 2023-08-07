package com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliaryy;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.ConstructorContentBuilder.fullConstructorContentBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.EqualsContentBuilder.fullEqualsContentBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.HashCodeContentBuilder.fullHashCodeContentBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.ToStringContentBuilder.fullToStringContentBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties.anyTypeAllowed;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberContentBuilder;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.EqualsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.HashCodeGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.PojoConstructorGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.ToStringGenerator;
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

  public PojoConstructorGenerator.ConstructorContent constructorContent() {
    return fullConstructorContentBuilder()
        .isArray(false)
        .className(pojoName.asIdentifier())
        .members(composition.getMembers())
        .modifier(PRIVATE)
        .additionalProperties(anyTypeAllowed())
        .build();
  }

  public EqualsGenerator.EqualsContent getEqualsContent() {
    return fullEqualsContentBuilder()
        .className(pojoName.asIdentifier())
        .members(composition.getMembers())
        .additionalProperties(anyTypeAllowed())
        .build();
  }

  public HashCodeGenerator.HashCodeContent getHashCodeContent() {
    return fullHashCodeContentBuilder()
        .members(composition.getMembers())
        .additionalProperties(anyTypeAllowed())
        .build();
  }

  public ToStringGenerator.ToStringContent getToStringContent() {
    return fullToStringContentBuilder()
        .className(pojoName.asIdentifier())
        .members(composition.getMembers())
        .additionalProperties(anyTypeAllowed())
        .build();
  }
}
