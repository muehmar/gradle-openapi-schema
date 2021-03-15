package com.github.muehmar.gradle.openapi.generator.java;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.Resources;
import com.github.muehmar.gradle.openapi.generator.Pojo;
import com.github.muehmar.gradle.openapi.generator.PojoMember;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.settings.JsonSupport;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.writer.TestStringWriter;
import org.junit.jupiter.api.Test;

class JavaPojoGeneratorTest {

  @Test
  void generatePojo_when_minimalPojoSetting_then_correctPojoGenerated() {
    final TestStringWriter writer = new TestStringWriter();
    final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator(() -> writer);

    final PojoSettings pojoSettings =
        new PojoSettings(
            JsonSupport.NONE, "com.github.muehmar", "Dto", false, PList.empty(), PList.empty());

    final Pojo pojo =
        new Pojo(
            "User",
            "User of the Application",
            "Dto",
            PList.of(
                new PojoMember("id", "ID of this user", JavaType.ofName("long"), false),
                new PojoMember("name", "Name of this user", JavaTypes.STRING, false),
                new PojoMember(
                    "language",
                    "Preferred language of this user",
                    JavaType.javaEnum(PList.of("GERMAN", "ENGLISH")),
                    true)),
            false);

    pojoGenerator.generatePojo(pojo, pojoSettings);

    assertEquals(Resources.readString("/java/pojos/UserDtoMinimal.jv"), writer.asString().trim());
  }

  @Test
  void generatePojo_when_jsonSupportJackson_then_correctPojoGenerated() {
    final TestStringWriter writer = new TestStringWriter();
    final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator(() -> writer);

    final PojoSettings pojoSettings =
        new PojoSettings(
            JsonSupport.JACKSON, "com.github.muehmar", "Dto", false, PList.empty(), PList.empty());

    final Pojo pojo =
        new Pojo(
            "User",
            "User of the Application",
            "Dto",
            PList.of(
                new PojoMember("id", "ID of this user", JavaType.ofName("long"), false),
                new PojoMember("name", "Name of this user", JavaTypes.STRING, false),
                new PojoMember(
                    "language",
                    "Preferred language of this user",
                    JavaType.javaEnum(PList.of("GERMAN", "ENGLISH")),
                    true)),
            false);

    pojoGenerator.generatePojo(pojo, pojoSettings);

    assertEquals(
        Resources.readString("/java/pojos/UserDtoJsonSupportJackson.jv"), writer.asString().trim());
  }

  @Test
  void generatePojo_when_enabledSafeBuilder_then_correctPojoGenerated() {
    final TestStringWriter writer = new TestStringWriter();
    final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator(() -> writer);

    final PojoSettings pojoSettings =
        new PojoSettings(
            JsonSupport.NONE, "com.github.muehmar", "Dto", true, PList.empty(), PList.empty());

    final Pojo pojo =
        new Pojo(
            "User",
            "User of the Application",
            "Dto",
            PList.of(
                new PojoMember("id", "ID of this user", JavaType.ofName("long"), false),
                new PojoMember("name", "Name of this user", JavaTypes.STRING, false),
                new PojoMember(
                    "language",
                    "Preferred language of this user",
                    JavaType.javaEnum(PList.of("GERMAN", "ENGLISH")),
                    true)),
            false);

    pojoGenerator.generatePojo(pojo, pojoSettings);

    assertEquals(
        Resources.readString("/java/pojos/UserDtoEnabledSafeBuilder.jv"), writer.asString().trim());
  }
}
