package com.github.muehmar.gradle.openapi.generator.mapper.processor;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.typemapper.TypeMapResult;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Map;
import java.util.Optional;

public class ObjectOpenApiProcessor extends BaseSingleSchemaOpenApiProcessor {

  @Override
  public Optional<SchemaProcessResult> process(
      OpenApiPojo openApiPojo, CompleteOpenApiProcessor completeOpenApiProcessor) {
    if (openApiPojo.getSchema().getProperties() != null) {
      final PojoProcessResult pojoProcessResult =
          processObjectSchema(openApiPojo.getPojoName(), openApiPojo.getSchema());
      return Optional.of(processPojoProcessResult(pojoProcessResult, completeOpenApiProcessor));
    } else {
      return Optional.empty();
    }
  }

  private PojoProcessResult processObjectSchema(PojoName pojoName, Schema<?> schema) {

    final PList<PojoMemberProcessResult> pojoMemberAndOpenApiPojos =
        Optional.ofNullable(schema.getProperties())
            .map(properties -> PList.fromIter(properties.entrySet()))
            .orElseThrow(
                () -> new IllegalArgumentException("Object schema without properties: " + schema))
            .map(entry -> processObjectSchemaEntry(entry, pojoName, schema));

    final Pojo pojo =
        ObjectPojo.of(
            pojoName,
            schema.getDescription(),
            pojoMemberAndOpenApiPojos.map(PojoMemberProcessResult::getPojoMember));

    final PList<OpenApiPojo> openApiPojos =
        pojoMemberAndOpenApiPojos.flatMap(PojoMemberProcessResult::getOpenApiPojos);

    return new PojoProcessResult(pojo, openApiPojos);
  }

  private PojoMemberProcessResult processObjectSchemaEntry(
      Map.Entry<String, Schema> entry, PojoName pojoName, Schema<?> schema) {
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
        pojoName, Name.ofString(entry.getKey()), entry.getValue(), necessity, nullability);
  }

  private PojoMemberProcessResult toPojoMemberFromSchema(
      PojoName pojoName,
      Name pojoMemberName,
      Schema<?> schema,
      Necessity necessity,
      Nullability nullability) {
    final TypeMapResult result = COMPLETE_TYPE_MAPPER.map(pojoName, pojoMemberName, schema);

    final Type type = result.getType();

    final PojoMember pojoMember =
        new PojoMember(pojoMemberName, schema.getDescription(), type, necessity, nullability);
    return new PojoMemberProcessResult(pojoMember, result.getOpenApiPojos());
  }
}
