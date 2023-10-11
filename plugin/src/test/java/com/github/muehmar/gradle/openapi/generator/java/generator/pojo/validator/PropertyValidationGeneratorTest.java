package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.validator;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.validator.PropertyValidationGenerator.propertyValidationGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers.requiredInteger;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.*;

import au.com.origin.snapshots.Expect;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class PropertyValidationGeneratorTest {
  private Expect expect;

  @Test
  void generate_when_integerWithMinAndMax_then_metchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator = propertyValidationGenerator();

    final Writer writer =
        generator.generate(requiredInteger(), defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(SnapshotUtil.writerSnapshot(writer));
  }
}
