package com.github.muehmar.gradle.openapi.generator.model.schema;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.ConstraintsMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.mapper.PropertyScopeMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.mapper.UnresolvedMapResult;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.PropertyScope;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.type.MemberSchema;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Map;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@EqualsAndHashCode
@ToString
public class ObjectSchema implements OpenApiSchema {
  private final Schema<?> delegate;
  private final Map<String, Schema> properties;

  private ObjectSchema(Schema<?> delegate, Map<String, Schema> properties) {
    this.delegate = delegate;
    this.properties = properties;
  }

  public static Optional<ObjectSchema> wrap(Schema<?> schema) {
    final Map<String, Schema> properties = schema.getProperties();
    if (properties != null) {
      final ObjectSchema objectSchema = new ObjectSchema(schema, properties);
      return Optional.of(objectSchema);
    }

    return Optional.empty();
  }

  @Override
  public MapContext mapToPojo(PojoName pojoName) {
    final PList<PojoMemberProcessResult> pojoMemberAndOpenApiPojos =
        getProperties().map(entry -> processObjectSchemaEntry(entry, pojoName));

    final Constraints constraints = ConstraintsMapper.getPropertyCountConstraints(delegate);

    final Pojo objectPojo =
        ObjectPojo.of(
            pojoName,
            getDescription(),
            pojoMemberAndOpenApiPojos.map(PojoMemberProcessResult::getPojoMember),
            constraints);

    final UnmappedItems unmappedItems =
        pojoMemberAndOpenApiPojos
            .map(PojoMemberProcessResult::getUnmappedItems)
            .reduce(UnmappedItems::merge)
            .orElse(UnmappedItems.empty());

    return MapContext.fromUnmappedItemsAndResult(
        unmappedItems, UnresolvedMapResult.ofPojo(objectPojo));
  }

  @Override
  public MemberSchemaMapResult mapToMemberType(PojoName pojoName, Name memberName) {
    final PojoName openApiPojoName = PojoName.deriveOpenApiPojoName(pojoName, memberName);
    final ObjectType objectType = ObjectType.ofName(openApiPojoName);
    final PojoSchema pojoSchema = new PojoSchema(openApiPojoName, this);
    return MemberSchemaMapResult.ofTypeAndPojoSchema(objectType, pojoSchema);
  }

  @Override
  public Schema<?> getDelegateSchema() {
    return delegate;
  }

  public PList<MemberSchema> getProperties() {
    return PList.fromIter(properties.entrySet())
        .map(
            entry ->
                new MemberSchema(
                    Name.ofString(entry.getKey()), OpenApiSchema.wrapSchema(entry.getValue())));
  }

  public PList<String> getRequired() {
    return Optional.ofNullable(delegate.getRequired()).map(PList::fromIter).orElseGet(PList::empty);
  }

  private PojoMemberProcessResult processObjectSchemaEntry(
      MemberSchema memberSchema, PojoName pojoName) {
    final Necessity necessity =
        Necessity.fromBoolean(getRequired().exists(memberSchema.getName().asString()::equals));

    final Nullability nullability =
        Nullability.fromNullableBoolean(memberSchema.getSchema().isNullable());

    return toPojoMemberFromSchema(
        pojoName, memberSchema.getName(), memberSchema.getSchema(), necessity, nullability);
  }

  private PojoMemberProcessResult toPojoMemberFromSchema(
      PojoName pojoName,
      Name pojoMemberName,
      OpenApiSchema schema,
      Necessity necessity,
      Nullability nullability) {
    final MemberSchemaMapResult result = schema.mapToMemberType(pojoName, pojoMemberName);
    final PropertyScope propertyScope = PropertyScopeMapper.mapScope(schema.getDelegateSchema());

    final Type type = result.getType();

    final PojoMember pojoMember =
        new PojoMember(
            pojoMemberName, schema.getDescription(), type, propertyScope, necessity, nullability);
    return new PojoMemberProcessResult(pojoMember, result.getUnmappedItems());
  }

  @Value
  private static class PojoMemberProcessResult {
    PojoMember pojoMember;
    UnmappedItems unmappedItems;
  }
}
