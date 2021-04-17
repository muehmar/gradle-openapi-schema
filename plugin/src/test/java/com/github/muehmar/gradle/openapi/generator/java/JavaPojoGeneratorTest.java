package com.github.muehmar.gradle.openapi.generator.java;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.Resources;
import com.github.muehmar.gradle.openapi.generator.data.Pojo;
import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.constraints.DecimalMax;
import com.github.muehmar.gradle.openapi.generator.constraints.DecimalMin;
import com.github.muehmar.gradle.openapi.generator.constraints.Max;
import com.github.muehmar.gradle.openapi.generator.constraints.Min;
import com.github.muehmar.gradle.openapi.generator.constraints.Pattern;
import com.github.muehmar.gradle.openapi.generator.constraints.Size;
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
            JsonSupport.NONE,
            "com.github.muehmar",
            "Dto",
            false,
            false,
            PList.empty(),
            PList.empty());

    final Pojo pojo =
        new Pojo(
            "User",
            "User of the Application. This description is intentionally longer to see if its wrapped to a new line.",
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
            JsonSupport.JACKSON,
            "com.github.muehmar",
            "Dto",
            false,
            false,
            PList.empty(),
            PList.empty());

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
            JsonSupport.NONE,
            "com.github.muehmar",
            "Dto",
            true,
            false,
            PList.empty(),
            PList.empty());

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

  @Test
  void generatePojo_when_enableConstraints_then_correctPojoGenerated() {
    final TestStringWriter writer = new TestStringWriter();
    final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator(() -> writer);

    final PojoSettings pojoSettings =
        new PojoSettings(
            JsonSupport.NONE,
            "com.github.muehmar",
            "Dto",
            false,
            true,
            PList.empty(),
            PList.empty());

    final Pojo pojo =
        new Pojo(
            "User",
            "User of the Application",
            "Dto",
            PList.of(
                new PojoMember(
                    "id",
                    "ID of this user",
                    JavaType.ofName("long").withConstraints(Constraints.ofMax(new Max(50))),
                    false),
                new PojoMember(
                    "name",
                    "Name of this user",
                    JavaTypes.STRING.withConstraints(Constraints.ofSize(Size.of(10, 15))),
                    false),
                new PojoMember(
                    "lastName",
                    "Lastname of this user",
                    JavaTypes.STRING.withConstraints(Constraints.ofSize(Size.ofMin(10))),
                    false),
                new PojoMember(
                    "nickName",
                    "Nickname of this user",
                    JavaTypes.STRING.withConstraints(Constraints.ofSize(Size.ofMax(50))),
                    false),
                new PojoMember(
                    "email",
                    "Email of this user",
                    JavaTypes.STRING.withConstraints(Constraints.ofEmail()),
                    false),
                new PojoMember(
                    "height",
                    "Height of this user",
                    JavaTypes.DOUBLE.withConstraints(
                        Constraints.ofDecimalMin(new DecimalMin("120.0", true))
                            .withDecimalMax(new DecimalMax("199", false))),
                    false),
                new PojoMember(
                    "level",
                    "Level of this user",
                    JavaType.ofName("Long").withConstraints(Constraints.ofMin(new Min(5))),
                    true),
                new PojoMember(
                    "uppercase",
                    "Something uppercase",
                    JavaTypes.STRING.withConstraints(Constraints.ofPattern(new Pattern("[A-Z]"))),
                    true),
                new PojoMember(
                    "anotherPojo", "Another Pojo", JavaType.ofUserDefined("AnotherPojo"), true)),
            false);

    pojoGenerator.generatePojo(pojo, pojoSettings);

    assertEquals(
        Resources.readString("/java/pojos/UserDtoConstraints.jv"), writer.asString().trim());
  }
}
