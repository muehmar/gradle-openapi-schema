package com.github.muehmar.gradle.openapi.generator.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class DiscriminatorTest {

  private static final Name USER_SCHEMA_NAME = Name.ofString("User");
  private static final Name CLIENT_SCHEMA_NAME = Name.ofString("Client");
  private static final Discriminator DISCRIMINATOR =
      Discriminator.fromPropertyName(Name.ofString("prop"))
          .withMapping(Optional.of(Collections.singletonMap("-user-", USER_SCHEMA_NAME)));

  @Test
  void getValueForPojoName_when_mappingPresent_then_returnMappingKey() {
    assertEquals("-user-", DISCRIMINATOR.getValueForSchemaName(USER_SCHEMA_NAME));
  }

  @Test
  void getValueForPojoName_when_noMappingPresent_then_returnPojoName() {
    assertEquals("Client", DISCRIMINATOR.getValueForSchemaName(CLIENT_SCHEMA_NAME));
  }
}
