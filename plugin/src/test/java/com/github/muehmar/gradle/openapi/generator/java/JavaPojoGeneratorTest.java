package com.github.muehmar.gradle.openapi.generator.java;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.Resources;
import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.constraints.DecimalMax;
import com.github.muehmar.gradle.openapi.generator.constraints.DecimalMin;
import com.github.muehmar.gradle.openapi.generator.constraints.Max;
import com.github.muehmar.gradle.openapi.generator.constraints.Min;
import com.github.muehmar.gradle.openapi.generator.constraints.Pattern;
import com.github.muehmar.gradle.openapi.generator.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.data.Name;
import com.github.muehmar.gradle.openapi.generator.data.Pojo;
import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import com.github.muehmar.gradle.openapi.generator.data.Type;
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
        Pojo.ofObject(
            Name.of("User"),
            "User of the Application. This description is intentionally longer to see if its wrapped to a new line.",
            "Dto",
            PList.of(
                new PojoMember(Name.of("id"), "ID of this user", JavaType.ofName("long"), false),
                new PojoMember(Name.of("name"), "Name of this user", JavaTypes.STRING, false),
                new PojoMember(
                    Name.of("language"),
                    "Preferred language of this user",
                    JavaType.javaEnum(PList.of("GERMAN", "ENGLISH")),
                    true)));

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
        Pojo.ofObject(
            Name.of("User"),
            "User of the Application",
            "Dto",
            PList.of(
                new PojoMember(Name.of("id"), "ID of this user", JavaType.ofName("long"), false),
                new PojoMember(Name.of("name"), "Name of this user", JavaTypes.STRING, false),
                new PojoMember(
                    Name.of("language"),
                    "Preferred language of this user",
                    JavaType.javaEnum(PList.of("GERMAN", "ENGLISH")),
                    true)));

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
        Pojo.ofObject(
            Name.of("User"),
            "User of the Application",
            "Dto",
            PList.of(
                new PojoMember(Name.of("id"), "ID of this user", JavaType.ofName("long"), false),
                new PojoMember(Name.of("name"), "Name of this user", JavaTypes.STRING, false),
                new PojoMember(
                    Name.of("language"),
                    "Preferred language of this user",
                    JavaType.javaEnum(PList.of("GERMAN", "ENGLISH")),
                    true)));

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
        Pojo.ofObject(
            Name.of("User"),
            "User of the Application",
            "Dto",
            PList.of(
                new PojoMember(
                    Name.of("id"),
                    "ID of this user",
                    JavaType.ofName("long").withConstraints(Constraints.ofMax(new Max(50))),
                    false),
                new PojoMember(
                    Name.of("name"),
                    "Name of this user",
                    JavaTypes.STRING.withConstraints(Constraints.ofSize(Size.of(10, 15))),
                    false),
                new PojoMember(
                    Name.of("lastName"),
                    "Lastname of this user",
                    JavaTypes.STRING.withConstraints(Constraints.ofSize(Size.ofMin(10))),
                    false),
                new PojoMember(
                    Name.of("nickName"),
                    "Nickname of this user",
                    JavaTypes.STRING.withConstraints(Constraints.ofSize(Size.ofMax(50))),
                    false),
                new PojoMember(
                    Name.of("email"),
                    "Email of this user",
                    JavaTypes.STRING.withConstraints(Constraints.ofEmail()),
                    false),
                new PojoMember(
                    Name.of("height"),
                    "Height of this user",
                    JavaTypes.DOUBLE.withConstraints(
                        Constraints.ofDecimalMin(new DecimalMin("120.0", true))
                            .withDecimalMax(new DecimalMax("199", false))),
                    false),
                new PojoMember(
                    Name.of("level"),
                    "Level of this user",
                    JavaType.ofName("Long").withConstraints(Constraints.ofMin(new Min(5))),
                    true),
                new PojoMember(
                    Name.of("uppercase"),
                    "Something uppercase",
                    JavaTypes.STRING.withConstraints(
                        Constraints.ofPattern(Pattern.ofUnescapedString("^(\\d[A-Z]*)"))),
                    true),
                new PojoMember(
                    Name.of("anotherPojo"),
                    "Another Pojo",
                    JavaType.ofUserDefined("AnotherPojo"),
                    true)));

    pojoGenerator.generatePojo(pojo, pojoSettings);

    assertEquals(
        Resources.readString("/java/pojos/UserDtoConstraints.jv"), writer.asString().trim());
  }

  @Test
  void generatePojo_when_enumPojo_then_correctPojoGenerated() {
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
        Pojo.ofEnum(
            Name.of("Gender"), "Gender of a user", "Dto", Type.simpleOfName(Name.of("Name")));

    pojoGenerator.generatePojo(pojo, pojoSettings);

    assertEquals(Resources.readString("/java/pojos/GenderEnumDto.jv"), writer.asString().trim());
  }

  @Test
  void generatePojo_when_enumPojoAndJacksonSupport_then_correctPojoGenerated() {
    final TestStringWriter writer = new TestStringWriter();
    final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator(() -> writer);

    final PojoSettings pojoSettings =
        new PojoSettings(
            JsonSupport.JACKSON,
            "com.github.muehmar",
            "Dto",
            false,
            true,
            PList.empty(),
            PList.empty());

    final Pojo pojo =
        Pojo.ofEnum(
            Name.of("Gender"), "Gender of a user", "Dto", Type.simpleOfName(Name.of("Name")));

    pojoGenerator.generatePojo(pojo, pojoSettings);

    assertEquals(
        Resources.readString("/java/pojos/GenderEnumDtoJsonSupportJackson.jv"),
        writer.asString().trim());
  }
}
