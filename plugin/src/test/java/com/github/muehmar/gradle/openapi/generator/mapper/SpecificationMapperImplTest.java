package com.github.muehmar.gradle.openapi.generator.mapper;

import static com.github.muehmar.gradle.openapi.generator.model.AdditionalProperties.anyTypeAllowed;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.REQUIRED;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.name.ComponentNames.componentName;
import static com.github.muehmar.gradle.openapi.generator.model.name.PojoNames.pojoName;
import static com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojoBuilder.objectPojoBuilder;
import static com.github.muehmar.gradle.openapi.generator.model.type.StringType.Format.DATE;
import static com.github.muehmar.gradle.openapi.generator.model.type.StringType.Format.DATE_TIME;
import static com.github.muehmar.gradle.openapi.generator.model.type.StringType.Format.EMAIL;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.resolver.MapResultResolverImpl;
import com.github.muehmar.gradle.openapi.generator.model.Parameter;
import com.github.muehmar.gradle.openapi.generator.model.ParameterSchema;
import com.github.muehmar.gradle.openapi.generator.model.ParsedSpecifications;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.PropertyScope;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.composition.AllOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.DecimalMax;
import com.github.muehmar.gradle.openapi.generator.model.constraints.DecimalMin;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Max;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Min;
import com.github.muehmar.gradle.openapi.generator.model.constraints.PropertyCount;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ArrayPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.EnumPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojoBuilder;
import com.github.muehmar.gradle.openapi.generator.model.specification.MainDirectory;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import com.github.muehmar.gradle.openapi.generator.model.type.AnyType;
import com.github.muehmar.gradle.openapi.generator.model.type.ArrayType;
import com.github.muehmar.gradle.openapi.generator.model.type.BooleanType;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
import com.github.muehmar.gradle.openapi.generator.model.type.IntegerType;
import com.github.muehmar.gradle.openapi.generator.model.type.MapType;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StandardObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.ExcludedSchemas;
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
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class SpecificationMapperImplTest {

  @Test
  void map_when_arraySchema_then_returnArrayPojo() {
    final ArraySchema schema = new ArraySchema().items(new IntegerSchema());
    schema.setMaxItems(50);

    // method call
    final PojoSchema pojoSchema = new PojoSchema(componentName("PojoName", "Dto"), schema);
    final SpecificationMapper specificationMapper =
        SpecificationMapperImpl.create(
            new MapResultResolverImpl(),
            (mainDir, spec) -> ParsedSpecifications.fromPojoSchemas(pojoSchema));
    final PList<Pojo> pojos =
        specificationMapper
            .map(MainDirectory.fromString(""), OpenApiSpec.fromString("doesNotMatter"))
            .getPojos();

    assertEquals(1, pojos.size());
    final Pojo pojo = pojos.head();
    assertEquals(
        ArrayPojo.of(
            componentName("PojoName", "Dto"),
            "",
            NOT_NULLABLE,
            IntegerType.formatInteger(),
            Constraints.ofSize(Size.ofMax(50))),
        pojo);
  }

  @Test
  void map_when_parameterSchema_then_returnMappedParameter() {
    final IntegerSchema schema = new IntegerSchema();
    schema.setMinimum(new BigDecimal(0L));
    schema.setMaximum(new BigDecimal(1000L));
    schema.setDefault(50L);
    final ParameterSchema parameterSchema =
        new ParameterSchema(Name.ofString("limitParam"), schema);

    // method call
    final SpecificationMapper specificationMapper =
        SpecificationMapperImpl.create(
            new MapResultResolverImpl(),
            (mainDir, spec) -> ParsedSpecifications.fromParameterSchemas(parameterSchema));

    final PList<Parameter> parameters =
        specificationMapper
            .map(MainDirectory.fromString(""), OpenApiSpec.fromString("doesNotMatter"))
            .getParameters();

    assertEquals(1, parameters.size());
    final Parameter parameter = parameters.head();

    final Parameter expectedParameter =
        new Parameter(
            parameterSchema.getName(),
            IntegerType.formatInteger()
                .withConstraints(Constraints.ofMin(new Min(0L)).withMax(new Max(1000L))),
            Optional.of(50));
    assertEquals(expectedParameter, parameter);
  }

  @Test
  void map_when_classMappedType_then_correctMappedTypePojo() {
    final HashMap<String, Schema> properties = new HashMap<>();
    properties.put("name", new StringSchema());
    final Schema<?> schema = new ObjectSchema().properties(properties);

    // method call
    final PojoSchema pojoSchema = new PojoSchema(componentName("PojoName", "Dto"), schema);

    final SpecificationMapper specificationMapper =
        SpecificationMapperImpl.create(
            new MapResultResolverImpl(),
            (mainDir, spec) -> ParsedSpecifications.fromPojoSchemas(pojoSchema));
    final PList<Pojo> pojos =
        specificationMapper
            .map(MainDirectory.fromString(""), OpenApiSpec.fromString("doesNotMatter"))
            .getPojos();

    assertEquals(1, pojos.size());
    final Pojo pojo = pojos.head();
    assertEquals(
        ObjectPojoBuilder.create()
            .name(componentName("PojoName", "Dto"))
            .description("")
            .nullability(NOT_NULLABLE)
            .members(
                PList.single(
                    new PojoMember(
                        Name.ofString("name"),
                        "",
                        StringType.ofFormat(StringType.Format.NONE),
                        PropertyScope.DEFAULT,
                        OPTIONAL)))
            .requiredAdditionalProperties(PList.empty())
            .constraints(Constraints.empty())
            .additionalProperties(anyTypeAllowed())
            .build(),
        pojo);
  }

  @Test
  void map_when_realSpecWithRemoteReference_then_allPojosCorrectMapped() {
    final PList<Pojo> pojos =
        ResourceSchemaMappingTestUtil.mapSchema("/specifications/remote-ref", "main.yml");

    assertEquals(2, pojos.size());
    assertEquals(
        PList.of("CityDto", "UserDto"),
        pojos.map(Pojo::getName).map(ComponentName::getPojoName).map(PojoName::asString));
  }

  @Test
  void map_when_calledWithRealOpenApiSchemas_then_allPojosCorrectMapped() {
    final PList<Pojo> pojos =
        ResourceSchemaMappingTestUtil.mapSchema("/integration/completespec", "openapi.yml");

    assertEquals(7, pojos.size());

    assertEquals(
        ObjectPojoBuilder.create()
            .name(componentName("Language", "Dto"))
            .description("")
            .nullability(NOT_NULLABLE)
            .members(
                PList.of(
                    new PojoMember(
                        Name.ofString("key"),
                        "",
                        IntegerType.formatInteger(),
                        PropertyScope.DEFAULT,
                        REQUIRED),
                    new PojoMember(
                        Name.ofString("name"),
                        "",
                        StringType.noFormat(),
                        PropertyScope.DEFAULT,
                        REQUIRED)))
            .requiredAdditionalProperties(PList.empty())
            .constraints(Constraints.empty())
            .additionalProperties(anyTypeAllowed())
            .build(),
        pojos.apply(0));

    assertEquals(
        ObjectPojoBuilder.create()
            .name(componentName("User", "Dto"))
            .description("")
            .nullability(NOT_NULLABLE)
            .members(
                PList.of(
                    new PojoMember(
                        Name.ofString("id"),
                        "",
                        StringType.uuid(),
                        PropertyScope.DEFAULT,
                        REQUIRED),
                    new PojoMember(
                        Name.ofString("externalId"),
                        "",
                        IntegerType.formatLong(),
                        PropertyScope.DEFAULT,
                        REQUIRED),
                    new PojoMember(
                        Name.ofString("user"),
                        "",
                        StringType.noFormat(),
                        PropertyScope.DEFAULT,
                        REQUIRED),
                    new PojoMember(
                        Name.ofString("birthday"),
                        "",
                        StringType.ofFormat(DATE),
                        PropertyScope.DEFAULT,
                        OPTIONAL),
                    new PojoMember(
                        Name.ofString("email"),
                        "",
                        StringType.ofFormat(EMAIL).withConstraints(Constraints.ofEmail()),
                        PropertyScope.DEFAULT,
                        OPTIONAL),
                    new PojoMember(
                        Name.ofString("city"),
                        "",
                        StringType.noFormat(),
                        PropertyScope.DEFAULT,
                        REQUIRED),
                    new PojoMember(
                        Name.ofString("age"),
                        "",
                        IntegerType.formatInteger()
                            .withConstraints(Constraints.ofMin(new Min(18)).withMax(new Max(50))),
                        PropertyScope.DEFAULT,
                        OPTIONAL),
                    new PojoMember(
                        Name.ofString("height"),
                        "",
                        NumericType.formatFloat()
                            .withConstraints(
                                Constraints.ofDecimalMinAndMax(
                                    new DecimalMin("120", false), new DecimalMax("199", true))),
                        PropertyScope.DEFAULT,
                        OPTIONAL),
                    new PojoMember(
                        Name.ofString("lastLogin"),
                        "",
                        StringType.ofFormat(DATE_TIME),
                        PropertyScope.DEFAULT,
                        OPTIONAL),
                    new PojoMember(
                        Name.ofString("role"),
                        "",
                        EnumType.ofNameAndMembers(
                            Name.ofString("RoleEnum"), PList.of("Admin", "User", "Visitor")),
                        PropertyScope.DEFAULT,
                        OPTIONAL),
                    new PojoMember(
                        Name.ofString("currencies"),
                        "",
                        MapType.ofKeyAndValueType(StringType.noFormat(), StringType.noFormat()),
                        PropertyScope.DEFAULT,
                        OPTIONAL),
                    new PojoMember(
                        Name.ofString("interests"),
                        "",
                        MapType.ofKeyAndValueType(
                            StringType.noFormat(),
                            StandardObjectType.ofName(pojoName("UserInterests", "Dto"))),
                        PropertyScope.DEFAULT,
                        OPTIONAL),
                    new PojoMember(
                        Name.ofString("languages"),
                        "",
                        MapType.ofKeyAndValueType(
                            StringType.noFormat(),
                            StandardObjectType.ofName(PojoName.ofNameAndSuffix("Language", "Dto"))),
                        PropertyScope.DEFAULT,
                        OPTIONAL),
                    new PojoMember(
                        Name.ofString("hobbies"),
                        "",
                        MapType.ofKeyAndValueType(
                            StringType.noFormat(),
                            StandardObjectType.ofName(pojoName("UserHobbies", "Dto"))),
                        PropertyScope.DEFAULT,
                        OPTIONAL),
                    new PojoMember(
                        Name.ofString("data"),
                        "Some user related data",
                        AnyType.create(NULLABLE),
                        PropertyScope.DEFAULT,
                        OPTIONAL)))
            .requiredAdditionalProperties(PList.empty())
            .constraints(Constraints.empty())
            .additionalProperties(anyTypeAllowed())
            .build(),
        pojos.apply(1));

    assertEquals(
        ObjectPojoBuilder.create()
            .name(componentName("UserGroup", "Dto"))
            .description("")
            .nullability(NOT_NULLABLE)
            .members(
                PList.of(
                    new PojoMember(
                        Name.ofString("owner"),
                        "",
                        StandardObjectType.ofName(PojoName.ofNameAndSuffix("User", "Dto")),
                        PropertyScope.DEFAULT,
                        OPTIONAL),
                    new PojoMember(
                        Name.ofString("members"),
                        "",
                        ArrayType.ofItemType(
                            StandardObjectType.ofName(PojoName.ofNameAndSuffix("User", "Dto")),
                            NOT_NULLABLE),
                        PropertyScope.DEFAULT,
                        OPTIONAL),
                    new PojoMember(
                        Name.ofString("languages"),
                        "",
                        ArrayType.ofItemType(
                            StandardObjectType.ofName(pojoName("UserGroupLanguages", "Dto")),
                            NOT_NULLABLE),
                        PropertyScope.DEFAULT,
                        OPTIONAL)))
            .requiredAdditionalProperties(PList.empty())
            .constraints(Constraints.ofPropertiesCount(PropertyCount.ofMinAndMaxProperties(1, 3)))
            .additionalProperties(anyTypeAllowed())
            .build(),
        pojos.apply(2));

    assertEquals(
        ObjectPojoBuilder.create()
            .name(
                componentName("UserGroup", "Dto")
                    .deriveMemberSchemaName(Name.ofString("languages")))
            .description("")
            .nullability(NOT_NULLABLE)
            .members(
                PList.of(
                    new PojoMember(
                        Name.ofString("id"),
                        "",
                        StringType.noFormat(),
                        PropertyScope.DEFAULT,
                        OPTIONAL),
                    new PojoMember(
                        Name.ofString("name"),
                        "",
                        StringType.noFormat(),
                        PropertyScope.DEFAULT,
                        OPTIONAL)))
            .requiredAdditionalProperties(PList.empty())
            .constraints(Constraints.empty())
            .additionalProperties(anyTypeAllowed())
            .build(),
        pojos.apply(3));

    assertEquals(
        ObjectPojoBuilder.create()
            .name(componentName("User", "Dto").deriveMemberSchemaName(Name.ofString("hobbies")))
            .description("")
            .nullability(NOT_NULLABLE)
            .members(
                PList.of(
                    new PojoMember(
                        Name.ofString("name"),
                        "",
                        StringType.noFormat(),
                        PropertyScope.DEFAULT,
                        REQUIRED),
                    new PojoMember(
                        Name.ofString("description"),
                        "",
                        StringType.noFormat(),
                        PropertyScope.DEFAULT,
                        OPTIONAL)))
            .requiredAdditionalProperties(PList.empty())
            .constraints(Constraints.empty())
            .additionalProperties(anyTypeAllowed())
            .build(),
        pojos.apply(4));

    assertEquals(
        ArrayPojo.of(
            componentName("User", "Dto").deriveMemberSchemaName(Name.ofString("interests")),
            "",
            NOT_NULLABLE,
            StandardObjectType.ofName(pojoName("UserInterestsValue", "Dto")),
            Constraints.empty()),
        pojos.apply(5));

    assertEquals(
        ObjectPojoBuilder.create()
            .name(
                componentName("User", "Dto")
                    .deriveMemberSchemaName(Name.ofString("interests"))
                    .deriveMemberSchemaName(Name.ofString("value")))
            .description("")
            .nullability(NOT_NULLABLE)
            .members(
                PList.of(
                    new PojoMember(
                        Name.ofString("name"),
                        "",
                        StringType.noFormat(),
                        PropertyScope.DEFAULT,
                        OPTIONAL),
                    new PojoMember(
                        Name.ofString("prio"),
                        "",
                        IntegerType.formatInteger(),
                        PropertyScope.DEFAULT,
                        OPTIONAL)))
            .requiredAdditionalProperties(PList.empty())
            .constraints(Constraints.empty())
            .additionalProperties(anyTypeAllowed())
            .build(),
        pojos.apply(6));
  }

  @Test
  void map_when_singleInlineDefinition_then_composedPojoAndInlineDefinitionPojoCreated() {
    final Schema<?> objectSchema =
        new ObjectSchema()
            .addProperties("user", new StringSchema())
            .addProperties("key", new IntegerSchema());

    final ComposedSchema composedSchema = new ComposedSchema();
    composedSchema.addAllOfItem(objectSchema);

    // method call
    final PojoSchema pojoSchema =
        new PojoSchema(componentName("ComposedPojoName", "Dto"), composedSchema);
    final SpecificationMapper specificationMapper =
        SpecificationMapperImpl.create(
            new MapResultResolverImpl(),
            (mainDir, spec) -> ParsedSpecifications.fromPojoSchemas(pojoSchema));
    final PList<Pojo> pojos =
        specificationMapper
            .map(MainDirectory.fromString(""), OpenApiSpec.fromString("doesNotMatter"))
            .getPojos()
            .sort(Comparator.comparing(pojo -> pojo.getName().getPojoName().asString()));

    assertEquals(2, pojos.size());

    final ObjectPojo expectedAllOfPojo =
        ObjectPojoBuilder.create()
            .name(componentName("ComposedPojoNameAllOf", "Dto"))
            .description("")
            .nullability(NOT_NULLABLE)
            .members(
                PList.of(
                    new PojoMember(
                        Name.ofString("user"),
                        "",
                        StringType.noFormat(),
                        PropertyScope.DEFAULT,
                        OPTIONAL),
                    new PojoMember(
                        Name.ofString("key"),
                        "",
                        IntegerType.formatInteger(),
                        PropertyScope.DEFAULT,
                        OPTIONAL)))
            .requiredAdditionalProperties(PList.empty())
            .constraints(Constraints.empty())
            .additionalProperties(anyTypeAllowed())
            .build();

    assertEquals(expectedAllOfPojo, pojos.apply(0));

    assertEquals(
        ObjectPojoBuilder.create()
            .name(componentName("ComposedPojoName", "Dto"))
            .description("")
            .nullability(NOT_NULLABLE)
            .members(PList.empty())
            .requiredAdditionalProperties(PList.empty())
            .constraints(Constraints.empty())
            .additionalProperties(anyTypeAllowed())
            .andOptionals()
            .allOfComposition(AllOfComposition.fromPojos(NonEmptyList.of(expectedAllOfPojo)))
            .build(),
        pojos.apply(1));
  }

  @Test
  void map_when_twoInlineDefinitionAndReference_then_allPojosCreated() {
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
    final PList<PojoSchema> pojoSchemas =
        PList.of(
            new PojoSchema(componentName("ComposedPojoName", "Dto"), composedSchema),
            new PojoSchema(componentName("ReferenceSchema", "Dto"), referenceSchema));

    final SpecificationMapper specificationMapper =
        SpecificationMapperImpl.create(
            new MapResultResolverImpl(),
            (mainDir, spec) -> ParsedSpecifications.fromPojoSchemas(pojoSchemas));
    final PList<Pojo> pojos =
        specificationMapper
            .map(MainDirectory.fromString(""), OpenApiSpec.fromString("doesNotMatter"))
            .getPojos()
            .sort(Comparator.comparing(pojo -> pojo.getName().getPojoName().asString()));

    assertEquals(4, pojos.size());

    final ObjectPojo expectedAllOf0Pojo =
        ObjectPojoBuilder.create()
            .name(componentName("ComposedPojoNameAllOf0", "Dto"))
            .description("")
            .nullability(NOT_NULLABLE)
            .members(
                PList.of(
                    new PojoMember(
                        Name.ofString("user"),
                        "",
                        StringType.noFormat(),
                        PropertyScope.DEFAULT,
                        OPTIONAL),
                    new PojoMember(
                        Name.ofString("key"),
                        "",
                        IntegerType.formatInteger(),
                        PropertyScope.DEFAULT,
                        OPTIONAL)))
            .requiredAdditionalProperties(PList.empty())
            .constraints(Constraints.empty())
            .additionalProperties(anyTypeAllowed())
            .build();
    assertEquals(expectedAllOf0Pojo, pojos.apply(0));

    final ObjectPojo expectedAllOf1Pojo =
        ObjectPojoBuilder.create()
            .name(componentName("ComposedPojoNameAllOf1", "Dto"))
            .description("")
            .nullability(NOT_NULLABLE)
            .members(
                PList.of(
                    new PojoMember(
                        Name.ofString("registerDate"),
                        "",
                        StringType.ofFormat(DATE),
                        PropertyScope.DEFAULT,
                        OPTIONAL),
                    new PojoMember(
                        Name.ofString("languages"),
                        "",
                        ArrayType.ofItemType(StringType.noFormat(), NOT_NULLABLE),
                        PropertyScope.DEFAULT,
                        OPTIONAL)))
            .requiredAdditionalProperties(PList.empty())
            .constraints(Constraints.empty())
            .additionalProperties(anyTypeAllowed())
            .build();
    assertEquals(expectedAllOf1Pojo, pojos.apply(1));

    final ObjectPojo expectedReferencePojo =
        ObjectPojoBuilder.create()
            .name(componentName("ReferenceSchema", "Dto"))
            .description("")
            .nullability(NOT_NULLABLE)
            .members(
                PList.of(
                    new PojoMember(
                        Name.ofString("color"),
                        "",
                        StringType.noFormat(),
                        PropertyScope.DEFAULT,
                        OPTIONAL),
                    new PojoMember(
                        Name.ofString("group"),
                        "",
                        IntegerType.formatInteger(),
                        PropertyScope.DEFAULT,
                        OPTIONAL)))
            .requiredAdditionalProperties(PList.empty())
            .constraints(Constraints.empty())
            .additionalProperties(anyTypeAllowed())
            .build();
    assertEquals(
        ObjectPojoBuilder.create()
            .name(componentName("ComposedPojoName", "Dto"))
            .description("")
            .nullability(NOT_NULLABLE)
            .members(PList.empty())
            .requiredAdditionalProperties(PList.empty())
            .constraints(Constraints.empty())
            .additionalProperties(anyTypeAllowed())
            .andOptionals()
            .allOfComposition(
                AllOfComposition.fromPojos(
                    NonEmptyList.of(expectedReferencePojo, expectedAllOf0Pojo, expectedAllOf1Pojo)))
            .build(),
        pojos.apply(2));

    assertEquals(expectedReferencePojo, pojos.apply(3));
  }

  @Test
  void map_when_rootUuidSchemaUsedAsReference_then_inlinedInPojo() {
    final Schema<?> userSchema =
        new ObjectSchema()
            .addProperties("key", new Schema<>().$ref("#/components/schemas/UserKey"));
    final Schema<?> keySchema = new UUIDSchema().description("User key");

    // method call
    final PList<PojoSchema> pojoSchemas =
        PList.of(
            new PojoSchema(componentName("UserKey", "Dto"), keySchema),
            new PojoSchema(componentName("User", "Dto"), userSchema));
    final SpecificationMapper specificationMapper =
        SpecificationMapperImpl.create(
            new MapResultResolverImpl(),
            (mainDir, spec) -> ParsedSpecifications.fromPojoSchemas(pojoSchemas));
    final PList<Pojo> pojos =
        specificationMapper
            .map(MainDirectory.fromString(""), OpenApiSpec.fromString("doesNotMatter"))
            .getPojos();

    assertEquals(1, pojos.size());
    assertEquals(
        ObjectPojoBuilder.create()
            .name(componentName("User", "Dto"))
            .description("")
            .nullability(NOT_NULLABLE)
            .members(
                PList.single(
                    new PojoMember(
                        Name.ofString("key"),
                        "User key",
                        StringType.uuid(),
                        PropertyScope.DEFAULT,
                        OPTIONAL)))
            .requiredAdditionalProperties(PList.empty())
            .constraints(Constraints.empty())
            .additionalProperties(anyTypeAllowed())
            .build(),
        pojos.apply(0));
  }

  @Test
  void map_when_rootIntegerSchemaUsedAsReference_then_inlinedInPojo() {
    final Schema<?> userSchema =
        new ObjectSchema()
            .addProperties("age", new Schema<>().$ref("#/components/schemas/UserAge"));
    final Schema<?> ageSchema = new IntegerSchema().description("User age");

    // method call
    final PList<PojoSchema> pojoSchemas =
        PList.of(
            new PojoSchema(componentName("UserAge", "Dto"), ageSchema),
            new PojoSchema(componentName("User", "Dto"), userSchema));

    final SpecificationMapper specificationMapper =
        SpecificationMapperImpl.create(
            new MapResultResolverImpl(),
            (mainDir, spec) -> ParsedSpecifications.fromPojoSchemas(pojoSchemas));
    final PList<Pojo> pojos =
        specificationMapper
            .map(MainDirectory.fromString(""), OpenApiSpec.fromString("doesNotMatter"))
            .getPojos();

    assertEquals(1, pojos.size());
    assertEquals(
        ObjectPojoBuilder.create()
            .name(componentName("User", "Dto"))
            .description("")
            .nullability(NOT_NULLABLE)
            .members(
                PList.single(
                    new PojoMember(
                        Name.ofString("age"),
                        "User age",
                        IntegerType.formatInteger(),
                        PropertyScope.DEFAULT,
                        OPTIONAL)))
            .requiredAdditionalProperties(PList.empty())
            .constraints(Constraints.empty())
            .additionalProperties(anyTypeAllowed())
            .build(),
        pojos.apply(0));
  }

  @Test
  void map_when_rootNumberSchemaUsedAsReference_then_inlinedInPojo() {
    final Schema<?> userSchema =
        new ObjectSchema()
            .addProperties("height", new Schema<>().$ref("#/components/schemas/UserHeight"));
    final Schema<?> heightSchema = new NumberSchema().description("User height");

    // method call
    final PList<PojoSchema> pojoSchemas =
        PList.of(
            new PojoSchema(componentName("UserHeight", "Dto"), heightSchema),
            new PojoSchema(componentName("User", "Dto"), userSchema));

    final SpecificationMapper specificationMapper =
        SpecificationMapperImpl.create(
            new MapResultResolverImpl(),
            (mainDir, spec) -> ParsedSpecifications.fromPojoSchemas(pojoSchemas));
    final PList<Pojo> pojos =
        specificationMapper
            .map(MainDirectory.fromString(""), OpenApiSpec.fromString("doesNotMatter"))
            .getPojos();

    assertEquals(1, pojos.size());
    assertEquals(
        ObjectPojoBuilder.create()
            .name(componentName("User", "Dto"))
            .description("")
            .nullability(NOT_NULLABLE)
            .members(
                PList.single(
                    new PojoMember(
                        Name.ofString("height"),
                        "User height",
                        NumericType.formatFloat(),
                        PropertyScope.DEFAULT,
                        OPTIONAL)))
            .requiredAdditionalProperties(PList.empty())
            .constraints(Constraints.empty())
            .additionalProperties(anyTypeAllowed())
            .build(),
        pojos.apply(0));
  }

  @Test
  void map_when_rootBooleanSchemaUsedAsReference_then_inlinedInPojo() {
    final Schema<?> userSchema =
        new ObjectSchema()
            .addProperties("admin", new Schema<>().$ref("#/components/schemas/UserAdmin"));
    final Schema<?> adminSchema = new BooleanSchema().description("User is admin");

    // method call
    final PList<PojoSchema> pojoSchemas =
        PList.of(
            new PojoSchema(componentName("UserAdmin", "Dto"), adminSchema),
            new PojoSchema(componentName("User", "Dto"), userSchema));

    final SpecificationMapper specificationMapper =
        SpecificationMapperImpl.create(
            new MapResultResolverImpl(),
            (mainDir, spec) -> ParsedSpecifications.fromPojoSchemas(pojoSchemas));
    final PList<Pojo> pojos =
        specificationMapper
            .map(MainDirectory.fromString(""), OpenApiSpec.fromString("doesNotMatter"))
            .getPojos();

    assertEquals(1, pojos.size());
    assertEquals(
        ObjectPojoBuilder.create()
            .name(componentName("User", "Dto"))
            .description("")
            .nullability(NOT_NULLABLE)
            .members(
                PList.single(
                    new PojoMember(
                        Name.ofString("admin"),
                        "User is admin",
                        BooleanType.create(NOT_NULLABLE),
                        PropertyScope.DEFAULT,
                        OPTIONAL)))
            .requiredAdditionalProperties(PList.empty())
            .constraints(Constraints.empty())
            .additionalProperties(anyTypeAllowed())
            .build(),
        pojos.apply(0));
  }

  @Test
  void map_when_rootEnumSchemaUsedAsReference_then_discreteEnumPojoCreated() {
    final Schema<?> userSchema =
        new ObjectSchema()
            .addProperties("gender", new Schema<>().$ref("#/components/schemas/Gender"));
    final Schema<String> genderSchema = new StringSchema();
    genderSchema.setEnum(Arrays.asList("FEMALE", "MALE", "UNKNOWN"));
    genderSchema.description("Gender of a user");

    // method call
    final PList<PojoSchema> pojoSchemas =
        PList.of(
            new PojoSchema(componentName("Gender", "Dto"), genderSchema),
            new PojoSchema(componentName("User", "Dto"), userSchema));

    final SpecificationMapper specificationMapper =
        SpecificationMapperImpl.create(
            new MapResultResolverImpl(),
            (mainDir, spec) -> ParsedSpecifications.fromPojoSchemas(pojoSchemas));
    final PList<Pojo> pojos =
        specificationMapper
            .map(MainDirectory.fromString(""), OpenApiSpec.fromString("doesNotMatter"))
            .getPojos()
            .sort(Comparator.comparing(pojo -> pojo.getName().getPojoName().asString()));

    assertEquals(2, pojos.size());
    final EnumPojo expectedEnumPojo =
        EnumPojo.of(
            componentName("Gender", "Dto"),
            "Gender of a user",
            PList.of("FEMALE", "MALE", "UNKNOWN"));

    assertEquals(expectedEnumPojo, pojos.apply(0));
    assertEquals(
        ObjectPojoBuilder.create()
            .name(componentName("User", "Dto"))
            .description("")
            .nullability(NOT_NULLABLE)
            .members(
                PList.single(
                    new PojoMember(
                        Name.ofString("gender"),
                        expectedEnumPojo.getDescription(),
                        EnumObjectType.ofEnumPojo(expectedEnumPojo),
                        PropertyScope.DEFAULT,
                        OPTIONAL)))
            .requiredAdditionalProperties(PList.empty())
            .constraints(Constraints.empty())
            .additionalProperties(anyTypeAllowed())
            .build(),
        pojos.apply(1));
  }

  @Test
  void map_when_lowercaseNamesAndReferences_then_allNamesStartUppercase() {
    final Schema<?> userSchema =
        new ObjectSchema()
            .addProperties("gender", new Schema<>().$ref("#/components/schemas/gender"));
    final Schema<String> genderSchema = new StringSchema();
    genderSchema.setEnum(Arrays.asList("FEMALE", "MALE", "UNKNOWN"));
    genderSchema.description("Gender of a user");

    // method call
    final PList<PojoSchema> pojoSchemas =
        PList.of(
            new PojoSchema(componentName("gender", "Dto"), genderSchema),
            new PojoSchema(componentName("user", "Dto"), userSchema));

    final SpecificationMapper specificationMapper =
        SpecificationMapperImpl.create(
            new MapResultResolverImpl(),
            (mainDir, spec) -> ParsedSpecifications.fromPojoSchemas(pojoSchemas));
    final PList<Pojo> pojos =
        specificationMapper
            .map(MainDirectory.fromString(""), OpenApiSpec.fromString("doesNotMatter"))
            .getPojos()
            .sort(Comparator.comparing(pojo -> pojo.getName().getPojoName().asString()));

    assertEquals(2, pojos.size());
    assertEquals(pojoName("Gender", "Dto"), pojos.apply(0).getName().getPojoName());
    assertEquals(pojoName("User", "Dto"), pojos.apply(1).getName().getPojoName());
    assertEquals(
        Optional.of(pojoName("Gender", "Dto")),
        pojos
            .apply(1)
            .asObjectPojo()
            .map(ObjectPojo::getMembers)
            .flatMap(PList::headOption)
            .map(PojoMember::getType)
            .flatMap(Type::asObjectType)
            .map(ObjectType::getName));
  }

  @Test
  void map_when_excludeSchemas_then_excludedSchemaNotMapped() {
    final Schema<?> userSchema =
        new ObjectSchema()
            .addProperty("gender", new Schema<>().$ref("#/components/schemas/gender"));
    final Schema<String> genderSchema = new StringSchema();
    genderSchema.setEnum(Arrays.asList("FEMALE", "MALE", "UNKNOWN"));
    genderSchema.description("Gender of a user");

    final PList<PojoSchema> pojoSchemas =
        PList.of(
            new PojoSchema(componentName("gender", "Dto"), genderSchema),
            new PojoSchema(componentName("user", "Dto"), userSchema));

    final SpecificationMapper specificationMapper =
        SpecificationMapperImpl.create(
            new MapResultResolverImpl(),
            (mainDir, spec) -> ParsedSpecifications.fromPojoSchemas(pojoSchemas));

    // method call
    final PList<Pojo> pojos =
        specificationMapper
            .map(
                MainDirectory.fromString(""),
                OpenApiSpec.fromString("doesNotMatter"),
                ExcludedSchemas.fromExcludedPojoNames(PList.single(Name.ofString("User"))))
            .getPojos()
            .sort(Comparator.comparing(pojo -> pojo.getName().getPojoName().asString()));

    assertEquals(1, pojos.size());
    assertEquals(pojoName("Gender", "Dto"), pojos.apply(0).getName().getPojoName());
  }

  @Test
  void map_when_nullableRootSchema_then_pojoMemberNullable() {
    final Schema<?> userSchema = new ObjectSchema();
    userSchema.addProperty("address", new Schema<>().$ref("#/components/schemas/Address"));
    final ObjectSchema addressSchema = new ObjectSchema();
    addressSchema.addProperty("street", new StringSchema());
    addressSchema.setNullable(true);

    final PList<PojoSchema> pojoSchemas =
        PList.of(
            new PojoSchema(componentName("User", "Dto"), userSchema),
            new PojoSchema(componentName("Address", "Dto"), addressSchema));

    final SpecificationMapper specificationMapper =
        SpecificationMapperImpl.create(
            new MapResultResolverImpl(),
            (mainDir, spec) -> ParsedSpecifications.fromPojoSchemas(pojoSchemas));

    // method call
    final PList<Pojo> pojos =
        specificationMapper
            .map(MainDirectory.fromString(""), OpenApiSpec.fromString("doesNotMatter"))
            .getPojos()
            .sort(Comparator.comparing(pojo -> pojo.getName().getPojoName().asString()));

    assertEquals(2, pojos.size());

    final ObjectPojo expectedUserPojo =
        objectPojoBuilder()
            .name(componentName("Address", "Dto"))
            .description("")
            .nullability(NULLABLE)
            .members(
                PList.single(
                    new PojoMember(
                        Name.ofString("street"),
                        "",
                        StringType.noFormat(),
                        PropertyScope.DEFAULT,
                        OPTIONAL)))
            .requiredAdditionalProperties(PList.empty())
            .constraints(Constraints.empty())
            .additionalProperties(anyTypeAllowed())
            .build();
    assertEquals(expectedUserPojo, pojos.apply(0));

    final ObjectPojo expectedAddressPojo =
        objectPojoBuilder()
            .name(componentName("User", "Dto"))
            .description("")
            .nullability(NOT_NULLABLE)
            .members(
                PList.single(
                    new PojoMember(
                        Name.ofString("address"),
                        "",
                        StandardObjectType.ofName(pojoName("Address", "Dto"))
                            .withNullability(NULLABLE),
                        PropertyScope.DEFAULT,
                        OPTIONAL)))
            .requiredAdditionalProperties(PList.empty())
            .constraints(Constraints.empty())
            .additionalProperties(anyTypeAllowed())
            .build();
    assertEquals(expectedAddressPojo, pojos.apply(1));
  }
}
