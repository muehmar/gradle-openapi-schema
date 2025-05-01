package com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson;

import static com.github.muehmar.gradle.openapi.generator.java.generator.data.VoidData.noData;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojoXml;
import com.github.muehmar.gradle.openapi.generator.java.ref.JacksonRefs;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.settings.JsonSupport;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.XmlSupport;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class JacksonAnnotationGeneratorTest {

  @Test
  void jsonIgnore_when_enabledJackson_then_correctOutputAndRefs() {
    final Generator<Void, PojoSettings> generator = JacksonAnnotationGenerator.jsonIgnore();

    final Writer writer = generator.generate(noData(), defaultTestSettings(), javaWriter());

    assertEquals(1, writer.getRefs().size());
    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_IGNORE::equals));
    assertEquals("@JsonIgnore", writer.asString());
  }

  @Test
  void jsonIgnore_when_disabledJackson_then_noOutput() {
    final Generator<Void, PojoSettings> generator = JacksonAnnotationGenerator.jsonIgnore();

    final Writer writer =
        generator.generate(
            noData(), defaultTestSettings().withJsonSupport(JsonSupport.NONE), javaWriter());

    assertTrue(writer.getRefs().isEmpty());
    assertEquals("", writer.asString());
  }

  @Test
  void jsonProperty_when_enabledJackson_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        JacksonAnnotationGenerator.jsonProperty();
    final JavaPojoMember pojoMember =
        TestJavaPojoMembers.birthdate(Necessity.REQUIRED, Nullability.NULLABLE);

    final Writer writer = generator.generate(pojoMember, defaultTestSettings(), javaWriter());

    assertEquals(1, writer.getRefs().size());
    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_PROPERTY::equals));
    assertEquals("@JsonProperty(\"birthdate\")", writer.asString());
  }

  @Test
  void jsonProperty_when_disabledJackson_then_noOutput() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        JacksonAnnotationGenerator.jsonProperty();
    final JavaPojoMember pojoMember =
        TestJavaPojoMembers.birthdate(Necessity.REQUIRED, Nullability.NULLABLE);

    final Writer writer =
        generator.generate(
            pojoMember, defaultTestSettings().withJsonSupport(JsonSupport.NONE), javaWriter());

    assertTrue(writer.getRefs().isEmpty());
    assertEquals("", writer.asString());
  }

  @Test
  void jsonIncludeNonNull_when_enabledJackson_then_correctOutputAndRefs() {
    final Generator<Void, PojoSettings> generator = JacksonAnnotationGenerator.jsonIncludeNonNull();

    final Writer writer = generator.generate(noData(), defaultTestSettings(), javaWriter());

    assertEquals(1, writer.getRefs().size());
    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_INCLUDE::equals));
    assertEquals("@JsonInclude(JsonInclude.Include.NON_NULL)", writer.asString());
  }

  @Test
  void jsonIncludeNonNull_when_disabledJackson_then_noOutput() {
    final Generator<Void, PojoSettings> generator = JacksonAnnotationGenerator.jsonIncludeNonNull();

    final Writer writer =
        generator.generate(
            noData(), defaultTestSettings().withJsonSupport(JsonSupport.NONE), javaWriter());

    assertTrue(writer.getRefs().isEmpty());
    assertEquals("", writer.asString());
  }

  @Test
  void jsonValue_when_enabledJackson_then_correctOutputAndRefs() {
    final Generator<Void, PojoSettings> generator = JacksonAnnotationGenerator.jsonValue();

    final Writer writer = generator.generate(noData(), defaultTestSettings(), javaWriter());

    assertEquals(1, writer.getRefs().size());
    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_VALUE::equals));
    assertEquals("@JsonValue", writer.asString());
  }

  @Test
  void jsonValue_when_disabledJackson_then_correctOutputAndRefs() {
    final Generator<Void, PojoSettings> generator = JacksonAnnotationGenerator.jsonValue();

    final Writer writer =
        generator.generate(
            noData(), defaultTestSettings().withJsonSupport(JsonSupport.NONE), javaWriter());

    assertTrue(writer.getRefs().isEmpty());
    assertEquals("", writer.asString());
  }

  @Test
  void jsonCreator_when_enabledJackson_then_correctOutputAndRefs() {
    final Generator<Void, PojoSettings> generator = JacksonAnnotationGenerator.jsonCreator();

    final Writer writer = generator.generate(noData(), defaultTestSettings(), javaWriter());

    assertEquals(1, writer.getRefs().size());
    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_CREATOR::equals));
    assertEquals("@JsonCreator", writer.asString());
  }

  @Test
  void jsonCreator_when_disabledJackson_then_correctOutputAndRefs() {
    final Generator<Void, PojoSettings> generator = JacksonAnnotationGenerator.jsonCreator();

    final Writer writer =
        generator.generate(
            noData(), defaultTestSettings().withJsonSupport(JsonSupport.NONE), javaWriter());

    assertTrue(writer.getRefs().isEmpty());
    assertEquals("", writer.asString());
  }

  @Test
  void jsonPojoBuilderWithPrefix_when_enabledJackson_then_correctOutputAndRefs() {
    final Generator<Object, PojoSettings> generator =
        JacksonAnnotationGenerator.jsonPojoBuilderWithPrefix("set");

    final Writer writer = generator.generate(noData(), defaultTestSettings(), javaWriter());

    assertEquals(1, writer.getRefs().size());
    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_POJO_BUILDER::equals));
    assertEquals("@JsonPOJOBuilder(withPrefix = \"set\")", writer.asString());
  }

  @Test
  void jsonPojoBuilderWithPrefix_when_disabledJackson_then_noOutput() {
    final Generator<Object, PojoSettings> generator =
        JacksonAnnotationGenerator.jsonPojoBuilderWithPrefix("set");

    final Writer writer =
        generator.generate(
            noData(), defaultTestSettings().withJsonSupport(JsonSupport.NONE), javaWriter());

    assertEquals(0, writer.getRefs().size());
    assertEquals("", writer.asString());
  }

  @Test
  void jacksonXmlRootElement_when_enabledXmlJackson_then_correctOutputAndRefs() {
    final Generator<JavaPojoXml, PojoSettings> generator =
        JacksonAnnotationGenerator.jacksonXmlRootElement();

    final Writer writer =
        generator.generate(
            new JavaPojoXml(Optional.of("root-name")),
            defaultTestSettings().withXmlSupport(XmlSupport.JACKSON),
            javaWriter());

    assertEquals(1, writer.getRefs().size());
    assertTrue(writer.getRefs().exists(JacksonRefs.JACKSON_XML_ROOT_ELEMENT::equals));
    assertEquals("@JacksonXmlRootElement(localName = \"root-name\")", writer.asString());
  }

  @Test
  void jacksonXmlRootElement_when_enabledXmlJacksonButNoXmlDefinition_then_noOutput() {
    final Generator<JavaPojoXml, PojoSettings> generator =
        JacksonAnnotationGenerator.jacksonXmlRootElement();

    final Writer writer =
        generator.generate(
            JavaPojoXml.noXmlDefinition(),
            defaultTestSettings().withXmlSupport(XmlSupport.JACKSON),
            javaWriter());

    assertEquals(0, writer.getRefs().size());
    assertEquals("", writer.asString());
  }

  @Test
  void jacksonXmlRootElement_when_disabledXmlJackson_then_noOutput() {
    final Generator<JavaPojoXml, PojoSettings> generator =
        JacksonAnnotationGenerator.jacksonXmlRootElement();

    final Writer writer =
        generator.generate(
            new JavaPojoXml(Optional.of("root-name")),
            defaultTestSettings().withXmlSupport(XmlSupport.NONE),
            javaWriter());

    assertEquals(0, writer.getRefs().size());
    assertEquals("", writer.asString());
  }
}
