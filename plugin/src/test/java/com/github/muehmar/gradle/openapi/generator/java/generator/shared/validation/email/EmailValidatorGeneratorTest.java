package com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.email;

import static com.github.muehmar.gradle.openapi.generator.java.generator.data.VoidData.noData;
import static com.github.muehmar.gradle.openapi.generator.java.generator.data.VoidData.noSettings;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.email.EmailValidatorGenerator.emailValidatorGenerator;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class EmailValidatorGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("emailValidator")
  void generate_when_emailValidator_then_matchSnapshot() {
    final Generator<Void, Void> generator = emailValidatorGenerator();

    final Writer writer = generator.generate(noData(), noSettings(), javaWriter());

    expect.toMatchSnapshot(SnapshotUtil.writerSnapshot(writer));
  }
}
