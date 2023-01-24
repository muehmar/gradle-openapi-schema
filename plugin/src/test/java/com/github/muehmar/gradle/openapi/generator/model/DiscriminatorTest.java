package com.github.muehmar.gradle.openapi.generator.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class DiscriminatorTest {

  private static final PojoName USER_POJO_NAME =
      PojoName.ofNameAndSuffix(Name.ofString("User"), "Dto");
  private static final PojoName CLIENT_POJO_NAME =
      PojoName.ofNameAndSuffix(Name.ofString("Client"), "Dto");
  private static final Discriminator DISCRIMINATOR =
      Discriminator.fromPropertyName(Name.ofString("prop"))
          .withMapping(Optional.of(Collections.singletonMap("-user-", USER_POJO_NAME)));

  @Test
  void getValueForPojoName_when_mappingPresent_then_returnMappingKey() {
    assertEquals("-user-", DISCRIMINATOR.getValueForPojoName(USER_POJO_NAME));
  }

  @Test
  void getValueForPojoName_when_noMappingPresent_then_returnPojoName() {
    assertEquals("Client", DISCRIMINATOR.getValueForPojoName(CLIENT_POJO_NAME));
  }
}
