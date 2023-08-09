package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.oneofcontainer;

import static com.github.muehmar.gradle.openapi.SnapshotUtil.writerSnapshot;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.oneofcontainer.ContainerGetter.containerGetter;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.allNecessityAndNullabilityVariants;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import com.github.muehmar.gradle.openapi.IntellijDiffSnapshotTestExtension;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliaryy.OneOfContainer;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({SnapshotExtension.class, IntellijDiffSnapshotTestExtension.class})
class ContainerGetterTest {
  private Expect expect;

  @Test
  void test() {
    final Generator<OneOfContainer, PojoSettings> generator = containerGetter();

    final OneOfContainer oneOfContainer =
        JavaPojos.oneOfPojo(sampleObjectPojo1(), allNecessityAndNullabilityVariants())
            .getOneOfContainer()
            .get();

    final Writer writer =
        generator.generate(
            oneOfContainer, TestPojoSettings.defaultSettings(), Writer.createDefault());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
