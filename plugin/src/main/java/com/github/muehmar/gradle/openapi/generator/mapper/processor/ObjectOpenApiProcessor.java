package com.github.muehmar.gradle.openapi.generator.mapper.processor;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.typemapper.CompleteTypeMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.typemapper.TypeMapResult;
import com.github.muehmar.gradle.openapi.generator.mapper.typemapper.TypeMapper;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.NewPojo;
import com.github.muehmar.gradle.openapi.generator.model.NewPojoMember;
import com.github.muehmar.gradle.openapi.generator.model.NewType;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Map;
import java.util.Optional;

public class ObjectOpenApiProcessor extends BaseSingleSchemaOpenApiProcessor {
  private static final TypeMapper COMPLETE_MAPPER = CompleteTypeMapper.create();

  @Override
  public Optional<NewSchemaProcessResult> process(
      OpenApiPojo openApiPojo,
      PojoSettings pojoSettings,
      NewCompleteOpenApiProcessor completeOpenApiProcessor) {
    if (openApiPojo.getSchema().getProperties() != null) {
      final NewPojoProcessResult pojoProcessResult =
          processObjectSchema(openApiPojo.getPojoName(), openApiPojo.getSchema());
      return Optional.of(
          processPojoProcessResult(pojoProcessResult, pojoSettings, completeOpenApiProcessor));
    } else {
      return Optional.empty();
    }
  }

  private NewPojoProcessResult processObjectSchema(PojoName pojoName, Schema<?> schema) {

    final PList<NewPojoMemberProcessResult> pojoMemberAndOpenApiPojos =
        Optional.ofNullable(schema.getProperties())
            .map(properties -> PList.fromIter(properties.entrySet()))
            .orElseThrow(
                () -> new IllegalArgumentException("Object schema without properties: " + schema))
            .map(entry -> processObjectSchemaEntry(entry, pojoName, schema));

    final NewPojo pojo =
        ObjectPojo.of(
            pojoName,
            schema.getDescription(),
            pojoMemberAndOpenApiPojos.map(NewPojoMemberProcessResult::getPojoMember));

    final PList<OpenApiPojo> openApiPojos =
        pojoMemberAndOpenApiPojos.flatMap(NewPojoMemberProcessResult::getOpenApiPojos);

    return new NewPojoProcessResult(pojo, openApiPojos);
  }

  private NewPojoMemberProcessResult processObjectSchemaEntry(
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
        pojoName, Name.of(entry.getKey()), entry.getValue(), necessity, nullability);
  }

  private NewPojoMemberProcessResult toPojoMemberFromSchema(
      PojoName pojoName,
      Name pojoMemberName,
      Schema<?> schema,
      Necessity necessity,
      Nullability nullability) {
    final TypeMapResult result = COMPLETE_MAPPER.mapThrowing(pojoName, pojoMemberName, schema);

    final NewType type = result.getType();

    final NewPojoMember pojoMember =
        new NewPojoMember(pojoMemberName, schema.getDescription(), type, necessity, nullability);
    return new NewPojoMemberProcessResult(pojoMember, result.getOpenApiPojos());
  }
}
