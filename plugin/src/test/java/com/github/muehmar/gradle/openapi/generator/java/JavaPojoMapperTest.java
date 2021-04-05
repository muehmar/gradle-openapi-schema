package com.github.muehmar.gradle.openapi.generator.java;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.Resources;
import com.github.muehmar.gradle.openapi.generator.Pojo;
import com.github.muehmar.gradle.openapi.generator.PojoMapper;
import com.github.muehmar.gradle.openapi.generator.PojoMember;
import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.constraints.DecimalMax;
import com.github.muehmar.gradle.openapi.generator.constraints.DecimalMin;
import com.github.muehmar.gradle.openapi.generator.constraints.Max;
import com.github.muehmar.gradle.openapi.generator.constraints.Min;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import java.util.Comparator;
import java.util.Map;
import org.junit.jupiter.api.Test;

class JavaPojoMapperTest {

  @Test
  void fromSchema_when_calledWithRealOpenApiSchemas_then_allPojosCorrectMapped() {
    final PojoMapper pojoMapper = new JavaPojoMapper();
    final PojoSettings pojoSettings =
        new PojoSettings(null, null, "Dto", false, true, PList.empty(), PList.empty());
    final PList<Pojo> pojos =
        parseOpenApiResourceEntries("/integration/completespec/openapi.yml")
            .flatMap(entry -> pojoMapper.fromSchema(entry.getKey(), entry.getValue(), pojoSettings))
            .sort(Comparator.comparing(pojo -> pojo.className(new JavaResolver())));

    assertEquals(6, pojos.size());

    assertEquals(
        new Pojo(
            "Language",
            "",
            "Dto",
            PList.of(
                new PojoMember("key", "", JavaType.ofName("int"), false),
                new PojoMember("name", "", JavaType.ofName("String"), false)),
            false),
        pojos.apply(0));

    assertEquals(
        new Pojo(
            "User",
            "",
            "Dto",
            PList.of(
                new PojoMember("id", "", JavaTypes.UUID, false),
                new PojoMember("externalId", "", JavaType.ofName("long"), false),
                new PojoMember("user", "", JavaTypes.STRING, false),
                new PojoMember("birthday", "", JavaTypes.LOCAL_DATE, true),
                new PojoMember(
                    "email", "", JavaTypes.STRING.withConstraints(Constraints.ofEmail()), true),
                new PojoMember("city", "", JavaTypes.STRING, false),
                new PojoMember(
                    "age",
                    "",
                    JavaTypes.INTEGER.withConstraints(
                        Constraints.ofMin(new Min(18)).withMax(new Max(50))),
                    true),
                new PojoMember(
                    "height",
                    "",
                    JavaTypes.FLOAT.withConstraints(
                        Constraints.ofDecimalMinAndMax(
                            new DecimalMin("120.0", true), new DecimalMax("199.99", false))),
                    true),
                new PojoMember("lastLogin", "", JavaTypes.LOCAL_DATE_TIME, true),
                new PojoMember(
                    "role", "", JavaType.javaEnum(PList.of("Admin", "User", "Visitor")), true),
                new PojoMember(
                    "currencies", "", JavaType.javaMap(JavaTypes.STRING, JavaTypes.STRING), true),
                new PojoMember(
                    "interests",
                    "",
                    JavaType.javaMap(
                        JavaTypes.STRING,
                        JavaType.javaList(JavaType.ofReference("UserInterests", "Dto"))),
                    true),
                new PojoMember(
                    "languages",
                    "",
                    JavaType.javaMap(JavaTypes.STRING, JavaType.ofReference("Language", "Dto")),
                    true),
                new PojoMember(
                    "hobbies",
                    "",
                    JavaType.javaMap(JavaTypes.STRING, JavaType.ofReference("UserHobbies", "Dto")),
                    true),
                new PojoMember("data", "Some user related data", JavaTypes.OBJECT, true)),
            false),
        pojos.apply(1));

    assertEquals(
        new Pojo(
            "UserGroup",
            "",
            "Dto",
            PList.of(
                new PojoMember("owner", "", JavaType.ofReference("User", "Dto"), true),
                new PojoMember(
                    "members", "", JavaType.javaList(JavaType.ofReference("User", "Dto")), true),
                new PojoMember(
                    "languages",
                    "",
                    JavaType.javaList(JavaType.ofReference("UserGroupLanguages", "Dto")),
                    true)),
            false),
        pojos.apply(2));

    assertEquals(
        new Pojo(
            "UserGroupLanguages",
            "",
            "Dto",
            PList.of(
                new PojoMember("id", "", JavaTypes.STRING, true),
                new PojoMember("name", "", JavaTypes.STRING, true)),
            false),
        pojos.apply(3));

    assertEquals(
        new Pojo(
            "UserHobbies",
            "",
            "Dto",
            PList.of(
                new PojoMember("name", "", JavaTypes.STRING, false),
                new PojoMember("description", "", JavaTypes.STRING, true)),
            false),
        pojos.apply(4));

    assertEquals(
        new Pojo(
            "UserInterests",
            "",
            "Dto",
            PList.of(
                new PojoMember("name", "", JavaTypes.STRING, true),
                new PojoMember("prio", "", JavaTypes.INTEGER, true)),
            false),
        pojos.apply(5));
  }

  private static PList<Map.Entry<String, Schema>> parseOpenApiResourceEntries(String resource) {
    final SwaggerParseResult swaggerParseResult =
        new OpenAPIV3Parser().readContents(Resources.readString(resource));
    final OpenAPI openAPI = swaggerParseResult.getOpenAPI();
    return PList.fromIter(openAPI.getComponents().getSchemas().entrySet());
  }
}
