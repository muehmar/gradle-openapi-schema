package com.github.muehmar.gradle.openapi.generator.java;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.Resources;
import com.github.muehmar.gradle.openapi.generator.PojoMapper;
import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.constraints.Max;
import com.github.muehmar.gradle.openapi.generator.constraints.Min;
import com.github.muehmar.gradle.openapi.generator.data.Name;
import com.github.muehmar.gradle.openapi.generator.data.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.data.Pojo;
import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.DateSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class JavaPojoMapperTest {

  @Test
  void fromSchema_when_arraySchema_then_returnArrayPojo() {
    final JavaPojoMapper pojoMapper = new JavaPojoMapper();
    final PojoSettings pojoSettings =
        new PojoSettings(null, null, "Dto", false, true, PList.empty(), PList.empty());
    final ArraySchema schema = new ArraySchema().items(new IntegerSchema());

    // method call
    final PList<Pojo> pojos =
        pojoMapper.fromSchemas(new OpenApiPojo(Name.of("PojoName"), schema), pojoSettings);

    assertEquals(1, pojos.size());
    final Pojo pojo = pojos.head();
    assertEquals(
        new Pojo(
            Name.of("PojoName"),
            "",
            "Dto",
            PList.single(
                new PojoMember(Name.of("value"), "", JavaType.javaList(JavaTypes.INTEGER), false)),
            true),
        pojo);
  }

  @Test
  void fromSchema_when_classMappedType_then_correctMappedTypePojo() {
    final JavaPojoMapper pojoMapper = new JavaPojoMapper();
    final ClassTypeMapping classTypeMapping =
        new ClassTypeMapping("String", "CustomString", "ch.custom.string.package");
    final PojoSettings pojoSettings =
        new PojoSettings(
            null, null, "Dto", false, true, PList.single(classTypeMapping), PList.empty());

    final HashMap<String, Schema> properties = new HashMap<>();
    properties.put("name", new StringSchema());
    final Schema<?> schema = new ObjectSchema().properties(properties);

    // method call
    final PList<Pojo> pojos =
        pojoMapper.fromSchemas(new OpenApiPojo(Name.of("PojoName"), schema), pojoSettings);

    assertEquals(1, pojos.size());
    final Pojo pojo = pojos.head();
    assertEquals(
        new Pojo(
            Name.of("PojoName"),
            "",
            "Dto",
            PList.single(
                new PojoMember(
                    Name.of("name"),
                    "",
                    JavaType.ofUserDefinedAndImport("CustomString", "ch.custom.string.package"),
                    true)),
            false),
        pojo);
  }

  @Test
  void fromSchema_when_calledWithRealOpenApiSchemas_then_allPojosCorrectMapped() {
    final PojoMapper pojoMapper = new JavaPojoMapper();
    final PojoSettings pojoSettings =
        new PojoSettings(null, null, "Dto", false, true, PList.empty(), PList.empty());

    final PList<Pojo> pojos =
        parseOpenApiResourceEntries("/integration/completespec/openapi.yml")
            .flatMap(
                entry ->
                    // method call
                    pojoMapper.fromSchemas(
                        new OpenApiPojo(Name.of(entry.getKey()), entry.getValue()), pojoSettings))
            .sort(Comparator.comparing(pojo -> pojo.className(new JavaResolver()).asString()));

    assertEquals(6, pojos.size());

    assertEquals(
        new Pojo(
            Name.of("Language"),
            "",
            "Dto",
            PList.of(
                new PojoMember(Name.of("key"), "", JavaType.ofName("int"), false),
                new PojoMember(Name.of("name"), "", JavaType.ofName("String"), false)),
            false),
        pojos.apply(0));

    assertEquals(
        new Pojo(
            Name.of("User"),
            "",
            "Dto",
            PList.of(
                new PojoMember(Name.of("id"), "", JavaTypes.UUID, false),
                new PojoMember(Name.of("externalId"), "", JavaType.ofName("long"), false),
                new PojoMember(Name.of("user"), "", JavaTypes.STRING, false),
                new PojoMember(Name.of("birthday"), "", JavaTypes.LOCAL_DATE, true),
                new PojoMember(
                    Name.of("email"),
                    "",
                    JavaTypes.STRING.withConstraints(Constraints.ofEmail()),
                    true),
                new PojoMember(Name.of("city"), "", JavaTypes.STRING, false),
                new PojoMember(
                    Name.of("age"),
                    "",
                    JavaTypes.INTEGER.withConstraints(
                        Constraints.ofMin(new Min(18)).withMax(new Max(50))),
                    true),
                new PojoMember(
                    Name.of("height"),
                    "",
                    JavaTypes.FLOAT.withConstraints(
                        Constraints.ofMinAndMax(new Min(120), new Max(199))),
                    true),
                new PojoMember(Name.of("lastLogin"), "", JavaTypes.LOCAL_DATE_TIME, true),
                new PojoMember(
                    Name.of("role"),
                    "",
                    JavaType.javaEnum(PList.of("Admin", "User", "Visitor")),
                    true),
                new PojoMember(
                    Name.of("currencies"),
                    "",
                    JavaType.javaMap(JavaTypes.STRING, JavaTypes.STRING),
                    true),
                new PojoMember(
                    Name.of("interests"),
                    "",
                    JavaType.javaMap(
                        JavaTypes.STRING,
                        JavaType.javaList(JavaType.ofReference(Name.of("UserInterests"), "Dto"))),
                    true),
                new PojoMember(
                    Name.of("languages"),
                    "",
                    JavaType.javaMap(
                        JavaTypes.STRING, JavaType.ofReference(Name.of("Language"), "Dto")),
                    true),
                new PojoMember(
                    Name.of("hobbies"),
                    "",
                    JavaType.javaMap(
                        JavaTypes.STRING, JavaType.ofReference(Name.of("UserHobbies"), "Dto")),
                    true),
                new PojoMember(Name.of("data"), "Some user related data", JavaTypes.OBJECT, true)),
            false),
        pojos.apply(1));

    assertEquals(
        new Pojo(
            Name.of("UserGroup"),
            "",
            "Dto",
            PList.of(
                new PojoMember(
                    Name.of("owner"), "", JavaType.ofReference(Name.of("User"), "Dto"), true),
                new PojoMember(
                    Name.of("members"),
                    "",
                    JavaType.javaList(JavaType.ofReference(Name.of("User"), "Dto")),
                    true),
                new PojoMember(
                    Name.of("languages"),
                    "",
                    JavaType.javaList(JavaType.ofReference(Name.of("UserGroupLanguages"), "Dto")),
                    true)),
            false),
        pojos.apply(2));

    assertEquals(
        new Pojo(
            Name.of("UserGroupLanguages"),
            "",
            "Dto",
            PList.of(
                new PojoMember(Name.of("id"), "", JavaTypes.STRING, true),
                new PojoMember(Name.of("name"), "", JavaTypes.STRING, true)),
            false),
        pojos.apply(3));

    assertEquals(
        new Pojo(
            Name.of("UserHobbies"),
            "",
            "Dto",
            PList.of(
                new PojoMember(Name.of("name"), "", JavaTypes.STRING, false),
                new PojoMember(Name.of("description"), "", JavaTypes.STRING, true)),
            false),
        pojos.apply(4));

    assertEquals(
        new Pojo(
            Name.of("UserInterests"),
            "",
            "Dto",
            PList.of(
                new PojoMember(Name.of("name"), "", JavaTypes.STRING, true),
                new PojoMember(Name.of("prio"), "", JavaTypes.INTEGER, true)),
            false),
        pojos.apply(5));
  }

  @Test
  void fromSchema_when_singleInlineDefinition_then_composedPojoAndInlineDefinitionPojoCreated() {
    final JavaPojoMapper pojoMapper = new JavaPojoMapper();
    final PojoSettings pojoSettings =
        new PojoSettings(null, null, "Dto", false, true, PList.empty(), PList.empty());

    final Schema<?> objectSchema =
        new ObjectSchema()
            .addProperties("user", new StringSchema())
            .addProperties("key", new IntegerSchema());

    final ComposedSchema composedSchema = new ComposedSchema();
    composedSchema.addAllOfItem(objectSchema);

    // method call
    final PList<Pojo> pojos =
        pojoMapper
            .fromSchemas(new OpenApiPojo(Name.of("ComposedPojoName"), composedSchema), pojoSettings)
            .sort(Comparator.comparing(pojo -> pojo.getName().asString()));

    assertEquals(2, pojos.size());

    assertEquals(
        new Pojo(
            Name.of("ComposedPojoName"),
            "",
            "Dto",
            PList.of(
                new PojoMember(Name.of("user"), "", JavaTypes.STRING, true),
                new PojoMember(Name.of("key"), "", JavaTypes.INTEGER, true)),
            false),
        pojos.apply(0));

    assertEquals(
        new Pojo(
            Name.of("ComposedPojoNameAllOf"),
            "",
            "Dto",
            PList.of(
                new PojoMember(Name.of("user"), "", JavaTypes.STRING, true),
                new PojoMember(Name.of("key"), "", JavaTypes.INTEGER, true)),
            false),
        pojos.apply(1));
  }

  @Test
  void fromSchema_when_twoInlineDefinitionAndReference_then_allPojosCreated() {
    final JavaPojoMapper pojoMapper = new JavaPojoMapper();
    final PojoSettings pojoSettings =
        new PojoSettings(null, null, "Dto", false, true, PList.empty(), PList.empty());

    final Schema<?> objectSchema1 =
        new ObjectSchema()
            .addProperties("user", new StringSchema())
            .addProperties("key", new IntegerSchema());

    final Schema<?> objectSchema2 =
        new ObjectSchema()
            .addProperties("registerDate", new DateSchema())
            .addProperties("languages", new ArraySchema().items(new StringSchema()));

    final Schema<?> referenceSchema =
        new ObjectSchema()
            .addProperties("color", new StringSchema())
            .addProperties("group", new IntegerSchema());

    final ComposedSchema composedSchema = new ComposedSchema();
    composedSchema
        .addAllOfItem(objectSchema1)
        .addAllOfItem(objectSchema2)
        .addAllOfItem(new Schema<>().$ref("#/components/schemas/ReferenceSchema"));

    // method call
    final PList<Pojo> pojos =
        pojoMapper
            .fromSchemas(
                PList.of(
                    new OpenApiPojo(Name.of("ComposedPojoName"), composedSchema),
                    new OpenApiPojo(Name.of("ReferenceSchema"), referenceSchema)),
                pojoSettings)
            .sort(Comparator.comparing(pojo -> pojo.getName().asString()));

    assertEquals(4, pojos.size());

    assertEquals(
        new Pojo(
            Name.of("ComposedPojoName"),
            "",
            "Dto",
            PList.of(
                new PojoMember(Name.of("color"), "", JavaTypes.STRING, true),
                new PojoMember(Name.of("group"), "", JavaTypes.INTEGER, true),
                new PojoMember(Name.of("user"), "", JavaTypes.STRING, true),
                new PojoMember(Name.of("key"), "", JavaTypes.INTEGER, true),
                new PojoMember(Name.of("registerDate"), "", JavaTypes.LOCAL_DATE, true),
                new PojoMember(
                    Name.of("languages"), "", JavaType.javaList(JavaTypes.STRING), true)),
            false),
        pojos.apply(0));

    assertEquals(
        new Pojo(
            Name.of("ComposedPojoNameAllOf0"),
            "",
            "Dto",
            PList.of(
                new PojoMember(Name.of("user"), "", JavaTypes.STRING, true),
                new PojoMember(Name.of("key"), "", JavaTypes.INTEGER, true)),
            false),
        pojos.apply(1));

    assertEquals(
        new Pojo(
            Name.of("ComposedPojoNameAllOf1"),
            "",
            "Dto",
            PList.of(
                new PojoMember(Name.of("registerDate"), "", JavaTypes.LOCAL_DATE, true),
                new PojoMember(
                    Name.of("languages"), "", JavaType.javaList(JavaTypes.STRING), true)),
            false),
        pojos.apply(2));

    assertEquals(
        new Pojo(
            Name.of("ReferenceSchema"),
            "",
            "Dto",
            PList.of(
                new PojoMember(Name.of("color"), "", JavaTypes.STRING, true),
                new PojoMember(Name.of("group"), "", JavaTypes.INTEGER, true)),
            false),
        pojos.apply(3));
  }

  private static PList<Map.Entry<String, Schema>> parseOpenApiResourceEntries(String resource) {
    final SwaggerParseResult swaggerParseResult =
        new OpenAPIV3Parser().readContents(Resources.readString(resource));
    final OpenAPI openAPI = swaggerParseResult.getOpenAPI();
    return PList.fromIter(openAPI.getComponents().getSchemas().entrySet());
  }
}
