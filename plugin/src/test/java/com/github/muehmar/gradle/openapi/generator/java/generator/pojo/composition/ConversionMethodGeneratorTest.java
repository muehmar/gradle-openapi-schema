package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition.ConversionMethodGenerator.conversionMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.illegalIdentifierPojo;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaStringType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SnapshotTest
class ConversionMethodGeneratorTest {
  private Expect expect;

  @ParameterizedTest
  @MethodSource("composedPojoArguments")
  @SnapshotName("composedPojo")
  void generate_when_composedPojo_then_correctOutput(
      Function<NonEmptyList<JavaPojo>, JavaObjectPojo> createComposedPojo, String name) {
    final Generator<JavaObjectPojo, PojoSettings> generator = conversionMethodGenerator();

    final JavaObjectPojo pojo1 = JavaPojos.sampleObjectPojo1();

    final JavaObjectPojo pojo2 =
        JavaPojos.objectPojo(
            PList.single(JavaPojoMembers.requiredBirthdate()),
            JavaAdditionalProperties.allowedFor(
                JavaStringType.wrap(StringType.noFormat(), TypeMappings.empty())));

    final JavaObjectPojo composedPojo = createComposedPojo.apply(NonEmptyList.of(pojo1, pojo2));

    final Writer writer = generator.generate(composedPojo, defaultTestSettings(), javaWriter());

    expect.scenario(name).toMatchSnapshot(writer.asString());
  }

  public static Stream<Arguments> composedPojoArguments() {
    return Stream.of(
        arguments(
            (Function<NonEmptyList<JavaPojo>, JavaObjectPojo>) (JavaPojos::allOfPojo), "allOf"),
        arguments(
            (Function<NonEmptyList<JavaPojo>, JavaObjectPojo>) (JavaPojos::anyOfPojo), "anyOf"),
        arguments(
            (Function<NonEmptyList<JavaPojo>, JavaObjectPojo>) (JavaPojos::oneOfPojo), "oneOf"));
  }

  @Test
  void generate_when_nonComposedPojo_then_noOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = conversionMethodGenerator();

    final JavaObjectPojo composedPojo = JavaPojos.sampleObjectPojo1();

    final Writer writer = generator.generate(composedPojo, defaultTestSettings(), javaWriter());

    assertEquals("", writer.asString());
  }

  @Test
  @SnapshotName("composedPojoWithNullabilityAndNecessityVariants")
  void generate_when_composedPojoWithNullabilityAndNecessityVariants_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = conversionMethodGenerator();

    final JavaObjectPojo pojo1 = JavaPojos.sampleObjectPojo1();

    final JavaObjectPojo pojo2 = JavaPojos.allNecessityAndNullabilityVariants();

    final JavaObjectPojo composedPojo = JavaPojos.oneOfPojo(pojo1, pojo2);

    final Writer writer = generator.generate(composedPojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("nestedOneOf")
  void generate_when_nestedOneOf_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = conversionMethodGenerator();

    final JavaObjectPojo composedPojo =
        JavaPojos.oneOfPojo(
            JavaPojos.oneOfPojo(JavaPojos.sampleObjectPojo1(), JavaPojos.sampleObjectPojo2()));

    final Writer writer = generator.generate(composedPojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("illegalIdentifierPojo")
  void generate_when_illegalIdentifierPojo_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = conversionMethodGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.oneOfPojo(illegalIdentifierPojo(), sampleObjectPojo1()),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
