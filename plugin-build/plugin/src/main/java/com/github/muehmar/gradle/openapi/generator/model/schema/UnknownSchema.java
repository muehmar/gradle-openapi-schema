package com.github.muehmar.gradle.openapi.generator.model.schema;

import com.github.muehmar.gradle.openapi.exception.OpenApiGeneratorException;
import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
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

  public static UnknownSchema wrap(SchemaWrapper wrapper) {
    return new UnknownSchema(wrapper.getSchema());
  }

  @Override
  public MapContext mapToPojo(ComponentName name) {
    final String message =
        String.format(
            "Not supported schema for pojo '%s': %s", name.getPojoName().getName(), delegate);
    throw new OpenApiGeneratorException(message);
  }

  @Override
  public MemberSchemaMapResult mapToMemberType(ComponentName parentComponentName, Name memberName) {
    final String message =
        String.format(
            "Not supported schema for pojo '%s' and pojo member '%s': %s",
            parentComponentName.getPojoName().asString(), memberName.asString(), delegate);
    throw new OpenApiGeneratorException(message);
  }

  @Override
  public Schema<?> getDelegateSchema() {
    return delegate;
  }
}
