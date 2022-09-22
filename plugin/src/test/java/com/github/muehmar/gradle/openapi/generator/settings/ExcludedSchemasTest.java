package com.github.muehmar.gradle.openapi.generator.settings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
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
    final PojoName userPojoName = PojoName.ofNameAndSuffix("User", "Dto");
    final PojoName genderPojoName = PojoName.ofNameAndSuffix("Gender", "Dto");
    final PojoName addressPojoName = PojoName.ofNameAndSuffix("Address", "Dto");

    return Stream.of(
        arguments(
            ExcludedSchemas.fromExcludedPojoNames(
                PList.of(userPojoName, genderPojoName, addressPojoName)),
            new PojoSchema(userPojoName, new StringSchema()),
            false),
        arguments(
            ExcludedSchemas.fromExcludedPojoNames(
                PList.of(userPojoName, genderPojoName, addressPojoName)),
            new PojoSchema(genderPojoName, new StringSchema()),
            false),
        arguments(
            ExcludedSchemas.fromExcludedPojoNames(
                PList.of(userPojoName, genderPojoName, addressPojoName)),
            new PojoSchema(addressPojoName, new StringSchema()),
            false),
        arguments(
            ExcludedSchemas.fromExcludedPojoNames(PList.of(userPojoName)),
            new PojoSchema(genderPojoName, new StringSchema()),
            true),
        arguments(
            ExcludedSchemas.fromExcludedPojoNames(PList.of(userPojoName)),
            new PojoSchema(addressPojoName, new StringSchema()),
            true),
        arguments(
            ExcludedSchemas.fromExcludedPojoNames(PList.of(userPojoName, genderPojoName)),
            new PojoSchema(addressPojoName, new StringSchema()),
            true));
  }
}
