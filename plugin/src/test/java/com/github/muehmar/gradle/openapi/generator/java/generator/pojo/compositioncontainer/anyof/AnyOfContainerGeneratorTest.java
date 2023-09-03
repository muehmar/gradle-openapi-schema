package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer.anyof;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer.anyof.AnyOfContainerGenerator.anyOfContainerGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.allNecessityAndNullabilityVariants;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo2;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import com.github.muehmar.gradle.openapi.IntellijDiffSnapshotTestExtension;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliaryy.AnyOfContainer;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({SnapshotExtension.class, IntellijDiffSnapshotTestExtension.class})
class AnyOfContainerGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("anyOfContainer")
  void anyOfContainerGenerator_when_anyOfContainer_then_correctOutput() {
    final Generator<AnyOfContainer, PojoSettings> generator = anyOfContainerGenerator();

    final AnyOfContainer anyOfContainer =
        JavaPojos.anyOfPojo(
                sampleObjectPojo1(), allNecessityAndNullabilityVariants(), sampleObjectPojo2())
            .getAnyOfContainer()
            .orElseThrow(IllegalStateException::new);

    final Writer writer = generator.generate(anyOfContainer, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }
}
