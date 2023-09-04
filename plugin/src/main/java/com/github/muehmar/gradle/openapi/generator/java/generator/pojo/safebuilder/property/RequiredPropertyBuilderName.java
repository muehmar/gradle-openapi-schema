package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.name.BuilderName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;

public class RequiredPropertyBuilderName implements BuilderName {
  private final SafeBuilderVariant builderVariant;
  private final JavaObjectPojo parentPojo;
  private final int memberIndex;

  private RequiredPropertyBuilderName(
      SafeBuilderVariant builderVariant, JavaObjectPojo parentPojo, int memberIndex) {
    this.builderVariant = builderVariant;
    this.parentPojo = parentPojo;
    this.memberIndex = memberIndex;
  }

  public static BuilderName initial(SafeBuilderVariant builderVariant, JavaObjectPojo parentPojo) {
    if (builderVariant.equals(SafeBuilderVariant.FULL) && parentPojo.hasNotRequiredMembers()) {
      return OptionalPropertyBuilderName.initial(builderVariant, parentPojo);
    } else {
      return new RequiredPropertyBuilderName(builderVariant, parentPojo, 0);
    }
  }

  public static RequiredPropertyBuilderName from(
      SafeBuilderVariant builderVariant, JavaObjectPojo parentPojo, int memberIndex) {
    return new RequiredPropertyBuilderName(builderVariant, parentPojo, memberIndex);
  }

  public String nextBuilderName() {
    if (parentPojo.getRequiredMemberCount() - 1 == memberIndex
        && builderVariant.equals(SafeBuilderVariant.FULL)) {
      return OptionalPropertyBuilderName.initial(builderVariant, parentPojo).currentName();
    } else {
      return new RequiredPropertyBuilderName(builderVariant, parentPojo, memberIndex + 1)
          .currentName();
    }
  }

  @Override
  public String currentName() {
    return String.format("%sPropertyBuilder%d", builderVariant.getBuilderNamePrefix(), memberIndex);
  }
}
