package com.github.muehmar.gradle.openapi.generator.settings;

import static com.github.muehmar.gradle.openapi.generator.model.name.PojoNames.pojoName;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

  // Bug: validate() accepts mixed Jackson generations. jsonSupport=JACKSON_2 combined
  // with xmlSupport=JACKSON_3 (and vice versa) would require conflicting Jackson
  // dialects (com.fasterxml.* vs tools.*) in the same generated sources and must be
  // rejected. Note: validate() currently only registers warnings and never throws;
  // IllegalArgumentException is asserted here since that is how other fatal config
  // errors are raised (e.g. unsupported values in SingleSchemaExtension).
  @Test
  void validate_when_jsonSupportJackson2AndXmlSupportJackson3_then_throws() {
    final PojoSettings settings =
        TestPojoSettings.defaultTestSettings()
            .withJsonSupport(JsonSupport.JACKSON_2)
            .withXmlSupport(XmlSupport.JACKSON_3);

    assertThrows(IllegalArgumentException.class, settings::validate);
  }

  // Bug: same as above, opposite direction (jsonSupport=JACKSON_3, xmlSupport=JACKSON_2).
  @Test
  void validate_when_jsonSupportJackson3AndXmlSupportJackson2_then_throws() {
    final PojoSettings settings =
        TestPojoSettings.defaultTestSettings()
            .withJsonSupport(JsonSupport.JACKSON_3)
            .withXmlSupport(XmlSupport.JACKSON_2);

    assertThrows(IllegalArgumentException.class, settings::validate);
  }

  @Test
  void validate_when_sameOrNoJacksonGenerations_then_doesNotThrow() {
    assertDoesNotThrow(
        () ->
            TestPojoSettings.defaultTestSettings()
                .withJsonSupport(JsonSupport.JACKSON_2)
                .withXmlSupport(XmlSupport.JACKSON_2)
                .validate());
    assertDoesNotThrow(
        () ->
            TestPojoSettings.defaultTestSettings()
                .withJsonSupport(JsonSupport.JACKSON_3)
                .withXmlSupport(XmlSupport.JACKSON_3)
                .validate());
    assertDoesNotThrow(
        () ->
            TestPojoSettings.defaultTestSettings()
                .withJsonSupport(JsonSupport.NONE)
                .withXmlSupport(XmlSupport.JACKSON_2)
                .validate());
    assertDoesNotThrow(
        () ->
            TestPojoSettings.defaultTestSettings()
                .withJsonSupport(JsonSupport.JACKSON_3)
                .withXmlSupport(XmlSupport.NONE)
                .validate());
    assertDoesNotThrow(
        () ->
            TestPojoSettings.defaultTestSettings()
                .withJsonSupport(JsonSupport.NONE)
                .withXmlSupport(XmlSupport.NONE)
                .validate());
  }
}
