package com.github.muehmar.gradle.openapi.generator.mapper.processor;

import static com.github.muehmar.gradle.openapi.generator.model.Necessity.OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.REQUIRED;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.type.NumericType.Format.INTEGER;
import static com.github.muehmar.gradle.openapi.generator.model.type.StringType.Format.DATE;
import static com.github.muehmar.gradle.openapi.generator.model.type.StringType.Format.DATE_TIME;
import static com.github.muehmar.gradle.openapi.generator.model.type.StringType.Format.EMAIL;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.Resources;
import com.github.muehmar.gradle.openapi.generator.PojoMapper;
import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.constraints.Max;
import com.github.muehmar.gradle.openapi.generator.constraints.Min;
import com.github.muehmar.gradle.openapi.generator.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ArrayPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.EnumPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.type.ArrayType;
import com.github.muehmar.gradle.openapi.generator.model.type.BooleanType;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
import com.github.muehmar.gradle.openapi.generator.model.type.MapType;
import com.github.muehmar.gradle.openapi.generator.model.type.NoType;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class PojoMapperImplTest {

  @Test
  void fromSchema_when_arraySchema_then_returnArrayPojo() {
    final PojoMapper pojoMapper = PojoMapperImpl.create();
    final ArraySchema schema = new ArraySchema().items(new IntegerSchema());
    schema.setMaxItems(50);

    // method call
    final OpenApiPojo openApiPojo =
        new OpenApiPojo(PojoName.ofNameAndSuffix(Name.ofString("PojoName"), "Dto"), schema);
    final PList<Pojo> pojos = pojoMapper.fromSchemas(openApiPojo);

    assertEquals(1, pojos.size());
    final Pojo pojo = pojos.head();
    assertEquals(
        ArrayPojo.of(
            PojoName.ofNameAndSuffix(Name.ofString("PojoName"), "Dto"),
            "",
            NumericType.ofFormat(INTEGER),
            Constraints.ofSize(Size.ofMax(50))),
        pojo);
  }

  @Test
  void fromSchema_when_classMappedType_then_correctMappedTypePojo() {
    final PojoMapper pojoMapper = PojoMapperImpl.create();

    final HashMap<String, Schema> properties = new HashMap<>();
    properties.put("name", new StringSchema());
    final Schema<?> schema = new ObjectSchema().properties(properties);

    // method call
    final PList<Pojo> pojos =
        pojoMapper.fromSchemas(
            new OpenApiPojo(PojoName.ofNameAndSuffix(Name.ofString("PojoName"), "Dto"), schema));

    assertEquals(1, pojos.size());
    final Pojo pojo = pojos.head();
    assertEquals(
        ObjectPojo.of(
            PojoName.ofNameAndSuffix(Name.ofString("PojoName"), "Dto"),
            "",
            PList.single(
                new PojoMember(
                    Name.ofString("name"),
                    "",
                    StringType.ofFormat(StringType.Format.NONE),
                    OPTIONAL,
                    NOT_NULLABLE))),
        pojo);
  }

  @Test
  void fromSchema_when_calledWithRealOpenApiSchemas_then_allPojosCorrectMapped() {
    final PojoMapper pojoMapper = PojoMapperImpl.create();

    final PList<Pojo> pojos =
        parseOpenApiResourceEntries("/integration/completespec/openapi.yml")
            .flatMap(
                entry ->
                    // method call
                    pojoMapper.fromSchemas(
                        new OpenApiPojo(
                            PojoName.ofNameAndSuffix(entry.getKey(), "Dto"), entry.getValue())))
            .sort(Comparator.comparing(pojo -> pojo.getName().asString()));

    assertEquals(6, pojos.size());

    assertEquals(
        ObjectPojo.of(
            PojoName.ofNameAndSuffix("Language", "Dto"),
            "",
            PList.of(
                new PojoMember(
                    Name.ofString("key"), "", NumericType.formatInteger(), REQUIRED, NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("name"), "", StringType.noFormat(), REQUIRED, NOT_NULLABLE))),
        pojos.apply(0));

    assertEquals(
        ObjectPojo.of(
            PojoName.ofNameAndSuffix("User", "Dto"),
            "",
            PList.of(
                new PojoMember(Name.ofString("id"), "", StringType.uuid(), REQUIRED, NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("externalId"),
                    "",
                    NumericType.formatLong(),
                    REQUIRED,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("user"), "", StringType.noFormat(), REQUIRED, NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("birthday"),
                    "",
                    StringType.ofFormat(DATE),
                    OPTIONAL,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("email"),
                    "",
                    StringType.ofFormat(EMAIL).withConstraints(Constraints.ofEmail()),
                    OPTIONAL,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("city"), "", StringType.noFormat(), REQUIRED, NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("age"),
                    "",
                    NumericType.formatInteger()
                        .withConstraints(Constraints.ofMin(new Min(18)).withMax(new Max(50))),
                    OPTIONAL,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("height"),
                    "",
                    NumericType.formatFloat()
                        .withConstraints(Constraints.ofMinAndMax(new Min(120), new Max(199))),
                    OPTIONAL,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("lastLogin"),
                    "",
                    StringType.ofFormat(DATE_TIME),
                    OPTIONAL,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("role"),
                    "",
                    EnumType.ofNameAndMembers(
                        Name.ofString("RoleEnum"), PList.of("Admin", "User", "Visitor")),
                    OPTIONAL,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("currencies"),
                    "",
                    MapType.ofKeyAndValueType(StringType.noFormat(), StringType.noFormat()),
                    OPTIONAL,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("interests"),
                    "",
                    MapType.ofKeyAndValueType(
                        StringType.noFormat(),
                        ArrayType.ofItemType(
                            ObjectType.ofName(PojoName.ofNameAndSuffix("UserInterests", "Dto")))),
                    OPTIONAL,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("languages"),
                    "",
                    MapType.ofKeyAndValueType(
                        StringType.noFormat(),
                        ObjectType.ofName(PojoName.ofNameAndSuffix("Language", "Dto"))),
                    OPTIONAL,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("hobbies"),
                    "",
                    MapType.ofKeyAndValueType(
                        StringType.noFormat(),
                        ObjectType.ofName(PojoName.ofNameAndSuffix("UserHobbies", "Dto"))),
                    OPTIONAL,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("data"),
                    "Some user related data",
                    NoType.create(),
                    OPTIONAL,
                    NOT_NULLABLE))),
        pojos.apply(1));

    assertEquals(
        ObjectPojo.of(
            PojoName.ofNameAndSuffix("UserGroup", "Dto"),
            "",
            PList.of(
                new PojoMember(
                    Name.ofString("owner"),
                    "",
                    ObjectType.ofName(PojoName.ofNameAndSuffix("User", "Dto")),
                    OPTIONAL,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("members"),
                    "",
                    ArrayType.ofItemType(
                        ObjectType.ofName(PojoName.ofNameAndSuffix("User", "Dto"))),
                    OPTIONAL,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("languages"),
                    "",
                    ArrayType.ofItemType(
                        ObjectType.ofName(PojoName.ofNameAndSuffix("UserGroupLanguages", "Dto"))),
                    OPTIONAL,
                    NOT_NULLABLE))),
        pojos.apply(2));

    assertEquals(
        ObjectPojo.of(
            PojoName.ofNameAndSuffix("UserGroupLanguages", "Dto"),
            "",
            PList.of(
                new PojoMember(
                    Name.ofString("id"), "", StringType.noFormat(), OPTIONAL, NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("name"), "", StringType.noFormat(), OPTIONAL, NOT_NULLABLE))),
        pojos.apply(3));

    assertEquals(
        ObjectPojo.of(
            PojoName.ofNameAndSuffix("UserHobbies", "Dto"),
            "",
            PList.of(
                new PojoMember(
                    Name.ofString("name"), "", StringType.noFormat(), REQUIRED, NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("description"),
                    "",
                    StringType.noFormat(),
                    OPTIONAL,
                    NOT_NULLABLE))),
        pojos.apply(4));

    assertEquals(
        ObjectPojo.of(
            PojoName.ofNameAndSuffix("UserInterests", "Dto"),
            "",
            PList.of(
                new PojoMember(
                    Name.ofString("name"), "", StringType.noFormat(), OPTIONAL, NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("prio"),
                    "",
                    NumericType.formatInteger(),
                    OPTIONAL,
                    NOT_NULLABLE))),
        pojos.apply(5));
  }

  @Test
  void fromSchema_when_singleInlineDefinition_then_composedPojoAndInlineDefinitionPojoCreated() {
    final PojoMapper pojoMapper = PojoMapperImpl.create();

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
                    composedSchema))
            .sort(Comparator.comparing(pojo -> pojo.getName().asString()));

    assertEquals(2, pojos.size());

    assertEquals(
        ObjectPojo.of(
            PojoName.ofNameAndSuffix("ComposedPojoNameAllOf", "Dto"),
            "",
            PList.of(
                new PojoMember(
                    Name.ofString("user"), "", StringType.noFormat(), OPTIONAL, NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("key"),
                    "",
                    NumericType.formatInteger(),
                    OPTIONAL,
                    NOT_NULLABLE))),
        pojos.apply(0));

    assertEquals(
        ObjectPojo.of(
            PojoName.ofNameAndSuffix("ComposedPojoName", "Dto"),
            "",
            PList.of(
                new PojoMember(
                    Name.ofString("user"), "", StringType.noFormat(), OPTIONAL, NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("key"),
                    "",
                    NumericType.formatInteger(),
                    OPTIONAL,
                    NOT_NULLABLE))),
        pojos.apply(1));
  }

  @Test
  void fromSchema_when_twoInlineDefinitionAndReference_then_allPojosCreated() {
    final PojoMapper pojoMapper = PojoMapperImpl.create();

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
                    new OpenApiPojo(
                        PojoName.ofNameAndSuffix("ComposedPojoName", "Dto"), composedSchema),
                    new OpenApiPojo(
                        PojoName.ofNameAndSuffix("ReferenceSchema", "Dto"), referenceSchema)))
            .sort(Comparator.comparing(pojo -> pojo.getName().asString()));

    assertEquals(4, pojos.size());

    assertEquals(
        ObjectPojo.of(
            PojoName.ofNameAndSuffix("ComposedPojoNameAllOf0", "Dto"),
            "",
            PList.of(
                new PojoMember(
                    Name.ofString("user"), "", StringType.noFormat(), OPTIONAL, NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("key"),
                    "",
                    NumericType.formatInteger(),
                    OPTIONAL,
                    NOT_NULLABLE))),
        pojos.apply(0));

    assertEquals(
        ObjectPojo.of(
            PojoName.ofNameAndSuffix("ComposedPojoNameAllOf1", "Dto"),
            "",
            PList.of(
                new PojoMember(
                    Name.ofString("registerDate"),
                    "",
                    StringType.ofFormat(DATE),
                    OPTIONAL,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("languages"),
                    "",
                    ArrayType.ofItemType(StringType.noFormat()),
                    OPTIONAL,
                    NOT_NULLABLE))),
        pojos.apply(1));

    assertEquals(
        ObjectPojo.of(
            PojoName.ofNameAndSuffix("ComposedPojoName", "Dto"),
            "",
            PList.of(
                new PojoMember(
                    Name.ofString("color"), "", StringType.noFormat(), OPTIONAL, NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("group"),
                    "",
                    NumericType.formatInteger(),
                    OPTIONAL,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("user"), "", StringType.noFormat(), OPTIONAL, NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("key"), "", NumericType.formatInteger(), OPTIONAL, NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("registerDate"),
                    "",
                    StringType.ofFormat(DATE),
                    OPTIONAL,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("languages"),
                    "",
                    ArrayType.ofItemType(StringType.noFormat()),
                    OPTIONAL,
                    NOT_NULLABLE))),
        pojos.apply(2));

    assertEquals(
        ObjectPojo.of(
            PojoName.ofNameAndSuffix("ReferenceSchema", "Dto"),
            "",
            PList.of(
                new PojoMember(
                    Name.ofString("color"), "", StringType.noFormat(), OPTIONAL, NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("group"),
                    "",
                    NumericType.formatInteger(),
                    OPTIONAL,
                    NOT_NULLABLE))),
        pojos.apply(3));
  }

  @Test
  void fromSchemas_when_rootUuidSchemaUsedAsReference_then_inlinedInPojo() {
    final PojoMapper pojoMapper = PojoMapperImpl.create();

    final Schema<?> userSchema =
        new ObjectSchema()
            .addProperties("key", new Schema<>().$ref("#/components/schemas/UserKey"));
    final Schema<?> keySchema = new UUIDSchema().description("User key");

    // method call
    final PList<Pojo> pojos =
        pojoMapper.fromSchemas(
            PList.of(
                new OpenApiPojo(PojoName.ofNameAndSuffix("UserKey", "Dto"), keySchema),
                new OpenApiPojo(PojoName.ofNameAndSuffix("User", "Dto"), userSchema)));

    assertEquals(1, pojos.size());
    assertEquals(
        ObjectPojo.of(
            PojoName.ofNameAndSuffix("User", "Dto"),
            "",
            PList.single(
                new PojoMember(
                    Name.ofString("key"), "User key", StringType.uuid(), OPTIONAL, NOT_NULLABLE))),
        pojos.apply(0));
  }

  @Test
  void fromSchemas_when_rootIntegerSchemaUsedAsReference_then_inlinedInPojo() {
    final PojoMapper pojoMapper = PojoMapperImpl.create();

    final Schema<?> userSchema =
        new ObjectSchema()
            .addProperties("age", new Schema<>().$ref("#/components/schemas/UserAge"));
    final Schema<?> ageSchema = new IntegerSchema().description("User age");

    // method call
    final PList<Pojo> pojos =
        pojoMapper.fromSchemas(
            PList.of(
                new OpenApiPojo(PojoName.ofNameAndSuffix("UserAge", "Dto"), ageSchema),
                new OpenApiPojo(PojoName.ofNameAndSuffix("User", "Dto"), userSchema)));

    assertEquals(1, pojos.size());
    assertEquals(
        ObjectPojo.of(
            PojoName.ofNameAndSuffix("User", "Dto"),
            "",
            PList.single(
                new PojoMember(
                    Name.ofString("age"),
                    "User age",
                    NumericType.formatInteger(),
                    OPTIONAL,
                    NOT_NULLABLE))),
        pojos.apply(0));
  }

  @Test
  void fromSchemas_when_rootNumberSchemaUsedAsReference_then_inlinedInPojo() {
    final PojoMapper pojoMapper = PojoMapperImpl.create();

    final Schema<?> userSchema =
        new ObjectSchema()
            .addProperties("height", new Schema<>().$ref("#/components/schemas/UserHeight"));
    final Schema<?> heightSchema = new NumberSchema().description("User height");

    // method call
    final PList<Pojo> pojos =
        pojoMapper.fromSchemas(
            PList.of(
                new OpenApiPojo(PojoName.ofNameAndSuffix("UserHeight", "Dto"), heightSchema),
                new OpenApiPojo(PojoName.ofNameAndSuffix("User", "Dto"), userSchema)));

    assertEquals(1, pojos.size());
    assertEquals(
        ObjectPojo.of(
            PojoName.ofNameAndSuffix("User", "Dto"),
            "",
            PList.single(
                new PojoMember(
                    Name.ofString("height"),
                    "User height",
                    NumericType.formatFloat(),
                    OPTIONAL,
                    NOT_NULLABLE))),
        pojos.apply(0));
  }

  @Test
  void fromSchemas_when_rootBooleanSchemaUsedAsReference_then_inlinedInPojo() {
    final PojoMapper pojoMapper = PojoMapperImpl.create();

    final Schema<?> userSchema =
        new ObjectSchema()
            .addProperties("admin", new Schema<>().$ref("#/components/schemas/UserAdmin"));
    final Schema<?> adminSchema = new BooleanSchema().description("User is admin");

    // method call
    final PList<Pojo> pojos =
        pojoMapper.fromSchemas(
            PList.of(
                new OpenApiPojo(PojoName.ofNameAndSuffix("UserAdmin", "Dto"), adminSchema),
                new OpenApiPojo(PojoName.ofNameAndSuffix("User", "Dto"), userSchema)));

    assertEquals(1, pojos.size());
    assertEquals(
        ObjectPojo.of(
            PojoName.ofNameAndSuffix("User", "Dto"),
            "",
            PList.single(
                new PojoMember(
                    Name.ofString("admin"),
                    "User is admin",
                    BooleanType.create(),
                    OPTIONAL,
                    NOT_NULLABLE))),
        pojos.apply(0));
  }

  @Test
  void fromSchemas_when_rootEnumSchemaUsedAsReference_then_discreteEnumPojoCreated() {
    final PojoMapper pojoMapper = PojoMapperImpl.create();

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
                new OpenApiPojo(PojoName.ofNameAndSuffix("Gender", "Dto"), genderSchema),
                new OpenApiPojo(PojoName.ofNameAndSuffix("User", "Dto"), userSchema)));

    assertEquals(2, pojos.size());
    assertEquals(
        EnumPojo.of(
            PojoName.ofNameAndSuffix("Gender", "Dto"),
            "Gender of a user",
            PList.of("FEMALE", "MALE", "UNKNOWN")),
        pojos.apply(0));
    assertEquals(
        ObjectPojo.of(
            PojoName.ofNameAndSuffix("User", "Dto"),
            "",
            PList.single(
                new PojoMember(
                    Name.ofString("gender"),
                    "Gender of a user",
                    ObjectType.ofName(PojoName.ofNameAndSuffix("Gender", "Dto")),
                    OPTIONAL,
                    NOT_NULLABLE))),
        pojos.apply(1));
  }

  @Test
  void fromSchemas_when_lowercaseNamesAndReferences_then_allNamesStartUppercase() {
    final PojoMapper pojoMapper = PojoMapperImpl.create();

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
                new OpenApiPojo(PojoName.ofNameAndSuffix("gender", "Dto"), genderSchema),
                new OpenApiPojo(PojoName.ofNameAndSuffix("user", "Dto"), userSchema)));

    assertEquals(2, pojos.size());
    assertEquals(PojoName.ofNameAndSuffix("Gender", "Dto"), pojos.apply(0).getName());
    assertEquals(PojoName.ofNameAndSuffix("User", "Dto"), pojos.apply(1).getName());
    assertEquals(
        Optional.of(PojoName.ofNameAndSuffix("Gender", "Dto")),
        pojos
            .apply(1)
            .asObjectPojo()
            .map(ObjectPojo::getMembers)
            .flatMap(PList::headOption)
            .map(PojoMember::getType)
            .flatMap(Type::asObjectType)
            .map(ObjectType::getName));
  }

  private static PList<Map.Entry<String, Schema>> parseOpenApiResourceEntries(String resource) {
    final SwaggerParseResult swaggerParseResult =
        new OpenAPIV3Parser().readContents(Resources.readString(resource));
    final OpenAPI openAPI = swaggerParseResult.getOpenAPI();
    return PList.fromIter(openAPI.getComponents().getSchemas().entrySet());
  }
}
