package com.github.muehmar.gradle.openapi.generator.mapper;

import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import java.util.Optional;
import java.util.function.UnaryOperator;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Contains a {@link Type} of a member as well as {@link UnmappedItems} which are not yet mapped to
 * an internal representation.
 */
@EqualsAndHashCode
@ToString
public class MemberSchemaMapResult {
  private final Type type;
  private final UnmappedItems unmappedItems;

  private MemberSchemaMapResult(Type type, UnmappedItems unmappedItems) {
    this.type = type;
    this.unmappedItems = unmappedItems;
  }

  public static MemberSchemaMapResult ofTypeAndUnmappedItems(
      Type type, UnmappedItems unmappedItems) {
    return new MemberSchemaMapResult(type, unmappedItems);
  }

  public static MemberSchemaMapResult ofType(Type type) {
    return new MemberSchemaMapResult(type, UnmappedItems.empty());
  }

  public static MemberSchemaMapResult ofTypeAndPojoSchema(Type type, PojoSchema pojoSchema) {
    return new MemberSchemaMapResult(type, UnmappedItems.ofPojoSchema(pojoSchema));
  }

  public MemberSchemaMapResult mapType(UnaryOperator<Type> mapType) {
    return new MemberSchemaMapResult(mapType.apply(type), unmappedItems);
  }

  public MemberSchemaMapResult addOpenApiSpec(Optional<OpenApiSpec> spec) {
    return new MemberSchemaMapResult(type, unmappedItems.addSpecification(spec));
  }

  public Type getType() {
    return type;
  }

  public UnmappedItems getUnmappedItems() {
    return unmappedItems;
  }
}
