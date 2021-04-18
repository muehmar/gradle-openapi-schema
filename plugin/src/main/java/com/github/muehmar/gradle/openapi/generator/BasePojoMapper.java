package com.github.muehmar.gradle.openapi.generator;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.data.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.data.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.data.Pojo;
import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public abstract class BasePojoMapper implements PojoMapper {

  @Override
  public PList<Pojo> fromSchema(PList<OpenApiPojo> openApiPojos, PojoSettings pojoSettings) {
    final PList<SchemaProcessResult> map =
        openApiPojos.map(openApiPojo -> fromSingleSchema(openApiPojo, pojoSettings));

    final PList<Pojo> pojos = map.flatMap(SchemaProcessResult::getPojos);
    final PList<ComposedPojo> composedPojos = map.flatMap(SchemaProcessResult::getComposedPojos);

    return convertComposedPojos(composedPojos, pojos, pojoSettings);
  }

  private SchemaProcessResult fromSingleSchema(OpenApiPojo openApiPojo, PojoSettings pojoSettings) {
    if (openApiPojo.getSchema() instanceof ComposedSchema) {
      final ComposedPojo composedPojo =
          fromComposedSchema(
              openApiPojo.getKey(), (ComposedSchema) openApiPojo.getSchema(), pojoSettings);

      final SchemaProcessResult schemaProcessResult =
          composedPojo
              .getOpenApiPojos()
              .map(oaPojo -> fromSingleSchema(oaPojo, pojoSettings))
              .foldLeft(SchemaProcessResult.empty(), SchemaProcessResult::concat);
      return schemaProcessResult.addComposedPojo(composedPojo);
    } else {
      final PojoProcessResult pojoProcessResult =
          openApiPojo.getSchema() instanceof ArraySchema
              ? fromArraysSchema(
                  openApiPojo.getKey(), (ArraySchema) openApiPojo.getSchema(), pojoSettings)
              : fromObjectSchema(openApiPojo.getKey(), openApiPojo.getSchema(), pojoSettings);

      final SchemaProcessResult schemaProcessResult =
          pojoProcessResult
              .getOpenApiPojos()
              .map(oaPojo -> fromSingleSchema(oaPojo, pojoSettings))
              .foldRight(SchemaProcessResult.empty(), SchemaProcessResult::concat);

      return schemaProcessResult.addPojo(pojoProcessResult.getPojo());
    }
  }

  private PojoProcessResult fromObjectSchema(
      String key, Schema<?> schema, PojoSettings pojoSettings) {

    final PList<PojoMemberProcessResult> pojoMemberAndOpenApiPojos =
        Optional.ofNullable(schema.getProperties())
            .map(properties -> PList.fromIter(properties.entrySet()))
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "The following schema is currently not supported: " + schema))
            .map(
                entry -> {
                  final Boolean nullable =
                      Optional.ofNullable(schema.getRequired())
                          .map(req -> req.stream().noneMatch(entry.getKey()::equals))
                          .orElse(true);
                  return toPojoMemberFromSchema(
                      key, entry.getKey(), entry.getValue(), pojoSettings, nullable);
                });

    final Pojo pojo =
        new Pojo(
            key,
            schema.getDescription(),
            pojoSettings.getSuffix(),
            pojoMemberAndOpenApiPojos.map(PojoMemberProcessResult::getPojoMember),
            false);

    final PList<OpenApiPojo> openApiPojos =
        pojoMemberAndOpenApiPojos.flatMap(PojoMemberProcessResult::getOpenApiPojos);

    return new PojoProcessResult(pojo, openApiPojos);
  }

  private ComposedPojo fromComposedSchema(
      String key, ComposedSchema schema, PojoSettings pojoSettings) {
    if (schema.getOneOf() != null) {
      return fromComposedSchema(
          key,
          schema.getDescription(),
          ComposedPojo.CompositionType.ONE_OF,
          PList.fromIter(schema.getOneOf()).map(s -> (Schema<?>) s),
          pojoSettings);
    }

    if (schema.getAnyOf() != null) {
      return fromComposedSchema(
          key,
          schema.getDescription(),
          ComposedPojo.CompositionType.ANY_OF,
          PList.fromIter(schema.getAnyOf()).map(s -> (Schema<?>) s),
          pojoSettings);
    }

    if (schema.getAllOf() != null) {
      return fromComposedSchema(
          key,
          schema.getDescription(),
          ComposedPojo.CompositionType.ALL_OF,
          PList.fromIter(schema.getAllOf()).map(s -> (Schema<?>) s),
          pojoSettings);
    }

    throw new IllegalArgumentException("Composed schema without any schema definitions");
  }

  private PList<Pojo> convertComposedPojos(
      PList<ComposedPojo> composedPojos, PList<Pojo> pojos, PojoSettings pojoSettings) {

    final PList<SchemaProcessResult> conversionResult =
        composedPojos
            .filter(
                composedPojo -> composedPojo.getType().equals(ComposedPojo.CompositionType.ALL_OF))
            .map(
                composedPojo -> {
                  final PList<String> pojoNames = composedPojo.getPojoNames();
                  final PList<String> openApiPojoNames =
                      composedPojo.getOpenApiPojos().map(OpenApiPojo::getKey);

                  final PList<Optional<Pojo>> foundPojos =
                      pojoNames
                          .concat(openApiPojoNames)
                          .map(name -> pojos.find(pojo -> pojo.getKey().equalsIgnoreCase(name)));
                  if (foundPojos.exists(p -> !p.isPresent())) {
                    return new SchemaProcessResult(PList.empty(), PList.single(composedPojo));
                  } else {
                    final PList<PojoMember> members =
                        foundPojos.flatMapOptional(Function.identity()).flatMap(Pojo::getMembers);
                    final Pojo pojo =
                        new Pojo(
                            composedPojo.getKey(),
                            composedPojo.getDescription(),
                            composedPojo.getSuffix(),
                            members,
                            false);
                    return new SchemaProcessResult(PList.single(pojo), PList.empty());
                  }
                });

    final PList<Pojo> newPojos = conversionResult.flatMap(SchemaProcessResult::getPojos);
    final PList<ComposedPojo> unconvertedComposedPojos =
        conversionResult.flatMap(SchemaProcessResult::getComposedPojos);
    if (newPojos.isEmpty() && unconvertedComposedPojos.nonEmpty()) {
      throw new IllegalStateException(
          "Unable to resolve schemas of composed schema: " + unconvertedComposedPojos);
    } else if (unconvertedComposedPojos.isEmpty()) {
      return pojos.concat(newPojos);
    } else {
      return convertComposedPojos(unconvertedComposedPojos, pojos.concat(newPojos), pojoSettings);
    }
  }

  /**
   * An implementation should create the {@link Pojo} representation for the given {@code key} and
   * {@link ArraySchema}. Possible inline definitions of objects can be included in the returned
   * container {@link PojoProcessResult}.
   */
  protected abstract PojoProcessResult fromArraysSchema(
      String key, ArraySchema schema, PojoSettings pojoSettings);

  /**
   * An implementation should create the {@link PojoMember} representation for the given {@code key}
   * and {@link ArraySchema}. Possible inline definitions of objects can be included in the returned
   * container {@link PojoMemberProcessResult}.
   */
  protected abstract PojoMemberProcessResult toPojoMemberFromSchema(
      String pojoKey, String key, Schema<?> schema, PojoSettings pojoSettings, boolean nullable);

  protected abstract ComposedPojo fromComposedSchema(
      String key,
      String description,
      ComposedPojo.CompositionType type,
      PList<Schema<?>> schemas,
      PojoSettings pojoSettings);

  /** Data class holding the result of processing a schema as a member of a pojo. */
  public static class PojoMemberProcessResult {
    private final PojoMember pojoMember;
    private final PList<OpenApiPojo> openApiPojos;

    public PojoMemberProcessResult(PojoMember pojoMember, PList<OpenApiPojo> openApiPojos) {
      this.pojoMember = pojoMember;
      this.openApiPojos = openApiPojos;
    }

    public PojoMember getPojoMember() {
      return pojoMember;
    }

    public PList<OpenApiPojo> getOpenApiPojos() {
      return openApiPojos;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      PojoMemberProcessResult that = (PojoMemberProcessResult) o;
      return Objects.equals(pojoMember, that.pojoMember)
          && Objects.equals(openApiPojos, that.openApiPojos);
    }

    @Override
    public int hashCode() {
      return Objects.hash(pojoMember, openApiPojos);
    }

    @Override
    public String toString() {
      return "PojoMemberProcessResult{"
          + "pojoMember="
          + pojoMember
          + ", openApiPojos="
          + openApiPojos
          + '}';
    }
  }

  /** Data class holding the result of processing a schema as a pojo. */
  public static class PojoProcessResult {
    private final Pojo pojo;
    private final PList<OpenApiPojo> openApiPojos;

    public PojoProcessResult(Pojo pojo, PList<OpenApiPojo> openApiPojos) {
      this.pojo = pojo;
      this.openApiPojos = openApiPojos;
    }

    public Pojo getPojo() {
      return pojo;
    }

    public PList<OpenApiPojo> getOpenApiPojos() {
      return openApiPojos;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      PojoProcessResult that = (PojoProcessResult) o;
      return Objects.equals(pojo, that.pojo) && Objects.equals(openApiPojos, that.openApiPojos);
    }

    @Override
    public int hashCode() {
      return Objects.hash(pojo, openApiPojos);
    }

    @Override
    public String toString() {
      return "PojoProcessResult{" + "pojo=" + pojo + ", openApiPojos=" + openApiPojos + '}';
    }
  }

  public static class SchemaProcessResult {
    private final PList<Pojo> pojos;
    private final PList<ComposedPojo> composedPojos;

    public SchemaProcessResult(PList<Pojo> pojos, PList<ComposedPojo> composedPojos) {
      this.pojos = pojos;
      this.composedPojos = composedPojos;
    }

    public static SchemaProcessResult empty() {
      return new SchemaProcessResult(PList.empty(), PList.empty());
    }

    public PList<Pojo> getPojos() {
      return pojos;
    }

    public PList<ComposedPojo> getComposedPojos() {
      return composedPojos;
    }

    public SchemaProcessResult concat(SchemaProcessResult other) {
      return new SchemaProcessResult(
          pojos.concat(other.pojos), composedPojos.concat(other.composedPojos));
    }

    public SchemaProcessResult addComposedPojo(ComposedPojo composedPojo) {
      return new SchemaProcessResult(pojos, composedPojos.cons(composedPojo));
    }

    public SchemaProcessResult addPojo(Pojo pojo) {
      return new SchemaProcessResult(pojos.cons(pojo), composedPojos);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      SchemaProcessResult that = (SchemaProcessResult) o;
      return Objects.equals(pojos, that.pojos) && Objects.equals(composedPojos, that.composedPojos);
    }

    @Override
    public int hashCode() {
      return Objects.hash(pojos, composedPojos);
    }

    @Override
    public String toString() {
      return "SchemaProcessResult{" + "pojos=" + pojos + ", composedPojos=" + composedPojos + '}';
    }
  }
}
