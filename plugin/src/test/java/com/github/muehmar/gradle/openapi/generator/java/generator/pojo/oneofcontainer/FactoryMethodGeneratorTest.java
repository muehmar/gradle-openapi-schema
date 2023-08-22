package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.oneofcontainer;

import static com.github.muehmar.gradle.openapi.SnapshotUtil.writerSnapshot;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.oneofcontainer.FactoryMethodGenerator.factoryMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo2;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.IntellijDiffSnapshotTestExtension;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoName;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliaryy.OneOfContainer;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({SnapshotExtension.class, IntellijDiffSnapshotTestExtension.class})
class FactoryMethodGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("oneOfPojo")
  void generate_when_oneOfPojo_then_correctOutput() {
    final Generator<OneOfContainer, PojoSettings> generator = factoryMethodGenerator();

    final JavaOneOfComposition javaOneOfComposition =
        JavaOneOfComposition.fromPojos(
            NonEmptyList.of(
                sampleObjectPojo1(),
                JavaPojos.allNecessityAndNullabilityVariants(),
                sampleObjectPojo2()));
    final OneOfContainer oneOfContainer =
        new OneOfContainer(JavaPojoName.fromNameAndSuffix("Object", "Dto"), javaOneOfComposition);
    final Writer writer =
        generator.generate(
            oneOfContainer, TestPojoSettings.defaultSettings(), Writer.createDefault());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
