package com.github.muehmar.gradle.openapi.generator.model.schema;

import com.github.muehmar.gradle.openapi.generator.mapper.ConstraintsMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.mapper.UnresolvedMapResult;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ArrayPojo;
import com.github.muehmar.gradle.openapi.generator.model.type.ArrayType;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ArraySchema implements OpenApiSchema {
  private final Schema<?> delegate;
  private final OpenApiSchema itemsSchema;

  private ArraySchema(Schema<?> delegate, OpenApiSchema itemsSchema) {
    this.delegate = delegate;
    this.itemsSchema = itemsSchema;
  }

  public static Optional<ArraySchema> wrap(Schema<?> schema) {
    final Schema<?> items = schema.getItems();

    if (items != null && SchemaType.ARRAY.matchesType(schema)) {
      final ArraySchema arraySchema = new ArraySchema(schema, OpenApiSchema.wrapSchema(items));
      return Optional.of(arraySchema);
    }

    return Optional.empty();
  }

  @Override
  public MapContext mapToPojo(PojoName pojoName) {
    final MemberSchemaMapResult memberSchemaMapResult =
        getItemSchema().mapToMemberType(pojoName, Name.ofString("value"));

    final ArrayPojo pojo =
        ArrayPojo.of(
            pojoName, getDescription(), memberSchemaMapResult.getType(), getArrayConstraints());

    return MapContext.fromUnmappedItemsAndResult(
        memberSchemaMapResult.getUnmappedItems(), UnresolvedMapResult.ofPojo(pojo));
  }

  @Override
  public MemberSchemaMapResult mapToMemberType(PojoName pojoName, Name memberName) {
    if (getItemSchema() instanceof ObjectSchema) {
      final PojoName openApiPojoName = PojoName.deriveOpenApiPojoName(pojoName, memberName);
      final ObjectType itemType = ObjectType.ofName(openApiPojoName);
      final ArrayType arrayType =
          ArrayType.ofItemType(itemType).withConstraints(getArrayConstraints());
      final PojoSchema pojoSchema = new PojoSchema(openApiPojoName, getItemSchema());
      return MemberSchemaMapResult.ofTypeAndPojoSchema(arrayType, pojoSchema);
    } else {
      return getItemSchema()
          .mapToMemberType(pojoName, memberName)
          .mapType(
              itemType -> ArrayType.ofItemType(itemType).withConstraints(getArrayConstraints()));
    }
  }

  @Override
  public Schema<?> getDelegateSchema() {
    return delegate;
  }

  public OpenApiSchema getItemSchema() {
    return itemsSchema;
  }

  private Constraints getArrayConstraints() {
    return ConstraintsMapper.getMinAndMaxItems(delegate)
        .and(ConstraintsMapper.getUniqueItems(delegate));
  }
}
