package com.github.muehmar.gradle.openapi.generator;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;

public abstract class BasePojoMapper implements PojoMapper {
  @Override
  public PList<Pojo> fromSchema(String key, Schema<?> schema, PojoSettings pojoSettings) {
    final PojoAndOpenApiPojos pojoAndOpenApiPojos =
        schema instanceof ArraySchema
            ? createArrayPojo(key, (ArraySchema) schema, pojoSettings)
            : processSchema(key, schema, pojoSettings);

    final PList<Pojo> innerPojos =
        pojoAndOpenApiPojos
            .getOpenApiPojos()
            .flatMap(
                openApiPojo ->
                    fromSchema(openApiPojo.getKey(), openApiPojo.getSchema(), pojoSettings));

    return innerPojos.cons(pojoAndOpenApiPojos.getPojo());
  }

  private PojoAndOpenApiPojos processSchema(
      String key, Schema<?> schema, PojoSettings pojoSettings) {
    final PList<PojoMemberAndOpenApiPojos> pojoMemberAndOpenApiPojos =
        Optional.ofNullable(schema.getProperties())
            .map(properties -> PList.fromIter(properties.entrySet()))
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Could only create pojo from schema with properties: " + schema))
            .map(
                entry -> {
                  final Boolean nullable =
                      Optional.ofNullable(schema.getRequired())
                          .map(req -> req.stream().noneMatch(entry.getKey()::equals))
                          .orElse(true);
                  return pojoMemberFromSchema(
                      entry.getKey(), entry.getValue(), pojoSettings, nullable);
                });

    final Pojo pojo =
        new Pojo(
            key,
            schema.getDescription(),
            pojoSettings.getSuffix(),
            pojoMemberAndOpenApiPojos.map(PojoMemberAndOpenApiPojos::getPojoMember),
            false);

    final PList<OpenApiPojo> openApiPojos =
        pojoMemberAndOpenApiPojos.flatMap(PojoMemberAndOpenApiPojos::getOpenApiPojos);

    return new PojoAndOpenApiPojos(pojo, openApiPojos);
  }

  /**
   * An implementation should create the {@link Pojo} representation for the given {@code key} and
   * {@link ArraySchema}. Possible inline definitions of objects can be included in the returned
   * container {@link PojoAndOpenApiPojos}.
   */
  protected abstract PojoAndOpenApiPojos createArrayPojo(
      String key, ArraySchema schema, PojoSettings pojoSettings);

  /**
   * An implementation should create the {@link PojoMember} representation for the given {@code key}
   * and {@link ArraySchema}. Possible inline definitions of objects can be included in the returned
   * container {@link PojoMemberAndOpenApiPojos}.
   */
  protected abstract PojoMemberAndOpenApiPojos pojoMemberFromSchema(
      String key, Schema<?> schema, PojoSettings pojoSettings, boolean nullable);

  /**
   * Container holding a {@link PojoMember} as well as a list of {@link OpenApiPojo} which in turn
   * contains a key and a {@link Schema}.
   */
  public static class PojoMemberAndOpenApiPojos {
    private final PojoMember pojoMember;
    private final PList<OpenApiPojo> openApiPojos;

    public PojoMemberAndOpenApiPojos(PojoMember pojoMember, PList<OpenApiPojo> openApiPojos) {
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

  /**
   * Container holding a {@link Pojo} as well as a list of {@link OpenApiPojo} which in turn
   * contains a key and a {@link Schema}.
   */
  public static class PojoAndOpenApiPojos {
    private final Pojo pojo;
    private final PList<OpenApiPojo> openApiPojos;

    public PojoAndOpenApiPojos(Pojo pojo, PList<OpenApiPojo> openApiPojos) {
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
