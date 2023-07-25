package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.oneof;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.anyof.AnyOfBuilderName;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.name.BuilderName;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;

public class OneOfBuilderName implements BuilderName {
  private final JavaObjectPojo parentPojo;
  private final JavaOneOfComposition oneOfComposition;
  private final JavaObjectPojo oneOfPojo;
  private final int idx;

  private OneOfBuilderName(
      JavaObjectPojo parentPojo,
      JavaOneOfComposition oneOfComposition,
      JavaObjectPojo oneOfPojo,
      int idx) {
    this.parentPojo = parentPojo;
    this.oneOfComposition = oneOfComposition;
    this.oneOfPojo = oneOfPojo;
    this.idx = idx;
  }

  public static BuilderName initial(JavaObjectPojo parentPojo) {
    return parentPojo
        .getOneOfComposition()
        .<BuilderName>flatMap(
            oneOfComposition ->
                oneOfComposition
                    .getPojos()
                    .head()
                    .getAllMembers()
                    .headOption()
                    .map(
                        member ->
                            new OneOfBuilderName(
                                parentPojo,
                                oneOfComposition,
                                oneOfComposition.getPojos().head(),
                                0)))
        .orElse(AnyOfBuilderName.initial(parentPojo));
  }

  public static OneOfBuilderName of(
      JavaObjectPojo parentPojo,
      JavaOneOfComposition oneOfComposition,
      JavaObjectPojo allOfPojo,
      int idx) {
    return new OneOfBuilderName(parentPojo, oneOfComposition, allOfPojo, idx);
  }

  @Override
  public String currentName() {
    if (idx == 0) {
      return String.format("OneOfBuilder%d", idx);
    } else {
      return String.format("OneOfBuilder%s%d", oneOfPojo.getSchemaName(), idx);
    }
  }

  public BuilderName getNextPojoBuilderName() {
    return AnyOfBuilderName.initial(parentPojo);
  }
}
