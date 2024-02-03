package com.github.muehmar.gradle.openapi.generator.model.schema;

import com.github.muehmar.gradle.openapi.generator.mapper.ConstraintsMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.mapper.UnresolvedMapResult;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
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
  public MapContext mapToPojo(ComponentName componentName) {
    final MemberSchemaMapResult memberSchemaMapResult =
        getItemSchema().mapToMemberType(componentName, Name.ofString("value"));

    final ArrayPojo pojo =
        ArrayPojo.of(
            componentName,
            getDescription(),
            memberSchemaMapResult.getType(),
            getArrayConstraints());

    return MapContext.fromUnmappedItemsAndResult(
        memberSchemaMapResult.getUnmappedItems(), UnresolvedMapResult.ofPojo(pojo));
  }

  @Override
  public MemberSchemaMapResult mapToMemberType(ComponentName parentComponentName, Name memberName) {
    final Nullability nullability = Nullability.fromNullableBoolean(isNullable());
    if (getItemSchema() instanceof ObjectSchema) {
      final ComponentName memberSchemaName = parentComponentName.deriveMemberSchemaName(memberName);
      final ObjectType itemType = ObjectType.ofName(memberSchemaName.getPojoName());
      final ArrayType arrayType =
          ArrayType.ofItemType(itemType, nullability).withConstraints(getArrayConstraints());
      final PojoSchema pojoSchema = new PojoSchema(memberSchemaName, getItemSchema());
      return MemberSchemaMapResult.ofTypeAndPojoSchema(arrayType, pojoSchema);
    } else {
      return getItemSchema()
          .mapToMemberType(parentComponentName, memberName)
          .mapType(
              itemType ->
                  ArrayType.ofItemType(itemType, nullability)
                      .withConstraints(getArrayConstraints()));
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
