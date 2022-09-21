package com.github.muehmar.gradle.openapi.generator.mapper.memberschema;

public class CompleteMemberSchemaMapperFactory {
  private CompleteMemberSchemaMapperFactory() {}

  public static CompleteMemberSchemaMapper create() {
    final MemberSchemaMapper chain =
        new IntegerSchemaMapper()
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
            .or(new MemberSchemaLessSchemaMapper())
            .or(new ObjectSchemaMapper());

    return CompleteMemberSchemaMapperImpl.ofChain(chain);
  }
}
