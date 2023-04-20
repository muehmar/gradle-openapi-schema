package com.github.muehmar.gradle.openapi.generator.model.schema;

import static com.github.muehmar.gradle.openapi.generator.model.schema.MapToMemberTypeTestUtil.mapToMemberType;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.mapper.UnresolvedMapResult;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.PropertyScope;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.type.IntegerType;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

class ObjectSchemaTest {

  @Test
  void mapToMemberType_when_uuidSchema_then_correctType() {
    final PojoName pojoName = PojoName.ofName(Name.ofString("Person"));
    final Name memberName = Name.ofString("Address");
    final Schema<?> schema = new ObjectSchema();
    final HashMap<String, Schema> properties = new HashMap<>();
    properties.put("street", new StringSchema());
    schema.setProperties(properties);

    final MemberSchemaMapResult result = mapToMemberType(pojoName, memberName, schema);

    final PojoName expectedPojoName = PojoName.deriveOpenApiPojoName(pojoName, memberName);
    assertEquals(ObjectType.ofName(expectedPojoName), result.getType());
    assertEquals(
        UnmappedItems.ofPojoSchema(new PojoSchema(expectedPojoName, schema)),
        result.getUnmappedItems());
  }

  @Test
  void mapToPojo_when_schemaWithInlineDefinitionAndReference_then_correctPojoCreated() {
    final ObjectSchema objectSchema = new ObjectSchema();
    objectSchema.setDescription("Test description");

    final HashMap<String, Schema> properties = new HashMap<>();
    properties.put("stringVal", new StringSchema());

    final ObjectSchema objectSchemaProp = new ObjectSchema();
    final HashMap<String, Schema> objectSchemaPropProperties = new HashMap<>();
    objectSchemaPropProperties.put("intVal", new io.swagger.v3.oas.models.media.IntegerSchema());
    objectSchemaProp.setProperties(objectSchemaPropProperties);

    properties.put("objectVal", objectSchemaProp);
    properties.put("refVal", new Schema<>().$ref("#/components/schemas/ReferenceSchema1"));
    objectSchema.setProperties(properties);

    final PojoName pojoName = PojoName.ofNameAndSuffix(Name.ofString("Object"), "Dto");
    final PojoSchema pojoSchema = new PojoSchema(pojoName, objectSchema);

    // method call
    final MapContext mapContext = pojoSchema.mapToPojo();

    final UnresolvedMapResult unresolvedMapResult = mapContext.getUnresolvedMapResult();
    assertEquals(1, unresolvedMapResult.getPojos().size());
    assertEquals(0, unresolvedMapResult.getUnresolvedComposedPojos().size());
    assertEquals(0, unresolvedMapResult.getPojoMemberReferences().size());

    final PojoName memberObjectPojoName = PojoName.ofNameAndSuffix("ObjectObjectVal", "Dto");

    final ObjectPojo expectedPojo =
        ObjectPojo.of(
            pojoName,
            "Test description",
            PList.of(
                new PojoMember(
                    Name.ofString("objectVal"),
                    null,
                    ObjectType.ofName(memberObjectPojoName),
                    PropertyScope.DEFAULT,
                    Necessity.OPTIONAL,
                    Nullability.NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("stringVal"),
                    null,
                    StringType.noFormat(),
                    PropertyScope.DEFAULT,
                    Necessity.OPTIONAL,
                    Nullability.NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("refVal"),
                    null,
                    ObjectType.ofName(PojoName.ofNameAndSuffix("ReferenceSchema1", "Dto")),
                    PropertyScope.DEFAULT,
                    Necessity.OPTIONAL,
                    Nullability.NOT_NULLABLE)),
            Constraints.empty());
    assertEquals(expectedPojo, unresolvedMapResult.getPojos().apply(0));
    assertEquals(
        UnmappedItems.ofPojoSchema(new PojoSchema(memberObjectPojoName, objectSchemaProp)),
        mapContext.getUnmappedItems());
  }

  @Test
  void mapToPojo_when_schemaWithRequiredAndNullable_then_correctPojoMemberCreated() {
    final ObjectSchema objectSchema = new ObjectSchema();
    objectSchema.setDescription("Test description");

    final HashMap<String, Schema> properties = new HashMap<>();
    properties.put("stringVal", new StringSchema());
    properties.put("intVal", new io.swagger.v3.oas.models.media.IntegerSchema().nullable(true));
    properties.put("numVal", new io.swagger.v3.oas.models.media.NumberSchema().nullable(true));
    objectSchema.setProperties(properties);
    objectSchema.setRequired(Arrays.asList("stringVal", "numVal"));

    final PojoName pojoName = PojoName.ofNameAndSuffix(Name.ofString("Object"), "Dto");
    final PojoSchema pojoSchema = new PojoSchema(pojoName, objectSchema);

    // method call
    final MapContext mapContext = pojoSchema.mapToPojo();

    final UnresolvedMapResult unresolvedMapResult = mapContext.getUnresolvedMapResult();
    assertEquals(1, unresolvedMapResult.getPojos().size());
    assertEquals(0, unresolvedMapResult.getUnresolvedComposedPojos().size());
    assertEquals(0, unresolvedMapResult.getPojoMemberReferences().size());

    final PList<PojoMember> expectedMembers =
        PList.of(
            new PojoMember(
                Name.ofString("intVal"),
                null,
                IntegerType.formatInteger(),
                PropertyScope.DEFAULT,
                Necessity.OPTIONAL,
                Nullability.NULLABLE),
            new PojoMember(
                Name.ofString("numVal"),
                null,
                NumericType.formatFloat(),
                PropertyScope.DEFAULT,
                Necessity.REQUIRED,
                Nullability.NULLABLE),
            new PojoMember(
                Name.ofString("stringVal"),
                null,
                StringType.noFormat(),
                PropertyScope.DEFAULT,
                Necessity.REQUIRED,
                Nullability.NOT_NULLABLE));
    assertEquals(
        expectedMembers,
        unresolvedMapResult
            .getPojos()
            .apply(0)
            .asObjectPojo()
            .map(ObjectPojo::getMembers)
            .orElse(PList.empty())
            .sort(Comparator.comparing(member -> member.getName().asString())));
    assertEquals(UnmappedItems.empty(), mapContext.getUnmappedItems());
  }

  @Test
  void mapToPojo_when_schemaWithReadOnlyAndWriteOnly_then_correctPojoMemberCreated() {
    final ObjectSchema objectSchema = new ObjectSchema();
    objectSchema.setDescription("Test description");

    final HashMap<String, Schema> properties = new HashMap<>();
    properties.put("stringVal", new StringSchema().readOnly(true));
    properties.put("intVal", new IntegerSchema().writeOnly(true));
    properties.put("numVal", new NumberSchema());
    objectSchema.setProperties(properties);

    final PojoName pojoName = PojoName.ofNameAndSuffix(Name.ofString("Object"), "Dto");
    final PojoSchema pojoSchema = new PojoSchema(pojoName, objectSchema);

    // method call
    final MapContext mapContext = pojoSchema.mapToPojo();

    final UnresolvedMapResult unresolvedMapResult = mapContext.getUnresolvedMapResult();
    assertEquals(1, unresolvedMapResult.getPojos().size());
    assertEquals(0, unresolvedMapResult.getUnresolvedComposedPojos().size());
    assertEquals(0, unresolvedMapResult.getPojoMemberReferences().size());

    final PList<PojoMember> expectedMembers =
        PList.of(
            new PojoMember(
                Name.ofString("intVal"),
                null,
                IntegerType.formatInteger(),
                PropertyScope.WRITE_ONLY,
                Necessity.OPTIONAL,
                Nullability.NOT_NULLABLE),
            new PojoMember(
                Name.ofString("numVal"),
                null,
                NumericType.formatFloat(),
                PropertyScope.DEFAULT,
                Necessity.OPTIONAL,
                Nullability.NOT_NULLABLE),
            new PojoMember(
                Name.ofString("stringVal"),
                null,
                StringType.noFormat(),
                PropertyScope.READ_ONLY,
                Necessity.OPTIONAL,
                Nullability.NOT_NULLABLE));
    assertEquals(
        expectedMembers,
        unresolvedMapResult
            .getPojos()
            .apply(0)
            .asObjectPojo()
            .map(ObjectPojo::getMembers)
            .orElse(PList.empty())
            .sort(Comparator.comparing(member -> member.getName().asString())));
    assertEquals(UnmappedItems.empty(), mapContext.getUnmappedItems());
  }
}
