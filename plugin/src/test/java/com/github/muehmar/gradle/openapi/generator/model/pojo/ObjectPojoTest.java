package com.github.muehmar.gradle.openapi.generator.model.pojo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.PojoMembers;
import com.github.muehmar.gradle.openapi.generator.model.Pojos;
import com.github.muehmar.gradle.openapi.generator.model.PropertyScope;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ObjectPojoTest {

  @ParameterizedTest
  @CsvSource({"DEFAULT, false", "READ_ONLY, true", "WRITE_ONLY, true"})
  void containsNoneDefaultPropertyScope_when_allPropertyScopes_then_expectedFlag(
      PropertyScope scope, boolean expectedFlag) {
    final PojoMember requiredString = PojoMembers.requiredString(PropertyScope.DEFAULT);
    final PojoMember requiredBirthdate = PojoMembers.requiredBirthdate(scope);
    final ObjectPojo objectPojo = Pojos.objectPojo(PList.of(requiredBirthdate, requiredString));

    assertEquals(expectedFlag, objectPojo.containsNoneDefaultPropertyScope());
  }
}
