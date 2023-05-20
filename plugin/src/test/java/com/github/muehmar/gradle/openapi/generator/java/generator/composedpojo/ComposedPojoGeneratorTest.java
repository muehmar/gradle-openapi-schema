package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaComposedPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.model.Discriminator;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@ExtendWith(SnapshotExtension.class)
class ComposedPojoGeneratorTest extends JavaComposedPojoVariantsTest {

  private Expect expect;

  @ParameterizedTest
  @SnapshotName("ComposedPojoGenerator")
  @MethodSource("variants")
  void generate_when_composedPojo_then_matchSnapshot(
      ComposedPojo.CompositionType type,
      Optional<Discriminator> discriminator,
      JavaComposedPojo pojo) {
    final ComposedPojoGenerator generator = new ComposedPojoGenerator();

    final Writer writer =
        generator.generate(pojo, TestPojoSettings.defaultSettings(), Writer.createDefault());

    compareSnapshot(expect, type, discriminator, pojo, writer);
  }

  @Test
  @SnapshotName("composedPojoWithEnumInSubPojos")
  void generate_when_composedPojoWithEnumInSubPojos_then_correctOutput() {
    final JavaObjectPojo pojo1 = JavaPojos.objectPojo(PList.of(JavaPojoMembers.requiredString()));
    final JavaObjectPojo pojo2 =
        JavaPojos.objectPojo(
            PList.of(JavaPojoMembers.requiredEnum(), JavaPojoMembers.requiredInteger()));
    final JavaComposedPojo javaComposedPojo =
        JavaPojos.composedPojo(PList.of(pojo1, pojo2), ComposedPojo.CompositionType.ANY_OF);
    final ComposedPojoGenerator generator = new ComposedPojoGenerator();

    final Writer writer =
        generator.generate(
            javaComposedPojo, TestPojoSettings.defaultSettings(), Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());
  }
}
