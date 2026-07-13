package com.github.muehmar.gradle.openapi.generator.settings;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Mixing jackson-2 and jackson-3 between jsonSupport and xmlSupport produces a single generated
 * file with annotations of both generations, where each mapper silently ignores the other
 * generation's annotations. Such configurations must be rejected with a clear error.
 */
class MixedJacksonSupportValidationTest {

  @Test
  void validate_when_jsonSupportJackson2AndXmlSupportJackson3_then_throws() {
    final PojoSettings settings =
        TestPojoSettings.defaultTestSettings()
            .withJsonSupport(JsonSupport.JACKSON_2)
            .withXmlSupport(XmlSupport.JACKSON_3);

    assertThrows(IllegalArgumentException.class, settings::validate);
  }

  @Test
  void validate_when_jsonSupportJackson3AndXmlSupportJackson2_then_throws() {
    final PojoSettings settings =
        TestPojoSettings.defaultTestSettings()
            .withJsonSupport(JsonSupport.JACKSON_3)
            .withXmlSupport(XmlSupport.JACKSON_2);

    assertThrows(IllegalArgumentException.class, settings::validate);
  }
}
