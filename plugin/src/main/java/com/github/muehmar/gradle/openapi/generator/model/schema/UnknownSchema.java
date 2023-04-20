package com.github.muehmar.gradle.openapi.generator.model.schema;

import com.github.muehmar.gradle.openapi.exception.OpenApiGeneratorException;
import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import io.swagger.v3.oas.models.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class UnknownSchema implements OpenApiSchema {
  private final Schema<?> delegate;

  private UnknownSchema(Schema<?> delegate) {
    this.delegate = delegate;
  }

  public static UnknownSchema wrap(Schema<?> schema) {
    return new UnknownSchema(schema);
  }

  @Override
  public MapContext mapToPojo(PojoName pojoName) {
    final String message =
        String.format("Not supported root schema for pojo '%s': %s", pojoName.asString(), delegate);
    throw new OpenApiGeneratorException(message);
  }

  @Override
  public MemberSchemaMapResult mapToMemberType(PojoName pojoName, Name memberName) {
    final String message =
        String.format(
            "Not supported schema for pojo '%s' and pojo member '%s': %s",
            pojoName.asString(), memberName.asString(), delegate);
    throw new OpenApiGeneratorException(message);
  }

  @Override
  public Schema<?> getDelegateSchema() {
    return delegate;
  }
}
