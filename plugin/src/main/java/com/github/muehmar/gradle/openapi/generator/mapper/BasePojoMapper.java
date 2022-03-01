package com.github.muehmar.gradle.openapi.generator.mapper;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.PojoMapper;
import com.github.muehmar.gradle.openapi.generator.data.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.data.Name;
import com.github.muehmar.gradle.openapi.generator.data.Necessity;
import com.github.muehmar.gradle.openapi.generator.data.Nullability;
import com.github.muehmar.gradle.openapi.generator.data.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.data.Pojo;
import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import com.github.muehmar.gradle.openapi.generator.data.PojoMemberReference;
import com.github.muehmar.gradle.openapi.generator.data.Type;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public abstract class BasePojoMapper implements PojoMapper {
  private static final PList<String> SUPPORTED_MEMBER_SCHEMAS =
      PList.of("string", "integer", "number", "boolean");

  protected final OpenApiProcessor openApiProcessor;

  protected BasePojoMapper() {
    openApiProcessor =
        arrayOpenApiProcessor()
            .or(objectOpenApiProcessor())
            .or(composedOpenApiProcessor())
            .or(enumOpenApiProcessor())
            .or(memberOpenApiProcessor());
  }

  /**
   * An implementation should create the {@link Pojo} representation for the given {@code pojoName}
   * and {@link ArraySchema}. Possible inline definitions of objects can be included in the returned
   * container {@link PojoProcessResult}.
   */
  protected abstract PojoProcessResult fromArraysSchema(
      Name pojoName, ArraySchema schema, PojoSettings pojoSettings);

  /**
   * An implementation should create the {@link PojoMember} representation for the given {@code
   * pojoMemberName} and {@link ArraySchema}. Possible inline definitions of objects can be included
   * in the returned container {@link PojoMemberProcessResult}.
   */
  protected abstract PojoMemberProcessResult toPojoMemberFromSchema(
      Name pojoName,
      Name pojoMemberName,
      Schema<?> schema,
      PojoSettings pojoSettings,
      Necessity necessity,
      Nullability nullability);

  /**
   * An implementation should create a {@link ComposedPojo} from the given {@link Schema}'s
   * referenced by a composed schema with the given type.
   */
  protected abstract ComposedPojo fromComposedSchema(
      Name name,
      String description,
      ComposedPojo.CompositionType type,
      PList<Schema<?>> schemas,
      PojoSettings pojoSettings);

  @Override
  public PList<Pojo> fromSchemas(PList<OpenApiPojo> openApiPojos, PojoSettings pojoSettings) {
    final PList<SchemaProcessResult> processResults =
        openApiPojos.map(openApiPojo -> processSchema(openApiPojo, pojoSettings));

    final PList<Pojo> pojos = processResults.flatMap(SchemaProcessResult::getPojos);
    final PList<ComposedPojo> composedPojos =
        processResults.flatMap(SchemaProcessResult::getComposedPojos);
    final PList<PojoMemberReference> pojoMemberReferences =
        processResults.flatMap(SchemaProcessResult::getPojoMemberReferences);

    return Optional.of(pojos)
        .map(p -> ComposedPojoConverter.convert(composedPojos, pojos))
        .map(p -> inlineMemberReferences(p, pojoMemberReferences, pojoSettings))
        .map(p -> replaceEnumReferences(p, pojoSettings))
        .orElse(PList.empty());
  }

  private PList<Pojo> inlineMemberReferences(
      PList<Pojo> inputPojos,
      PList<PojoMemberReference> pojoMemberReferences,
      PojoSettings pojoSettings) {
    return pojoMemberReferences.foldLeft(
        inputPojos,
        (pojos, memberReference) ->
            pojos.map(
                pojo ->
                    pojo.replaceMemberType(
                        memberReference.getName().append(pojoSettings.getSuffix()),
                        memberReference.getDescription(),
                        memberReference.getType())));
  }

  private PList<Pojo> replaceEnumReferences(PList<Pojo> inputPojos, PojoSettings pojoSettings) {
    return inputPojos
        .filter(Pojo::isEnum)
        .foldLeft(
            inputPojos,
            (p, enumPojo) ->
                p.map(
                    pojo -> {
                      final Name enumName = enumPojo.getName().append(pojoSettings.getSuffix());
                      return pojo.replaceMemberType(
                          enumName, enumPojo.getDescription(), Type.simpleOfName(enumName));
                    }));
  }

  private SchemaProcessResult processSchema(OpenApiPojo openApiPojo, PojoSettings pojoSettings) {
    return openApiProcessor
        .process(openApiPojo, pojoSettings)
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "The following schema is currently not supported as root schema in the components section (contact the maintainer to support this schema as well): "
                        + openApiPojo.getSchema()));
  }

  /** Process array schemas */
  private OpenApiProcessor arrayOpenApiProcessor() {
    return (openApiPojo, pojoSettings) -> {
      if (openApiPojo.getSchema() instanceof ArraySchema) {
        final PojoProcessResult pojoProcessResult =
            fromArraysSchema(
                openApiPojo.getName(), (ArraySchema) openApiPojo.getSchema(), pojoSettings);
        return Optional.of(processPojoProcessResult(pojoProcessResult, pojoSettings));
      } else {
        return Optional.empty();
      }
    };
  }

  /** Processes object schemas (definitions with properties) */
  private OpenApiProcessor objectOpenApiProcessor() {
    return (openApiPojo, pojoSettings) -> {
      if (openApiPojo.getSchema().getProperties() != null) {
        final PojoProcessResult pojoProcessResult =
            processObjectSchema(openApiPojo.getName(), openApiPojo.getSchema(), pojoSettings);
        return Optional.of(processPojoProcessResult(pojoProcessResult, pojoSettings));
      } else {
        return Optional.empty();
      }
    };
  }

  /** Process schema compositions */
  private OpenApiProcessor composedOpenApiProcessor() {
    return (openApiPojo, pojoSettings) -> {
      if (openApiPojo.getSchema() instanceof ComposedSchema) {
        final ComposedPojo composedPojo =
            processComposedSchema(
                openApiPojo.getName(), (ComposedSchema) openApiPojo.getSchema(), pojoSettings);

        return Optional.of(processComposedPojo(composedPojo, pojoSettings));
      } else {
        return Optional.empty();
      }
    };
  }

  /** Processes schemas which are single member definitions */
  private OpenApiProcessor memberOpenApiProcessor() {
    return ((openApiPojo, pojoSettings) -> {
      final String type = openApiPojo.getSchema().getType();
      if (Objects.nonNull(type) && SUPPORTED_MEMBER_SCHEMAS.exists(type::equals)) {
        return Optional.of(
            SchemaProcessResult.ofPojoMemberReference(
                processMemberSchema(openApiPojo.getName(), openApiPojo.getSchema(), pojoSettings)));
      } else {
        return Optional.empty();
      }
    });
  }

  /** Processes enums as root schema definitions */
  private OpenApiProcessor enumOpenApiProcessor() {
    return ((openApiPojo, pojoSettings) -> {
      final Schema<?> schema = openApiPojo.getSchema();
      if (new StringSchema().getType().equals(schema.getType())
          && Objects.nonNull(schema.getEnum())) {
        final PojoMemberProcessResult pojoMemberProcessResult =
            toPojoMemberFromSchema(
                Name.of("Unused"),
                openApiPojo.getName(),
                schema,
                pojoSettings,
                Necessity.OPTIONAL,
                Nullability.NOT_NULLABLE);
        final Pojo pojo =
            Pojo.ofEnum(
                openApiPojo.getName(),
                schema.getDescription(),
                pojoSettings.getSuffix(),
                pojoMemberProcessResult.getPojoMember().getType());
        return Optional.ofNullable(SchemaProcessResult.ofPojo(pojo));
      } else {
        return Optional.empty();
      }
    });
  }

  private SchemaProcessResult processPojoProcessResult(
      PojoProcessResult pojoProcessResult, PojoSettings pojoSettings) {
    return pojoProcessResult
        .getOpenApiPojos()
        .map(oaPojo -> processSchema(oaPojo, pojoSettings))
        .foldRight(SchemaProcessResult.empty(), SchemaProcessResult::concat)
        .addPojo(pojoProcessResult.getPojo());
  }

  private SchemaProcessResult processComposedPojo(
      ComposedPojo composedPojo, PojoSettings pojoSettings) {
    return composedPojo
        .getOpenApiPojos()
        .map(oaPojo -> processSchema(oaPojo, pojoSettings))
        .foldRight(SchemaProcessResult.empty(), SchemaProcessResult::concat)
        .addComposedPojo(composedPojo);
  }

  private PojoProcessResult processObjectSchema(
      Name pojoName, Schema<?> schema, PojoSettings pojoSettings) {

    final PList<PojoMemberProcessResult> pojoMemberAndOpenApiPojos =
        Optional.ofNullable(schema.getProperties())
            .map(properties -> PList.fromIter(properties.entrySet()))
            .orElseThrow(
                () -> new IllegalArgumentException("Object schema without properties: " + schema))
            .map(entry -> processObjectSchemaEntry(entry, pojoName, schema, pojoSettings));

    final Pojo pojo =
        Pojo.ofObject(
            pojoName,
            schema.getDescription(),
            pojoSettings.getSuffix(),
            pojoMemberAndOpenApiPojos.map(PojoMemberProcessResult::getPojoMember));

    final PList<OpenApiPojo> openApiPojos =
        pojoMemberAndOpenApiPojos.flatMap(PojoMemberProcessResult::getOpenApiPojos);

    return new PojoProcessResult(pojo, openApiPojos);
  }

  private PojoMemberProcessResult processObjectSchemaEntry(
      Map.Entry<String, Schema> entry, Name pojoName, Schema<?> schema, PojoSettings pojoSettings) {
    final Necessity necessity =
        Optional.ofNullable(schema.getRequired())
            .map(req -> req.stream().anyMatch(entry.getKey()::equals))
            .map(Necessity::fromBoolean)
            .orElse(Necessity.OPTIONAL);

    final Nullability nullability =
        Optional.ofNullable(entry.getValue().getNullable())
            .map(Nullability::fromNullableBoolean)
            .orElse(Nullability.NOT_NULLABLE);

    return toPojoMemberFromSchema(
        pojoName, Name.of(entry.getKey()), entry.getValue(), pojoSettings, necessity, nullability);
  }

  private ComposedPojo processComposedSchema(
      Name name, ComposedSchema schema, PojoSettings pojoSettings) {
    if (schema.getOneOf() != null) {
      return fromComposedSchema(
          name,
          schema.getDescription(),
          ComposedPojo.CompositionType.ONE_OF,
          PList.fromIter(schema.getOneOf()).map(s -> (Schema<?>) s),
          pojoSettings);
    }

    if (schema.getAnyOf() != null) {
      return fromComposedSchema(
          name,
          schema.getDescription(),
          ComposedPojo.CompositionType.ANY_OF,
          PList.fromIter(schema.getAnyOf()).map(s -> (Schema<?>) s),
          pojoSettings);
    }

    if (schema.getAllOf() != null) {
      return fromComposedSchema(
          name,
          schema.getDescription(),
          ComposedPojo.CompositionType.ALL_OF,
          PList.fromIter(schema.getAllOf()).map(s -> (Schema<?>) s),
          pojoSettings);
    }

    throw new IllegalArgumentException("Composed schema without any schema definitions");
  }

  private PojoMemberReference processMemberSchema(
      Name name, Schema<?> schema, PojoSettings pojoSettings) {
    final PojoMemberProcessResult pojoMemberProcessResult =
        toPojoMemberFromSchema(
            Name.of("Unused"),
            name,
            schema,
            pojoSettings,
            Necessity.OPTIONAL,
            Nullability.NOT_NULLABLE);

    return new PojoMemberReference(
        name, schema.getDescription(), pojoMemberProcessResult.getPojoMember().getType());
  }
}
