package com.github.muehmar.gradle.openapi.generator.java;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.settings.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
class JavaPojoGeneratorTest {

  private Expect expect;

  @Test
  void generatePojo_when_arrayPojoWithUniqueItems_then_correctPojoGenerated() {
    final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator();

    final String content =
        pojoGenerator
            .generatePojo(
                JavaPojos.arrayPojo(Constraints.ofUniqueItems(true)),
                TestPojoSettings.defaultSettings())
            .getContent();

    expect.toMatchSnapshot(content);
  }
}
