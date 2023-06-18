package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaStringType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@ExtendWith(SnapshotExtension.class)
class ConversionMethodGeneratorTest {
  private Expect expect;

  @ParameterizedTest
  @MethodSource("composedPojoArguments")
  @SnapshotName("composedPojo")
  void generate_when_composedPojo_then_correctOutput(
      Function<PList<JavaPojo>, JavaObjectPojo> createComposedPojo, String name) {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        ConversionMethodGenerator.composedAsDtoMethods();

    final JavaObjectPojo pojo1 = JavaPojos.sampleObjectPojo1();

    final JavaObjectPojo pojo2 =
        JavaPojos.objectPojo(
            PList.single(JavaPojoMembers.requiredBirthdate()),
            JavaAdditionalProperties.allowedFor(
                JavaStringType.wrap(StringType.noFormat(), TypeMappings.empty())));

    final JavaObjectPojo composedPojo = createComposedPojo.apply(PList.of(pojo1, pojo2));

    final Writer writer =
        generator.generate(
            composedPojo, TestPojoSettings.defaultSettings(), Writer.createDefault());

    expect.scenario(name).toMatchSnapshot(writer.asString());
  }

  public static Stream<Arguments> composedPojoArguments() {
    return Stream.of(
        arguments((Function<PList<JavaPojo>, JavaObjectPojo>) (JavaPojos::anyOfPojo), "anyOf"),
        arguments((Function<PList<JavaPojo>, JavaObjectPojo>) (JavaPojos::oneOfPojo), "oneOf"));
  }

  @Test
  void generate_when_nonComposedPojo_then_noOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        ConversionMethodGenerator.composedAsDtoMethods();

    final JavaObjectPojo composedPojo = JavaPojos.sampleObjectPojo1();

    final Writer writer =
        generator.generate(
            composedPojo, TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals("", writer.asString());
  }
}
