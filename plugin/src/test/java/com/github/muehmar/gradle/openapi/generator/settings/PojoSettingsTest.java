package com.github.muehmar.gradle.openapi.generator.settings;

import static com.github.muehmar.gradle.openapi.generator.model.name.PojoNames.pojoName;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class PojoSettingsTest {

  @Test
  void pojoNameMapping_when_called_then_mappingAppliedInOrder() {
    final PojoSettings settings =
        TestPojoSettings.defaultTestSettings()
            .withPojoNameMappings(
                new PojoNameMappings(
                    Stream.of(
                            new ConstantNameMapping("User", "Per.son"),
                            new ConstantNameMapping(".", ""))
                        .collect(Collectors.toList())));

    final PojoNameMapping pojoNameMapping = settings.pojoNameMapping();

    final PojoName pojoName = pojoName("User", "Dto");
    final PojoName mappedPojoName = pojoNameMapping.map(pojoName);

    assertEquals("PersonDto", mappedPojoName.asString());
  }
}
