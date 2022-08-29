package com.github.muehmar.gradle.openapi.generator.mapper.typemapper;

public class CompleteTypeMapper {
  private CompleteTypeMapper() {}

  public static TypeMapper create() {
    return new IntegerSchemaMapper()
        .or(new NumberSchemaMapper())
        .or(new BooleanSchemaMapper())
        .or(new MapSchemaMapper())
        .or(new ArraySchemaMapper())
        .or(new StringSchemaMapper())
        .or(new DateSchemaMapper())
        .or(new DateTimeSchemaMapper())
        .or(new UUIDSchemaMapper())
        .or(new ReferenceSchemaMapper())
        .or(new PasswordSchemaMapper())
        .or(new BinarySchemaMapper())
        .or(new FileSchemaMapper())
        .or(new EmailSchemaMapper())
        .or(new TypeLessSchemaMapper())
        .or(new ObjectSchemaMapper());
  }
}
