package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer.FactoryMethodGenerator.anyOfFromFactoryMethods;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer.FactoryMethodGenerator.oneOfFromFactoryMethods;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo2;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaPojoNames;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliaryy.AnyOfContainer;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliaryy.OneOfContainer;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class FactoryMethodGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("oneOfPojo")
  void oneOFromFactoryMethods_when_oneOfPojo_then_correctOutput() {
    final Generator<OneOfContainer, PojoSettings> generator = oneOfFromFactoryMethods();

    final JavaOneOfComposition javaOneOfComposition =
        JavaOneOfComposition.fromPojos(
            NonEmptyList.of(
                sampleObjectPojo1(),
                JavaPojos.allNecessityAndNullabilityVariants(),
                sampleObjectPojo2()));
    final OneOfContainer oneOfContainer =
        new OneOfContainer(JavaPojoNames.fromNameAndSuffix("Object", "Dto"), javaOneOfComposition);
    final Writer writer = generator.generate(oneOfContainer, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("anyOfPojo")
  void anyOFromFactoryMethods_when_oneOfPojo_then_correctOutput() {
    final Generator<AnyOfContainer, PojoSettings> generator = anyOfFromFactoryMethods();

    final JavaAnyOfComposition anyOfComposition =
        JavaAnyOfComposition.fromPojos(
            NonEmptyList.of(
                sampleObjectPojo1(),
                JavaPojos.allNecessityAndNullabilityVariants(),
                sampleObjectPojo2()));
    final AnyOfContainer anyOfContainer =
        new AnyOfContainer(JavaPojoNames.fromNameAndSuffix("Object", "Dto"), anyOfComposition);
    final Writer writer = generator.generate(anyOfContainer, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
