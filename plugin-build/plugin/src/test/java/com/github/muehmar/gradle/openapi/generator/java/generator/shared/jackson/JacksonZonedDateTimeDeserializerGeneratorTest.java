package com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson;

import static com.github.muehmar.gradle.openapi.generator.java.generator.data.VoidData.noData;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.settings.JsonSupport;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.XmlSupport;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class JacksonZonedDateTimeDeserializerGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("zonedDateTimeDeserializerJackson2")
  void zonedDateTimeDeserializer_when_jackson2_then_matchSnapshot() {
    final Generator<Void, PojoSettings> generator =
        JacksonZonedDateTimeDeserializerGenerator.zonedDateTimeDeserializer();

    final Writer writer =
        generator.generate(
            noData(),
            defaultTestSettings().withJsonSupport(JsonSupport.JACKSON_2),
            Writer.javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("zonedDateTimeDeserializerJackson3")
  void zonedDateTimeDeserializer_when_jackson3_then_matchSnapshot() {
    final Generator<Void, PojoSettings> generator =
        JacksonZonedDateTimeDeserializerGenerator.zonedDateTimeDeserializer();

    final Writer writer = generator.generate(noData(), defaultTestSettings(), Writer.javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }

  // For the xml-only config (jsonSupport=NONE, xmlSupport=JACKSON_2) the dialect
  // is taken from the xml support, so the generator emits the Jackson-2 shape
  // (JsonDeserializer, p.getText()) as in the zonedDateTimeDeserializerJackson2
  // snapshot -- not the Jackson-3 one that keying on getJsonSupport() alone would
  // produce.
  @Test
  void zonedDateTimeDeserializer_when_jsonSupportNoneAndXmlSupportJackson2_then_jackson2Shape() {
    final Generator<Void, PojoSettings> generator =
        JacksonZonedDateTimeDeserializerGenerator.zonedDateTimeDeserializer();

    final Writer writer =
        generator.generate(
            noData(),
            defaultTestSettings()
                .withJsonSupport(JsonSupport.NONE)
                .withXmlSupport(XmlSupport.JACKSON_2),
            Writer.javaWriter());

    final String output = writer.asString();

    assertTrue(
        output.contains("extends JsonDeserializer"),
        "xml-only Jackson-2 config must generate the Jackson-2 base class but was:\n" + output);
    assertFalse(
        output.contains("extends ValueDeserializer"),
        "xml-only Jackson-2 config must not generate the Jackson-3 base class but was:\n" + output);
    assertTrue(
        output.contains("p.getText()"),
        "xml-only Jackson-2 config must use the Jackson-2 accessor p.getText() but was:\n"
            + output);
    assertFalse(
        output.contains("p.getString()"),
        "xml-only Jackson-2 config must not use the Jackson-3 accessor p.getString() but was:\n"
            + output);
    assertTrue(
        output.contains("import com.fasterxml.jackson.databind.JsonDeserializer;"),
        "xml-only Jackson-2 config must import the Jackson-2 JsonDeserializer but was:\n" + output);
  }
}
