package com.github.muehmar.gradle.openapi.generator.mapper.pojoschema;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.memberschema.CompleteMemberSchemaMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.memberschema.CompleteMemberSchemaMapperFactory;
import com.github.muehmar.gradle.openapi.generator.mapper.memberschema.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Map;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

public class ObjectPojoSchemaMapper implements SinglePojoSchemaMapper {
  private static final CompleteMemberSchemaMapper COMPLETE_TYPE_MAPPER =
      CompleteMemberSchemaMapperFactory.create();

  @Override
  public Optional<PojoSchemaMapResult> map(
      PojoSchema pojoSchema, CompletePojoSchemaMapper completePojoSchemaMapper) {
    if (pojoSchema.getSchema().getProperties() != null) {
      final PojoSchemaMapResult pojoSchemaMapResult =
          processObjectSchema(
              pojoSchema.getPojoName(), pojoSchema.getSchema(), completePojoSchemaMapper);
      return Optional.of(pojoSchemaMapResult);
    } else {
      return Optional.empty();
    }
  }

  private PojoSchemaMapResult processObjectSchema(
      PojoName pojoName, Schema<?> schema, CompletePojoSchemaMapper completePojoSchemaMapper) {

    final PList<PojoMemberProcessResult> pojoMemberAndOpenApiPojos =
        Optional.ofNullable(schema.getProperties())
            .map(properties -> PList.fromIter(properties.entrySet()))
            .orElseThrow(
                () -> new IllegalArgumentException("Object schema without properties: " + schema))
            .map(entry -> processObjectSchemaEntry(entry, pojoName, schema));

    final Pojo objectPojo =
        ObjectPojo.of(
            pojoName,
            schema.getDescription(),
            pojoMemberAndOpenApiPojos.map(PojoMemberProcessResult::getPojoMember));

    final PList<PojoSchema> openApiPojos =
        pojoMemberAndOpenApiPojos
            .map(PojoMemberProcessResult::getMemberSchemaMapResult)
            .flatMap(MemberSchemaMapResult::getPojoSchemas);

    final PList<OpenApiSpec> remoteSpecs =
        pojoMemberAndOpenApiPojos
            .map(PojoMemberProcessResult::getMemberSchemaMapResult)
            .flatMap(MemberSchemaMapResult::getRemoteSpecs);

    return completePojoSchemaMapper
        .process(openApiPojos)
        .addPojo(objectPojo)
        .addSpecifications(remoteSpecs);
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
    final MemberSchemaMapResult result = COMPLETE_TYPE_MAPPER.map(pojoName, pojoMemberName, schema);

    final Type type = result.getType();

    final PojoMember pojoMember =
        new PojoMember(pojoMemberName, schema.getDescription(), type, necessity, nullability);
    return new PojoMemberProcessResult(pojoMember, result);
  }

  @EqualsAndHashCode
  @ToString
  private static class PojoMemberProcessResult {
    private final PojoMember pojoMember;
    private final MemberSchemaMapResult memberSchemaMapResult;

    public PojoMemberProcessResult(
        PojoMember pojoMember, MemberSchemaMapResult memberSchemaMapResult) {
      this.pojoMember = pojoMember;
      this.memberSchemaMapResult = memberSchemaMapResult;
    }

    public PojoMember getPojoMember() {
      return pojoMember;
    }

    public MemberSchemaMapResult getMemberSchemaMapResult() {
      return memberSchemaMapResult;
    }
  }
}
