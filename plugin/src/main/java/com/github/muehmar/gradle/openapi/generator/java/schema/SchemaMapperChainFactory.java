package com.github.muehmar.gradle.openapi.generator.java.schema;

import java.util.function.Function;
import java.util.stream.Stream;

public class SchemaMapperChainFactory {
  private SchemaMapperChainFactory() {}

  public static JavaSchemaMapper createChain() {
    return Stream.<Function<JavaSchemaMapper, JavaSchemaMapper>>of(
            IntegerSchemaMapper::new,
            NumberSchemaMapper::new,
            BooleanSchemaMapper::new,
            MapSchemaMapper::new,
            ArraySchemaMapper::new,
            StringSchemaMapper::new,
            DateSchemaMapper::new,
            DateTimeSchemaMapper::new,
            UUIDSchemaMapper::new,
            ReferenceSchemaMapper::new,
            PasswordSchemaMapper::new,
            BinarySchemaMapper::new,
            FileSchemaMapper::new,
            EmailSchemaMapper::new)
        .reduce(Function::compose)
        .orElseThrow(IllegalStateException::new)
        .apply(new ThrowingSchemaMapper());
  }
}
