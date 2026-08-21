package com.github.muehmar.gradle.openapi.generator.java.ref;

import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.settings.JsonSupport;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.XmlSupport;
import org.junit.jupiter.api.Test;

class JacksonRefsTest {

  // ---------------------------------------------------------------------
  // Green companion cases: explicit json support selects the dialect.
  // ---------------------------------------------------------------------

  @Test
  void jsonRefs_when_jsonSupportJackson2_then_comFasterxmlRefs() {
    final PojoSettings settings =
        defaultTestSettings()
            .withJsonSupport(JsonSupport.JACKSON_2)
            .withXmlSupport(XmlSupport.NONE);

    assertEquals(
        "com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder",
        JacksonRefs.jsonPojoBuilderRef(settings));
    assertEquals(
        "com.fasterxml.jackson.databind.annotation.JsonDeserialize",
        JacksonRefs.jsonDeserializeRef(settings));
    assertEquals(
        "com.fasterxml.jackson.databind.JsonDeserializer",
        JacksonRefs.jsonDeserializerRef(settings));
    assertEquals("com.fasterxml.jackson.core.JsonParser", JacksonRefs.jsonParserRef(settings));
    assertEquals(
        "com.fasterxml.jackson.databind.DeserializationContext",
        JacksonRefs.deserializationContextRef(settings));
  }

  @Test
  void jsonRefs_when_jsonSupportJackson3_then_toolsRefs() {
    final PojoSettings settings =
        defaultTestSettings()
            .withJsonSupport(JsonSupport.JACKSON_3)
            .withXmlSupport(XmlSupport.NONE);

    assertEquals(
        "tools.jackson.databind.annotation.JsonPOJOBuilder",
        JacksonRefs.jsonPojoBuilderRef(settings));
    assertEquals(
        "tools.jackson.databind.annotation.JsonDeserialize",
        JacksonRefs.jsonDeserializeRef(settings));
    assertEquals(
        "tools.jackson.databind.ValueDeserializer", JacksonRefs.jsonDeserializerRef(settings));
    assertEquals("tools.jackson.core.JsonParser", JacksonRefs.jsonParserRef(settings));
    assertEquals(
        "tools.jackson.databind.DeserializationContext",
        JacksonRefs.deserializationContextRef(settings));
  }

  // ---------------------------------------------------------------------
  // xml-only Jackson configs must still produce the json refs.
  //
  // PojoSettings#isJacksonJson() deliberately returns true when only
  // xmlSupport is Jackson, because Jackson XML uses the databind
  // annotations. The json refs therefore fall back to the xml support's
  // Jackson generation instead of keying on getJsonSupport() alone, which
  // would emit Jackson annotations without the corresponding imports.
  // ---------------------------------------------------------------------

  @Test
  void jsonRefs_when_jsonSupportNoneAndXmlSupportJackson2_then_comFasterxmlRefs() {
    final PojoSettings settings =
        defaultTestSettings()
            .withJsonSupport(JsonSupport.NONE)
            .withXmlSupport(XmlSupport.JACKSON_2);

    assertEquals(
        "com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder",
        JacksonRefs.jsonPojoBuilderRef(settings));
    assertEquals(
        "com.fasterxml.jackson.databind.annotation.JsonDeserialize",
        JacksonRefs.jsonDeserializeRef(settings));
    assertEquals(
        "com.fasterxml.jackson.databind.JsonDeserializer",
        JacksonRefs.jsonDeserializerRef(settings));
    assertEquals("com.fasterxml.jackson.core.JsonParser", JacksonRefs.jsonParserRef(settings));
    assertEquals(
        "com.fasterxml.jackson.databind.DeserializationContext",
        JacksonRefs.deserializationContextRef(settings));
  }

  @Test
  void jsonRefs_when_jsonSupportNoneAndXmlSupportJackson3_then_toolsRefs() {
    final PojoSettings settings =
        defaultTestSettings()
            .withJsonSupport(JsonSupport.NONE)
            .withXmlSupport(XmlSupport.JACKSON_3);

    assertEquals(
        "tools.jackson.databind.annotation.JsonPOJOBuilder",
        JacksonRefs.jsonPojoBuilderRef(settings));
    assertEquals(
        "tools.jackson.databind.annotation.JsonDeserialize",
        JacksonRefs.jsonDeserializeRef(settings));
    assertEquals(
        "tools.jackson.databind.ValueDeserializer", JacksonRefs.jsonDeserializerRef(settings));
    assertEquals("tools.jackson.core.JsonParser", JacksonRefs.jsonParserRef(settings));
    assertEquals(
        "tools.jackson.databind.DeserializationContext",
        JacksonRefs.deserializationContextRef(settings));
  }
}
