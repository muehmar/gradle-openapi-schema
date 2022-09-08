package com.github.muehmar.gradle.openapi.generator.java;

import static com.github.muehmar.gradle.openapi.generator.model.Necessity.OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.REQUIRED;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.Resources;
import com.github.muehmar.gradle.openapi.generator.PojoMapper;
import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.constraints.Max;
import com.github.muehmar.gradle.openapi.generator.constraints.Min;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.DateSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.media.UUIDSchema;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class JavaPojoMapperTest {

  @Test
  void fromSchema_when_arraySchema_then_returnArrayPojo() {
    final JavaPojoMapper pojoMapper = new JavaPojoMapper();
    final ArraySchema schema = new ArraySchema().items(new IntegerSchema());

    // method call
    final PList<Pojo> pojos =
        pojoMapper.fromSchemas(
            new OpenApiPojo(Name.ofString("PojoName"), schema), TestPojoSettings.defaultSettings());

    assertEquals(1, pojos.size());
    final Pojo pojo = pojos.head();
    assertEquals(
        Pojo.ofArray(
            Name.ofString("PojoName"),
            "",
            "Dto",
            new PojoMember(
                Name.ofString("value"),
                "",
                JavaType.javaList(JavaTypes.INTEGER),
                REQUIRED,
                NOT_NULLABLE)),
        pojo);
  }

  @Test
  void fromSchema_when_classMappedType_then_correctMappedTypePojo() {
    final JavaPojoMapper pojoMapper = new JavaPojoMapper();
    final ClassTypeMapping classTypeMapping =
        new ClassTypeMapping("String", "CustomString", "ch.custom.string.package");
    final PojoSettings pojoSettings =
        TestPojoSettings.defaultSettings()
            .withClassTypeMappings(Collections.singletonList(classTypeMapping));

    final HashMap<String, Schema> properties = new HashMap<>();
    properties.put("name", new StringSchema());
    final Schema<?> schema = new ObjectSchema().properties(properties);

    // method call
    final PList<Pojo> pojos =
        pojoMapper.fromSchemas(new OpenApiPojo(Name.ofString("PojoName"), schema), pojoSettings);

    assertEquals(1, pojos.size());
    final Pojo pojo = pojos.head();
    assertEquals(
        Pojo.ofObject(
            Name.ofString("PojoName"),
            "",
            "Dto",
            PList.single(
                new PojoMember(
                    Name.ofString("name"),
                    "",
                    JavaType.ofUserDefinedAndImport("CustomString", "ch.custom.string.package"),
                    OPTIONAL,
                    NOT_NULLABLE))),
        pojo);
  }

  @Test
  void fromSchema_when_calledWithRealOpenApiSchemas_then_allPojosCorrectMapped() {
    final PojoMapper pojoMapper = new JavaPojoMapper();

    final PList<Pojo> pojos =
        parseOpenApiResourceEntries("/integration/completespec/openapi.yml")
            .flatMap(
                entry ->
                    // method call
                    pojoMapper.fromSchemas(
                        new OpenApiPojo(Name.ofString(entry.getKey()), entry.getValue()),
                        TestPojoSettings.defaultSettings()))
            .sort(Comparator.comparing(pojo -> pojo.className(new JavaResolver()).asString()));

    assertEquals(6, pojos.size());

    assertEquals(
        Pojo.ofObject(
            Name.ofString("Language"),
            "",
            "Dto",
            PList.of(
                new PojoMember(Name.ofString("key"), "", JavaTypes.INTEGER, REQUIRED, NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("name"), "", JavaTypes.STRING, REQUIRED, NOT_NULLABLE))),
        pojos.apply(0));

    assertEquals(
        Pojo.ofObject(
            Name.ofString("User"),
            "",
            "Dto",
            PList.of(
                new PojoMember(Name.ofString("id"), "", JavaTypes.UUID, REQUIRED, NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("externalId"), "", JavaTypes.LONG, REQUIRED, NOT_NULLABLE),
                new PojoMember(Name.ofString("user"), "", JavaTypes.STRING, REQUIRED, NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("birthday"), "", JavaTypes.LOCAL_DATE, OPTIONAL, NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("email"),
                    "",
                    JavaTypes.STRING.withConstraints(Constraints.ofEmail()),
                    OPTIONAL,
                    NOT_NULLABLE),
                new PojoMember(Name.ofString("city"), "", JavaTypes.STRING, REQUIRED, NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("age"),
                    "",
                    JavaTypes.INTEGER.withConstraints(
                        Constraints.ofMin(new Min(18)).withMax(new Max(50))),
                    OPTIONAL,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("height"),
                    "",
                    JavaTypes.FLOAT.withConstraints(
                        Constraints.ofMinAndMax(new Min(120), new Max(199))),
                    OPTIONAL,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("lastLogin"),
                    "",
                    JavaTypes.LOCAL_DATE_TIME,
                    OPTIONAL,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("role"),
                    "",
                    JavaType.javaEnum(PList.of("Admin", "User", "Visitor")),
                    OPTIONAL,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("currencies"),
                    "",
                    JavaType.javaMap(JavaTypes.STRING, JavaTypes.STRING),
                    OPTIONAL,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("interests"),
                    "",
                    JavaType.javaMap(
                        JavaTypes.STRING,
                        JavaType.javaList(
                            JavaType.ofReference(Name.ofString("UserInterests"), "Dto"))),
                    OPTIONAL,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("languages"),
                    "",
                    JavaType.javaMap(
                        JavaTypes.STRING, JavaType.ofReference(Name.ofString("Language"), "Dto")),
                    OPTIONAL,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("hobbies"),
                    "",
                    JavaType.javaMap(
                        JavaTypes.STRING,
                        JavaType.ofReference(Name.ofString("UserHobbies"), "Dto")),
                    OPTIONAL,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("data"),
                    "Some user related data",
                    JavaTypes.OBJECT,
                    OPTIONAL,
                    NOT_NULLABLE))),
        pojos.apply(1));

    assertEquals(
        Pojo.ofObject(
            Name.ofString("UserGroup"),
            "",
            "Dto",
            PList.of(
                new PojoMember(
                    Name.ofString("owner"),
                    "",
                    JavaType.ofReference(Name.ofString("User"), "Dto"),
                    OPTIONAL,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("members"),
                    "",
                    JavaType.javaList(JavaType.ofReference(Name.ofString("User"), "Dto")),
                    OPTIONAL,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("languages"),
                    "",
                    JavaType.javaList(
                        JavaType.ofReference(Name.ofString("UserGroupLanguages"), "Dto")),
                    OPTIONAL,
                    NOT_NULLABLE))),
        pojos.apply(2));

    assertEquals(
        Pojo.ofObject(
            Name.ofString("UserGroupLanguages"),
            "",
            "Dto",
            PList.of(
                new PojoMember(Name.ofString("id"), "", JavaTypes.STRING, OPTIONAL, NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("name"), "", JavaTypes.STRING, OPTIONAL, NOT_NULLABLE))),
        pojos.apply(3));

    assertEquals(
        Pojo.ofObject(
            Name.ofString("UserHobbies"),
            "",
            "Dto",
            PList.of(
                new PojoMember(Name.ofString("name"), "", JavaTypes.STRING, REQUIRED, NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("description"), "", JavaTypes.STRING, OPTIONAL, NOT_NULLABLE))),
        pojos.apply(4));

    assertEquals(
        Pojo.ofObject(
            Name.ofString("UserInterests"),
            "",
            "Dto",
            PList.of(
                new PojoMember(Name.ofString("name"), "", JavaTypes.STRING, OPTIONAL, NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("prio"), "", JavaTypes.INTEGER, OPTIONAL, NOT_NULLABLE))),
        pojos.apply(5));
  }

  @Test
  void fromSchema_when_singleInlineDefinition_then_composedPojoAndInlineDefinitionPojoCreated() {
    final JavaPojoMapper pojoMapper = new JavaPojoMapper();

    final Schema<?> objectSchema =
        new ObjectSchema()
            .addProperties("user", new StringSchema())
            .addProperties("key", new IntegerSchema());

    final ComposedSchema composedSchema = new ComposedSchema();
    composedSchema.addAllOfItem(objectSchema);

    // method call
    final PList<Pojo> pojos =
        pojoMapper
            .fromSchemas(
                new OpenApiPojo(
                    PojoName.ofNameAndSuffix(Name.ofString("ComposedPojoName"), "Dto"),
                    composedSchema),
                TestPojoSettings.defaultSettings())
            .sort(Comparator.comparing(pojo -> pojo.getName().asString()));

    assertEquals(2, pojos.size());

    assertEquals(
        Pojo.ofObject(
            Name.ofString("ComposedPojoName"),
            "",
            "Dto",
            PList.of(
                new PojoMember(Name.ofString("user"), "", JavaTypes.STRING, OPTIONAL, NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("key"), "", JavaTypes.INTEGER, OPTIONAL, NOT_NULLABLE))),
        pojos.apply(0));

    assertEquals(
        Pojo.ofObject(
            Name.ofString("ComposedPojoNameAllOf"),
            "",
            "Dto",
            PList.of(
                new PojoMember(Name.ofString("user"), "", JavaTypes.STRING, OPTIONAL, NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("key"), "", JavaTypes.INTEGER, OPTIONAL, NOT_NULLABLE))),
        pojos.apply(1));
  }

  @Test
  void fromSchema_when_twoInlineDefinitionAndReference_then_allPojosCreated() {
    final JavaPojoMapper pojoMapper = new JavaPojoMapper();

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
                    new OpenApiPojo(Name.ofString("ComposedPojoName"), composedSchema),
                    new OpenApiPojo(Name.ofString("ReferenceSchema"), referenceSchema)),
                TestPojoSettings.defaultSettings())
            .sort(Comparator.comparing(pojo -> pojo.getName().asString()));

    assertEquals(4, pojos.size());

    assertEquals(
        Pojo.ofObject(
            Name.ofString("ComposedPojoName"),
            "",
            "Dto",
            PList.of(
                new PojoMember(
                    Name.ofString("color"), "", JavaTypes.STRING, OPTIONAL, NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("group"), "", JavaTypes.INTEGER, OPTIONAL, NOT_NULLABLE),
                new PojoMember(Name.ofString("user"), "", JavaTypes.STRING, OPTIONAL, NOT_NULLABLE),
                new PojoMember(Name.ofString("key"), "", JavaTypes.INTEGER, OPTIONAL, NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("registerDate"),
                    "",
                    JavaTypes.LOCAL_DATE,
                    OPTIONAL,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("languages"),
                    "",
                    JavaType.javaList(JavaTypes.STRING),
                    OPTIONAL,
                    NOT_NULLABLE))),
        pojos.apply(0));

    assertEquals(
        Pojo.ofObject(
            Name.ofString("ComposedPojoNameAllOf0"),
            "",
            "Dto",
            PList.of(
                new PojoMember(Name.ofString("user"), "", JavaTypes.STRING, OPTIONAL, NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("key"), "", JavaTypes.INTEGER, OPTIONAL, NOT_NULLABLE))),
        pojos.apply(1));

    assertEquals(
        Pojo.ofObject(
            Name.ofString("ComposedPojoNameAllOf1"),
            "",
            "Dto",
            PList.of(
                new PojoMember(
                    Name.ofString("registerDate"),
                    "",
                    JavaTypes.LOCAL_DATE,
                    OPTIONAL,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("languages"),
                    "",
                    JavaType.javaList(JavaTypes.STRING),
                    OPTIONAL,
                    NOT_NULLABLE))),
        pojos.apply(2));

    assertEquals(
        Pojo.ofObject(
            Name.ofString("ReferenceSchema"),
            "",
            "Dto",
            PList.of(
                new PojoMember(
                    Name.ofString("color"), "", JavaTypes.STRING, OPTIONAL, NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("group"), "", JavaTypes.INTEGER, OPTIONAL, NOT_NULLABLE))),
        pojos.apply(3));
  }

  @Test
  void fromSchemas_when_rootUuidSchemaUsedAsReference_then_inlinedInPojo() {
    final JavaPojoMapper pojoMapper = new JavaPojoMapper();

    final Schema<?> userSchema =
        new ObjectSchema()
            .addProperties("key", new Schema<>().$ref("#/components/schemas/UserKey"));
    final Schema<?> keySchema = new UUIDSchema().description("User key");

    // method call
    final PList<Pojo> pojos =
        pojoMapper.fromSchemas(
            PList.of(
                new OpenApiPojo(Name.ofString("UserKey"), keySchema),
                new OpenApiPojo(Name.ofString("User"), userSchema)),
            TestPojoSettings.defaultSettings());

    assertEquals(1, pojos.size());
    assertEquals(
        Pojo.ofObject(
            Name.ofString("User"),
            "",
            "Dto",
            PList.single(
                new PojoMember(
                    Name.ofString("key"), "User key", JavaTypes.UUID, OPTIONAL, NOT_NULLABLE))),
        pojos.apply(0));
  }

  @Test
  void fromSchemas_when_rootIntegerSchemaUsedAsReference_then_inlinedInPojo() {
    final JavaPojoMapper pojoMapper = new JavaPojoMapper();

    final Schema<?> userSchema =
        new ObjectSchema()
            .addProperties("age", new Schema<>().$ref("#/components/schemas/UserAge"));
    final Schema<?> ageSchema = new IntegerSchema().description("User age");

    // method call
    final PList<Pojo> pojos =
        pojoMapper.fromSchemas(
            PList.of(
                new OpenApiPojo(Name.ofString("UserAge"), ageSchema),
                new OpenApiPojo(Name.ofString("User"), userSchema)),
            TestPojoSettings.defaultSettings());

    assertEquals(1, pojos.size());
    assertEquals(
        Pojo.ofObject(
            Name.ofString("User"),
            "",
            "Dto",
            PList.single(
                new PojoMember(
                    Name.ofString("age"), "User age", JavaTypes.INTEGER, OPTIONAL, NOT_NULLABLE))),
        pojos.apply(0));
  }

  @Test
  void fromSchemas_when_rootNumberSchemaUsedAsReference_then_inlinedInPojo() {
    final JavaPojoMapper pojoMapper = new JavaPojoMapper();

    final Schema<?> userSchema =
        new ObjectSchema()
            .addProperties("height", new Schema<>().$ref("#/components/schemas/UserHeight"));
    final Schema<?> heightSchema = new NumberSchema().description("User height");

    // method call
    final PList<Pojo> pojos =
        pojoMapper.fromSchemas(
            PList.of(
                new OpenApiPojo(Name.ofString("UserHeight"), heightSchema),
                new OpenApiPojo(Name.ofString("User"), userSchema)),
            TestPojoSettings.defaultSettings());

    assertEquals(1, pojos.size());
    assertEquals(
        Pojo.ofObject(
            Name.ofString("User"),
            "",
            "Dto",
            PList.single(
                new PojoMember(
                    Name.ofString("height"),
                    "User height",
                    JavaTypes.FLOAT,
                    OPTIONAL,
                    NOT_NULLABLE))),
        pojos.apply(0));
  }

  @Test
  void fromSchemas_when_rootBooleanSchemaUsedAsReference_then_inlinedInPojo() {
    final JavaPojoMapper pojoMapper = new JavaPojoMapper();

    final Schema<?> userSchema =
        new ObjectSchema()
            .addProperties("admin", new Schema<>().$ref("#/components/schemas/UserAdmin"));
    final Schema<?> adminSchema = new BooleanSchema().description("User is admin");

    // method call
    final PList<Pojo> pojos =
        pojoMapper.fromSchemas(
            PList.of(
                new OpenApiPojo(Name.ofString("UserAdmin"), adminSchema),
                new OpenApiPojo(Name.ofString("User"), userSchema)),
            TestPojoSettings.defaultSettings());

    assertEquals(1, pojos.size());
    assertEquals(
        Pojo.ofObject(
            Name.ofString("User"),
            "",
            "Dto",
            PList.single(
                new PojoMember(
                    Name.ofString("admin"),
                    "User is admin",
                    JavaTypes.BOOLEAN,
                    OPTIONAL,
                    NOT_NULLABLE))),
        pojos.apply(0));
  }

  @Test
  void fromSchemas_when_rootEnumSchemaUsedAsReference_then_discreteEnumPojoCreated() {
    final JavaPojoMapper pojoMapper = new JavaPojoMapper();

    final Schema<?> userSchema =
        new ObjectSchema()
            .addProperties("gender", new Schema<>().$ref("#/components/schemas/Gender"));
    final Schema<String> genderSchema = new StringSchema();
    genderSchema.setEnum(Arrays.asList("FEMALE", "MALE", "UNKNOWN"));
    genderSchema.description("Gender of a user");

    // method call
    final PList<Pojo> pojos =
        pojoMapper.fromSchemas(
            PList.of(
                new OpenApiPojo(Name.ofString("Gender"), genderSchema),
                new OpenApiPojo(Name.ofString("User"), userSchema)),
            TestPojoSettings.defaultSettings());

    assertEquals(2, pojos.size());
    assertEquals(
        Pojo.ofEnum(
            Name.ofString("Gender"),
            "Gender of a user",
            "Dto",
            JavaType.javaEnum(PList.of("FEMALE", "MALE", "UNKNOWN"))),
        pojos.apply(0));
    assertEquals(
        Pojo.ofObject(
            Name.ofString("User"),
            "",
            "Dto",
            PList.single(
                new PojoMember(
                    Name.ofString("gender"),
                    "Gender of a user",
                    Type.simpleOfName(Name.ofString("GenderDto")),
                    OPTIONAL,
                    NOT_NULLABLE))),
        pojos.apply(1));
  }

  @Test
  void fromSchemas_when_lowercaseNamesAndReferences_then_allNamesStartUppercase() {
    final JavaPojoMapper pojoMapper = new JavaPojoMapper();

    final Schema<?> userSchema =
        new ObjectSchema()
            .addProperties("gender", new Schema<>().$ref("#/components/schemas/gender"));
    final Schema<String> genderSchema = new StringSchema();
    genderSchema.setEnum(Arrays.asList("FEMALE", "MALE", "UNKNOWN"));
    genderSchema.description("Gender of a user");

    // method call
    final PList<Pojo> pojos =
        pojoMapper.fromSchemas(
            PList.of(
                new OpenApiPojo(Name.ofString("gender"), genderSchema),
                new OpenApiPojo(Name.ofString("user"), userSchema)),
            TestPojoSettings.defaultSettings());

    assertEquals(2, pojos.size());
    assertEquals(Name.ofString("Gender"), pojos.apply(0).getName());
    assertEquals(Name.ofString("User"), pojos.apply(1).getName());
    assertEquals(1, pojos.apply(1).getMembers().size());
    assertEquals(
        Name.ofString("GenderDto"), pojos.apply(1).getMembers().apply(0).getType().getFullName());
  }

  private static PList<Map.Entry<String, Schema>> parseOpenApiResourceEntries(String resource) {
    final SwaggerParseResult swaggerParseResult =
        new OpenAPIV3Parser().readContents(Resources.readString(resource));
    final OpenAPI openAPI = swaggerParseResult.getOpenAPI();
    return PList.fromIter(openAPI.getComponents().getSchemas().entrySet());
  }
}
