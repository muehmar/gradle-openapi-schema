package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaComposedPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.settings.JavaModifier;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@ExtendWith(SnapshotExtension.class)
class ValidCountValidationMethodTest {
  private Expect expect;

  public static Stream<Arguments> compositionTypeAndSettings() {
    final PList<ComposedPojo.CompositionType> compositionTypes =
        PList.of(ComposedPojo.CompositionType.values());
    final PojoSettings validationMethodsSettings =
        TestPojoSettings.defaultSettings()
            .withValidationMethods(
                TestPojoSettings.defaultValidationMethods()
                    .withModifier(JavaModifier.PROTECTED)
                    .withDeprecatedAnnotation(true));
    final PList<PojoSettings> settings =
        TestPojoSettings.validationVariants().add(validationMethodsSettings);
    return compositionTypes
        .flatMap(type -> settings.map(setting -> Arguments.arguments(type, setting)))
        .toStream();
  }

  @ParameterizedTest
  @MethodSource("compositionTypeAndSettings")
  @SnapshotName("ValidCountValidationMethod")
  void generate_when_pojoAndSettings_then_correctOutput(
      ComposedPojo.CompositionType type, PojoSettings settings) {
    final Generator<JavaComposedPojo, PojoSettings> generator =
        ValidCountValidationMethod.generator();

    final JavaComposedPojo javaPojo = JavaPojos.composedPojo(type);
    final Writer writer = generator.generate(javaPojo, settings, Writer.createDefault());

    final String scenario =
        PList.of(
                type.name(),
                Boolean.toString(settings.isEnableValidation()),
                settings.getValidationApi().getValue(),
                settings.getValidationMethods().getModifier())
            .mkString(",");

    expect
        .scenario(scenario)
        .toMatchSnapshot(writer.getRefs().mkString("\n") + "\n\n" + writer.asString());
  }
}
