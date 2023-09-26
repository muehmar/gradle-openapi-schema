package com.github.muehmar.gradle.openapi.generator.settings;

import static com.github.muehmar.gradle.openapi.generator.model.name.ComponentNames.componentName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ExcludedSchemasTest {
  @ParameterizedTest
  @MethodSource("filter")
  void getSchemaFilter_when_usedForPojoSchema_then_expectedFilterResult(
      ExcludedSchemas excludedSchemas, PojoSchema pojoSchema, boolean expectedFilterResult) {

    final Predicate<PojoSchema> schemaFilter = excludedSchemas.getSchemaFilter();

    assertEquals(expectedFilterResult, schemaFilter.test(pojoSchema));
  }

  public static Stream<Arguments> filter() {
    final ComponentName userName = componentName("User", "Dto");
    final ComponentName genderName = componentName("Gender", "Dto");
    final ComponentName addressName = componentName("Address", "Dto");

    return Stream.of(
        arguments(
            ExcludedSchemas.fromExcludedPojoNames(
                PList.of(userName, genderName, addressName)
                    .map(ComponentName::getPojoName)
                    .map(PojoName::getName)),
            new PojoSchema(userName, new StringSchema()),
            false),
        arguments(
            ExcludedSchemas.fromExcludedPojoNames(
                PList.of(userName, genderName, addressName)
                    .map(ComponentName::getPojoName)
                    .map(PojoName::getName)),
            new PojoSchema(genderName, new StringSchema()),
            false),
        arguments(
            ExcludedSchemas.fromExcludedPojoNames(
                PList.of(userName, genderName, addressName)
                    .map(ComponentName::getPojoName)
                    .map(PojoName::getName)),
            new PojoSchema(addressName, new StringSchema()),
            false),
        arguments(
            ExcludedSchemas.fromExcludedPojoNames(
                PList.of(userName).map(ComponentName::getPojoName).map(PojoName::getName)),
            new PojoSchema(genderName, new StringSchema()),
            true),
        arguments(
            ExcludedSchemas.fromExcludedPojoNames(
                PList.of(userName).map(ComponentName::getPojoName).map(PojoName::getName)),
            new PojoSchema(addressName, new StringSchema()),
            true),
        arguments(
            ExcludedSchemas.fromExcludedPojoNames(
                PList.of(userName, genderName)
                    .map(ComponentName::getPojoName)
                    .map(PojoName::getName)),
            new PojoSchema(addressName, new StringSchema()),
            true));
  }
}
