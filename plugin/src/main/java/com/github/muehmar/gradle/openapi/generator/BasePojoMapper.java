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
import java.util.Optional;

public abstract class BasePojoMapper implements PojoMapper {

  @Override
  public PList<Pojo> fromSchema(PList<OpenApiPojo> openApiPojos, PojoSettings pojoSettings) {
    return openApiPojos.flatMap(openApiPojo -> fromSingleSchema(openApiPojo, pojoSettings));
  }

  private PList<Pojo> fromSingleSchema(OpenApiPojo openApiPojo, PojoSettings pojoSettings) {
    final PojoProcessResult pojoProcessResult =
        openApiPojo.getSchema() instanceof ArraySchema
            ? createArrayPojo(
                openApiPojo.getKey(), (ArraySchema) openApiPojo.getSchema(), pojoSettings)
            : processSchema(openApiPojo.getKey(), openApiPojo.getSchema(), pojoSettings);

    final PList<Pojo> innerPojos =
        pojoProcessResult.getOpenApiPojos().flatMap(oaPojo -> fromSchema(oaPojo, pojoSettings));

    return innerPojos.cons(pojoProcessResult.getPojo());
  }

  private PojoProcessResult processSchema(String key, Schema<?> schema, PojoSettings pojoSettings) {

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
                  return pojoMemberFromSchema(
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

  private ComposedPojo processComposedSchema(
      String key, ComposedSchema schema, PojoSettings pojoSettings) {
    if (schema.getOneOf() != null) {
      throw new IllegalArgumentException("oneOf composition is currently not supported");
    }

    if (schema.getAnyOf() != null) {
      throw new IllegalArgumentException("anyOf composition is currently not supported");
    }

    if (schema.getAllOf() == null) {
      throw new IllegalArgumentException("Schema composition does not contain any schemas.");
    }

    throw new UnsupportedOperationException("Not implemented yet");
  }

  /**
   * An implementation should create the {@link Pojo} representation for the given {@code key} and
   * {@link ArraySchema}. Possible inline definitions of objects can be included in the returned
   * container {@link PojoProcessResult}.
   */
  protected abstract PojoProcessResult createArrayPojo(
      String key, ArraySchema schema, PojoSettings pojoSettings);

  /**
   * An implementation should create the {@link PojoMember} representation for the given {@code key}
   * and {@link ArraySchema}. Possible inline definitions of objects can be included in the returned
   * container {@link PojoMemberProcessResult}.
   */
  protected abstract PojoMemberProcessResult pojoMemberFromSchema(
      String pojoKey, String key, Schema<?> schema, PojoSettings pojoSettings, boolean nullable);

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
  }
}
