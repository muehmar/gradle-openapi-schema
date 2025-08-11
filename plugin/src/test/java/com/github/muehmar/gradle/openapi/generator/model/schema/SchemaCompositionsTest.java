package com.github.muehmar.gradle.openapi.generator.model.schema;

import static com.github.muehmar.gradle.openapi.generator.model.name.ComponentNames.componentName;
import static com.github.muehmar.gradle.openapi.generator.model.schema.SchemaWrappers.wrap;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.composition.UnresolvedAllOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.UnresolvedAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.UnresolvedOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SchemaCompositionsTest {

  @ParameterizedTest
  @MethodSource("allOfArguments")
  void getAllOf_when_allOfArguments_then_correctMapContext(
      SchemaCompositions schemaCompositions,
      ComponentName componentName,
      PList<ComponentName> componentNames,
      UnmappedItems expectedUnmappedItems) {

    // method call
    final SchemaCompositions.CompositionMapResult<UnresolvedAllOfComposition> result =
        schemaCompositions.getAllOf(componentName);

    final UnresolvedAllOfComposition expectedComposition =
        UnresolvedAllOfComposition.fromComponentNames(componentNames);

    assertEquals(Optional.of(expectedComposition), result.getComposition());
    assertEquals(expectedUnmappedItems, result.getUnmappedItems());
  }

  @ParameterizedTest
  @MethodSource("oneOfArguments")
  void getOneOf_when_oneOfArguments_then_correctMapContext(
      SchemaCompositions schemaCompositions,
      ComponentName componentName,
      PList<ComponentName> componentNames,
      UnmappedItems expectedUnmappedItems) {

    // method call
    final SchemaCompositions.CompositionMapResult<UnresolvedOneOfComposition> result =
        schemaCompositions.getOneOf(componentName);

    final UnresolvedOneOfComposition expectedComposition =
        UnresolvedOneOfComposition.fromComponentNames(componentNames);

    assertEquals(Optional.of(expectedComposition), result.getComposition());
    assertEquals(expectedUnmappedItems, result.getUnmappedItems());
  }

  @ParameterizedTest
  @MethodSource("anyOfArguments")
  void getAnyOf_when_anyOfArguments_then_correctMapContext(
      SchemaCompositions schemaCompositions,
      ComponentName componentName,
      PList<ComponentName> componentNames,
      UnmappedItems expectedUnmappedItems) {

    // method call
    final SchemaCompositions.CompositionMapResult<UnresolvedAnyOfComposition> result =
        schemaCompositions.getAnyOf(componentName);

    final UnresolvedAnyOfComposition expectedComposition =
        UnresolvedAnyOfComposition.fromPojoNames(componentNames);

    assertEquals(Optional.of(expectedComposition), result.getComposition());
    assertEquals(expectedUnmappedItems, result.getUnmappedItems());
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

    final SchemaCompositions schemaCompositions = SchemaCompositions.wrap(wrap(composedSchema));

    final ComponentName componentName = componentName("ComposedPojoName", "Dto");

    final ComponentName refSchema1ComponentName = componentName("ReferenceSchema1", "Dto");
    final ComponentName refSchema2ComponentName = componentName("ReferenceSchema2", "Dto");
    final PList<ComponentName> componentNames =
        PList.of(refSchema1ComponentName, refSchema2ComponentName);

    final UnmappedItems expectedUnmappedItems =
        UnmappedItems.ofSpec(OpenApiSpec.fromPath(Path.of("../../dir/components.yml")));

    return Arguments.arguments(
        schemaCompositions, componentName, componentNames, expectedUnmappedItems);
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

    final SchemaCompositions schemaCompositions = SchemaCompositions.wrap(wrap(composedSchema));

    final ComponentName componentName = componentName("ComposedPojoName", "Dto");

    final ComponentName objectSchemaComponentName =
        componentName(String.format("ComposedPojoName%s", type.asPascalCaseName()), "Dto");
    final PList<ComponentName> componentNames = PList.of(objectSchemaComponentName);

    final UnmappedItems expectedUnmappedItems =
        UnmappedItems.ofPojoSchema(new PojoSchema(objectSchemaComponentName, wrap(objectSchema)));

    return Arguments.arguments(
        schemaCompositions, componentName, componentNames, expectedUnmappedItems);
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

    final SchemaCompositions schemaCompositions = SchemaCompositions.wrap(wrap(composedSchema));

    final ComponentName componentName = componentName("ComposedPojoName", "Dto");

    final ComponentName objectSchema1ComponentName =
        componentName(String.format("ComposedPojoName%s0", type.asPascalCaseName()), "Dto");
    final ComponentName objectSchema2ComponentName =
        componentName(String.format("ComposedPojoName%s1", type.asPascalCaseName()), "Dto");

    final PList<ComponentName> componentNames =
        PList.of(objectSchema1ComponentName, objectSchema2ComponentName);

    final UnmappedItems expectedUnmappedItems =
        UnmappedItems.ofPojoSchemas(
            PList.of(
                new PojoSchema(objectSchema1ComponentName, wrap(objectSchema1)),
                new PojoSchema(objectSchema2ComponentName, wrap(objectSchema2))));

    return Arguments.arguments(
        schemaCompositions, componentName, componentNames, expectedUnmappedItems);
  }
}
