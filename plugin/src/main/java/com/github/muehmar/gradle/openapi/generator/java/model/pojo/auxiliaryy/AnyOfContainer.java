package com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliaryy;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.ConstructorContentBuilder.fullConstructorContentBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.EqualsContentBuilder.fullEqualsContentBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.HashCodeContentBuilder.fullHashCodeContentBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.ToStringContentBuilder.fullToStringContentBuilder;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberContentBuilder;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.EqualsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.HashCodeGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.PojoConstructorGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.ToStringGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoName;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfComposition;
import java.util.Optional;
import lombok.Value;

@Value
public class AnyOfContainer {
  private static final String NAME_SUFFIX = "AnyOfContainer";
  JavaPojoName pojoName;
  JavaAnyOfComposition composition;

  public JavaIdentifier getContainerName() {
    return pojoName.appendToName(NAME_SUFFIX).asIdentifier();
  }

  public MemberGenerator.MemberContent memberContent() {
    return MemberContentBuilder.fullMemberContentBuilder()
        .isArrayPojo(false)
        .members(composition.getPojosAsTechnicalMembers())
        .additionalProperties(Optional.empty())
        .build();
  }

  public PojoConstructorGenerator.ConstructorContent constructorContent() {
    return fullConstructorContentBuilder()
        .isArray(false)
        .className(getContainerName())
        .members(composition.getPojosAsTechnicalMembers())
        .modifier(PRIVATE)
        .additionalProperties(Optional.empty())
        .build();
  }

  public EqualsGenerator.EqualsContent getEqualsContent() {
    return fullEqualsContentBuilder()
        .className(getContainerName())
        .technicalPojoMembers(composition.getPojosAsTechnicalMembers())
        .build();
  }

  public HashCodeGenerator.HashCodeContent getHashCodeContent() {
    return fullHashCodeContentBuilder()
        .technicalPojoMembers(composition.getPojosAsTechnicalMembers())
        .build();
  }

  public ToStringGenerator.ToStringContent getToStringContent() {
    return fullToStringContentBuilder()
        .className(getContainerName())
        .technicalPojoMembers(composition.getPojosAsTechnicalMembers())
        .build();
  }
}
