package com.github.muehmar.gradle.openapi.generator.model.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.model.Discriminator;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.composition.UnresolvedAllOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.UnresolvedAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.UnresolvedOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SchemaCompositionsTest {

  @ParameterizedTest
  @MethodSource("allOfArguments")
  void getAllOf_when_allOfArguments_then_correctMapContext(
      SchemaCompositions schemaCompositions,
      PojoName pojoName,
      PList<PojoName> pojoNames,
      UnmappedItems expectedUnmappedItems) {

    // method call
    final SchemaCompositions.CompositionMapResult<UnresolvedAllOfComposition> result =
        schemaCompositions.getAllOf(pojoName);

    final UnresolvedAllOfComposition expectedComposition =
        UnresolvedAllOfComposition.fromPojoNames(pojoNames);

    assertEquals(Optional.of(expectedComposition), result.getComposition());
    assertEquals(expectedUnmappedItems, result.getUnmappedItems());
  }

  @ParameterizedTest
  @MethodSource("oneOfArguments")
  void getOneOf_when_oneOfArguments_then_correctMapContext(
      SchemaCompositions schemaCompositions,
      PojoName pojoName,
      PList<PojoName> pojoNames,
      UnmappedItems expectedUnmappedItems) {

    // method call
    final SchemaCompositions.CompositionMapResult<UnresolvedOneOfComposition> result =
        schemaCompositions.getOneOf(pojoName);

    final UnresolvedOneOfComposition expectedComposition =
        UnresolvedOneOfComposition.fromPojoNamesAndDiscriminator(pojoNames, Optional.empty());

    assertEquals(Optional.of(expectedComposition), result.getComposition());
    assertEquals(expectedUnmappedItems, result.getUnmappedItems());
  }

  @ParameterizedTest
  @MethodSource("anyOfArguments")
  void getAnyOf_when_anyOfArguments_then_correctMapContext(
      SchemaCompositions schemaCompositions,
      PojoName pojoName,
      PList<PojoName> pojoNames,
      UnmappedItems expectedUnmappedItems) {

    // method call
    final SchemaCompositions.CompositionMapResult<UnresolvedAnyOfComposition> result =
        schemaCompositions.getAnyOf(pojoName);

    final UnresolvedAnyOfComposition expectedComposition =
        UnresolvedAnyOfComposition.fromPojoNames(pojoNames);

    assertEquals(Optional.of(expectedComposition), result.getComposition());
    assertEquals(expectedUnmappedItems, result.getUnmappedItems());
  }

  @Test
  void getOneOf_when_discriminator_then_mappedCorrectly() {
    final ComposedSchema composedSchema = new ComposedSchema();
    composedSchema.addOneOfItem(new Schema<>());
    final io.swagger.v3.oas.models.media.Discriminator discriminator =
        new io.swagger.v3.oas.models.media.Discriminator();
    composedSchema.setDiscriminator(discriminator);

    discriminator.propertyName("propXY");
    discriminator.mapping("hello", "#/components/schemas/World");
    discriminator.mapping("lorem", "#/components/schemas/Ipsum");

    final SchemaCompositions schemaCompositions = SchemaCompositions.wrap(composedSchema);

    // method call
    final SchemaCompositions.CompositionMapResult<UnresolvedOneOfComposition> result =
        schemaCompositions.getOneOf(PojoName.ofNameAndSuffix("Composed", "Dto"));

    final HashMap<String, Name> mappings = new HashMap<>();
    mappings.put("hello", Name.ofString("World"));
    mappings.put("lorem", Name.ofString("Ipsum"));
    final Discriminator expectedDiscriminator =
        Discriminator.fromPropertyNameAndMapping(Name.ofString("propXY"), mappings);

    assertEquals(
        Optional.of(expectedDiscriminator),
        result.getComposition().flatMap(UnresolvedOneOfComposition::getDiscriminator));
  }

  private static Stream<Arguments> allOfArguments() {
    return arguments(ComposedSchemas.CompositionType.ALL_OF, ComposedSchema::setAllOf);
  }

  private static Stream<Arguments> oneOfArguments() {
    return arguments(ComposedSchemas.CompositionType.ONE_OF, ComposedSchema::setOneOf);
  }

  private static Stream<Arguments> anyOfArguments() {
    return arguments(ComposedSchemas.CompositionType.ANY_OF, ComposedSchema::setAnyOf);
  }

  private static Stream<Arguments> arguments(
      ComposedSchemas.CompositionType type,
      BiConsumer<ComposedSchema, List<Schema>> setCompositionsSchemas) {
    return Stream.of(
        localAndRemoteReferencesArguments(setCompositionsSchemas),
        singleInlineDefinitionArguments(type, setCompositionsSchemas),
        multipleInlineDefinitionArguments(type, setCompositionsSchemas));
  }

  private static Arguments localAndRemoteReferencesArguments(
      BiConsumer<ComposedSchema, List<Schema>> setCompositionsSchemas) {
    final ComposedSchema composedSchema = new ComposedSchema();
    composedSchema.setDescription("Test description");
    final Schema<?> remoteRefSchema =
        new Schema<>().$ref("../../dir/components.yml#/components/schemas/ReferenceSchema1");
    final Schema<?> localRefSchema = new Schema<>().$ref("#/components/schemas/ReferenceSchema2");

    final ArrayList<Schema> schemas = new ArrayList<>();
    schemas.add(remoteRefSchema);
    schemas.add(localRefSchema);
    setCompositionsSchemas.accept(composedSchema, schemas);

    final SchemaCompositions schemaCompositions = SchemaCompositions.wrap(composedSchema);

    final PojoName pojoName = PojoName.ofNameAndSuffix(Name.ofString("ComposedPojoName"), "Dto");

    final PojoName refSchema1PojoName = PojoName.ofNameAndSuffix("ReferenceSchema1", "Dto");
    final PojoName refSchema2PojoName = PojoName.ofNameAndSuffix("ReferenceSchema2", "Dto");
    final PList<PojoName> pojoNames = PList.of(refSchema1PojoName, refSchema2PojoName);

    final UnmappedItems expectedUnmappedItems =
        UnmappedItems.ofSpec(OpenApiSpec.fromString("../../dir/components.yml"));

    return Arguments.arguments(schemaCompositions, pojoName, pojoNames, expectedUnmappedItems);
  }

  private static Arguments singleInlineDefinitionArguments(
      ComposedSchemas.CompositionType type,
      BiConsumer<ComposedSchema, List<Schema>> setCompositionsSchemas) {
    final ComposedSchema composedSchema = new ComposedSchema();
    composedSchema.setDescription("Test description");

    final io.swagger.v3.oas.models.media.ObjectSchema objectSchema =
        new io.swagger.v3.oas.models.media.ObjectSchema();
    final HashMap<String, Schema> properties = new HashMap<>();
    properties.put("key", new io.swagger.v3.oas.models.media.StringSchema());
    objectSchema.setProperties(properties);
    composedSchema.setMaxProperties(5);

    final ArrayList<Schema> schemas = new ArrayList<>();
    schemas.add(objectSchema);
    setCompositionsSchemas.accept(composedSchema, schemas);

    final SchemaCompositions schemaCompositions = SchemaCompositions.wrap(composedSchema);

    final PojoName pojoName = PojoName.ofNameAndSuffix(Name.ofString("ComposedPojoName"), "Dto");

    final PojoName objectSchemaPojoName =
        PojoName.ofNameAndSuffix(
            String.format("ComposedPojoName%s", type.asPascalCaseName()), "Dto");
    final PList<PojoName> pojoNames = PList.of(objectSchemaPojoName);

    final UnmappedItems expectedUnmappedItems =
        UnmappedItems.ofPojoSchema(new PojoSchema(objectSchemaPojoName, objectSchema));

    return Arguments.arguments(schemaCompositions, pojoName, pojoNames, expectedUnmappedItems);
  }

  private static Arguments multipleInlineDefinitionArguments(
      ComposedSchemas.CompositionType type,
      BiConsumer<ComposedSchema, List<Schema>> setCompositionsSchemas) {
    final ComposedSchema composedSchema = new ComposedSchema();
    composedSchema.setDescription("Test description");

    final io.swagger.v3.oas.models.media.ObjectSchema objectSchema1 =
        new io.swagger.v3.oas.models.media.ObjectSchema();
    final HashMap<String, Schema> properties1 = new HashMap<>();
    properties1.put("key1", new StringSchema());
    objectSchema1.setProperties(properties1);

    final io.swagger.v3.oas.models.media.ObjectSchema objectSchema2 = new ObjectSchema();
    final HashMap<String, Schema> properties2 = new HashMap<>();
    properties2.put("key2", new IntegerSchema());
    objectSchema2.setProperties(properties2);

    final ArrayList<Schema> schemas = new ArrayList<>();
    schemas.add(objectSchema1);
    schemas.add(objectSchema2);
    setCompositionsSchemas.accept(composedSchema, schemas);

    final SchemaCompositions schemaCompositions = SchemaCompositions.wrap(composedSchema);

    final PojoName pojoName = PojoName.ofNameAndSuffix(Name.ofString("ComposedPojoName"), "Dto");

    final PojoName objectSchema1PojoName =
        PojoName.ofNameAndSuffix(
            String.format("ComposedPojoName%s0", type.asPascalCaseName()), "Dto");
    final PojoName objectSchema2PojoName =
        PojoName.ofNameAndSuffix(
            String.format("ComposedPojoName%s1", type.asPascalCaseName()), "Dto");

    final PList<PojoName> pojoNames = PList.of(objectSchema1PojoName, objectSchema2PojoName);

    final UnmappedItems expectedUnmappedItems =
        UnmappedItems.ofPojoSchemas(
            PList.of(
                new PojoSchema(objectSchema1PojoName, objectSchema1),
                new PojoSchema(objectSchema2PojoName, objectSchema2)));

    return Arguments.arguments(schemaCompositions, pojoName, pojoNames, expectedUnmappedItems);
  }
}
