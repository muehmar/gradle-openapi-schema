package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.additionalproperties;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.additionalproperties.AdditionalPropertiesCastMethod.additionalPropertiesCastMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SnapshotTest
class AdditionalPropertiesCastMethodTest {
  private Expect expect;

  @ParameterizedTest
  @MethodSource("additionalProperties")
  void generate_when_additionalPropertiesNotAllowed_then_noOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        additionalPropertiesCastMethodGenerator();

    final JavaAdditionalProperties additionalProperties = JavaAdditionalProperties.notAllowed();
    final JavaObjectPojo pojo =
        JavaPojos.objectPojo().withAdditionalProperties(additionalProperties);

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    assertEquals("", writer.asString());
  }

  public static Stream<Arguments> additionalProperties() {
    return PList.of(
            JavaAdditionalProperties.notAllowed(),
            JavaAdditionalProperties.allowedFor(JavaTypes.anyType().withNullability(NULLABLE)),
            JavaAdditionalProperties.allowedFor(
                JavaTypes.stringType().withNullability(NOT_NULLABLE)))
        .toStream()
        .map(Arguments::of);
  }

  @Test
  @SnapshotName("additionalPropertiesTypeIsNotNullableString")
  void generate_when_additionalPropertiesTypeIsNotNullableString_then_matchSnapshot() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        additionalPropertiesCastMethodGenerator();

    final JavaAdditionalProperties additionalProperties =
        JavaAdditionalProperties.allowedFor(JavaTypes.stringType());
    final JavaObjectPojo pojo =
        JavaPojos.objectPojo().withAdditionalProperties(additionalProperties);

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("additionalPropertiesTypeIsNullableString")
  void generate_when_additionalPropertiesTypeIsNullableString_then_matchSnapshot() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        additionalPropertiesCastMethodGenerator();

    final JavaAdditionalProperties additionalProperties =
        JavaAdditionalProperties.allowedFor(JavaTypes.stringType().withNullability(NULLABLE));
    final JavaObjectPojo pojo =
        JavaPojos.objectPojo().withAdditionalProperties(additionalProperties);

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
