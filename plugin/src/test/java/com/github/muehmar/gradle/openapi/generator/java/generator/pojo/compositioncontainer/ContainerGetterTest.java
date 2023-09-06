package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer.ContainerGetter.anyOfContainerGetter;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer.ContainerGetter.oneOfContainerGetter;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.allNecessityAndNullabilityVariants;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliaryy.AnyOfContainer;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliaryy.OneOfContainer;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class ContainerGetterTest {
  private Expect expect;

  @Test
  @SnapshotName("oneOfPojo")
  void oneOfContainerGetter_when_oneOfPojo_then_correctOutput() {
    final Generator<OneOfContainer, PojoSettings> generator = oneOfContainerGetter();

    final OneOfContainer oneOfContainer =
        JavaPojos.oneOfPojo(sampleObjectPojo1(), allNecessityAndNullabilityVariants())
            .getOneOfContainer()
            .orElseThrow(IllegalStateException::new);

    final Writer writer = generator.generate(oneOfContainer, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("anyOfPojo")
  void containerGetter_when_oneOfPojo_then_correctOutput() {
    final Generator<AnyOfContainer, PojoSettings> generator = anyOfContainerGetter();

    final AnyOfContainer anyOfContainer =
        JavaPojos.anyOfPojo(sampleObjectPojo1(), allNecessityAndNullabilityVariants())
            .getAnyOfContainer()
            .orElseThrow(IllegalStateException::new);

    final Writer writer = generator.generate(anyOfContainer, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
