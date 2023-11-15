package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.oneof;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.anyof.AnyOfBuilderName;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.name.BuilderName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;

public class OneOfBuilderName implements BuilderName {
  private final SafeBuilderVariant builderVariant;
  private final JavaObjectPojo parentPojo;

  private OneOfBuilderName(SafeBuilderVariant builderVariant, JavaObjectPojo parentPojo) {
    this.builderVariant = builderVariant;
    this.parentPojo = parentPojo;
  }

  public static BuilderName initial(SafeBuilderVariant builderVariant, JavaObjectPojo parentPojo) {
    return parentPojo
        .getOneOfComposition()
        .<BuilderName>map(oneOfComposition -> new OneOfBuilderName(builderVariant, parentPojo))
        .orElse(AnyOfBuilderName.initial(builderVariant, parentPojo));
  }

  public static OneOfBuilderName of(SafeBuilderVariant builderVariant, JavaObjectPojo parentPojo) {
    return new OneOfBuilderName(builderVariant, parentPojo);
  }

  @Override
  public String currentName() {
    return String.format("%sOneOfBuilder", builderVariant.getBuilderNamePrefix());
  }

  public BuilderName getNextPojoBuilderName() {
    return AnyOfBuilderName.initial(builderVariant, parentPojo);
  }
}
