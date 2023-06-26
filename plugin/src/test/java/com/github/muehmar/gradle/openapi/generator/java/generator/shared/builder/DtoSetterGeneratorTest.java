package com.github.muehmar.gradle.openapi.generator.java.generator.shared.builder;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.builder.DtoSetterGenerator.dtoSetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.GetterSuffixes;
import com.github.muehmar.gradle.openapi.generator.settings.GetterSuffixesBuilder;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
class DtoSetterGeneratorTest {
  private Expect expect;

  @Test
  void generator_when_calledWithComposedPojo_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = dtoSetterGenerator();

    final GetterSuffixes getterSuffixes =
        GetterSuffixesBuilder.create()
            .requiredSuffix("")
            .requiredNullableSuffix("Opt1")
            .optionalSuffix("Opt2")
            .optionalNullableSuffix("Tristate")
            .build();
    final Writer writer =
        generator.generate(
            JavaPojos.oneOfPojo(
                sampleObjectPojo1(), JavaPojos.allNecessityAndNullabilityVariants()),
            TestPojoSettings.defaultSettings().withGetterSuffixes(getterSuffixes),
            Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());
  }
}
