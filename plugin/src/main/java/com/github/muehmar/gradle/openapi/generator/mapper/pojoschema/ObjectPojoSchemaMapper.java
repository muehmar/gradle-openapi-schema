package com.github.muehmar.gradle.openapi.generator.mapper.pojoschema;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.MapResult;
import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
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
import io.swagger.v3.oas.models.media.Schema;
import java.util.Map;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

public class ObjectPojoSchemaMapper implements SinglePojoSchemaMapper {
  private static final CompleteMemberSchemaMapper COMPLETE_TYPE_MAPPER =
      CompleteMemberSchemaMapperFactory.create();

  @Override
  public Optional<MapContext> map(PojoSchema pojoSchema) {
    if (pojoSchema.getSchema().getProperties() != null) {
      final MapContext mapContext =
          processObjectSchema(pojoSchema.getPojoName(), pojoSchema.getSchema());
      return Optional.of(mapContext);
    } else {
      return Optional.empty();
    }
  }

  private MapContext processObjectSchema(PojoName pojoName, Schema<?> schema) {

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

    final UnmappedItems unmappedItems =
        pojoMemberAndOpenApiPojos
            .map(PojoMemberProcessResult::getUnmappedItems)
            .reduce(UnmappedItems::merge)
            .orElse(UnmappedItems.empty());

    return MapContext.fromUnmappedItemsAndResult(unmappedItems, MapResult.ofPojo(objectPojo));
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
    return new PojoMemberProcessResult(pojoMember, result.getUnmappedItems());
  }

  @EqualsAndHashCode
  @ToString
  private static class PojoMemberProcessResult {
    private final PojoMember pojoMember;
    private final UnmappedItems unmappedItems;

    public PojoMemberProcessResult(PojoMember pojoMember, UnmappedItems unmappedItems) {
      this.pojoMember = pojoMember;
      this.unmappedItems = unmappedItems;
    }

    public PojoMember getPojoMember() {
      return pojoMember;
    }

    public UnmappedItems getUnmappedItems() {
      return unmappedItems;
    }
  }
}
