package com.github.muehmar.gradle.openapi.generator.model.pojo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.AdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.PojoMembers;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.Pojos;
import com.github.muehmar.gradle.openapi.generator.model.PropertyScope;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import org.junit.jupiter.api.Test;
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

  @ParameterizedTest
  @CsvSource({"DEFAULT, false", "READ_ONLY, true", "WRITE_ONLY, true"})
  void containsNoneDefaultPropertyScope_when_allPropertyScopesInCompositionPojo_then_expectedFlag(
      PropertyScope scope, boolean expectedFlag) {
    final PojoMember requiredString = PojoMembers.requiredString(PropertyScope.DEFAULT);
    final PojoMember requiredBirthdate = PojoMembers.requiredBirthdate(scope);
    final ObjectPojo objectPojo = Pojos.objectPojo(PList.of(requiredBirthdate, requiredString));
    final ObjectPojo anyOfPojo = Pojos.anyOfPojo(objectPojo);

    assertEquals(expectedFlag, anyOfPojo.containsNoneDefaultPropertyScope());
  }

  @Test
  void inlineObjectReference_when_referenceTypeMatches_then_referenceTypeUsed() {
    final PojoName referenceName = PojoName.ofNameAndSuffix("MemberReference", "Dto");
    final StringType referenceType = StringType.noFormat();
    final ObjectPojo objectPojo =
        Pojos.objectPojo(
            PList.of(
                PojoMembers.requiredUsername(),
                PojoMembers.ofType(ObjectType.ofName(referenceName))),
            AdditionalProperties.allowed(ObjectType.ofName(referenceName)));

    final ObjectPojo mappedPojo =
        (ObjectPojo) objectPojo.inlineObjectReference(referenceName, "description", referenceType);

    assertEquals(referenceType, mappedPojo.getMembers().apply(1).getType());
    assertEquals(referenceType, mappedPojo.getAdditionalProperties().getType());
  }

  @Test
  void inlineObjectReference_when_referenceTypeDoesNotMatch_then_originalTypeUsed() {
    final PojoName referenceName = PojoName.ofNameAndSuffix("MemberReference", "Dto");
    final StringType referenceType = StringType.noFormat();
    final ObjectType originalType = ObjectType.ofName(PojoName.ofNameAndSuffix("Object", "Dto"));
    final ObjectPojo objectPojo =
        Pojos.objectPojo(
            PList.of(PojoMembers.requiredUsername(), PojoMembers.ofType(originalType)),
            AdditionalProperties.allowed(originalType));

    final ObjectPojo mappedPojo =
        (ObjectPojo) objectPojo.inlineObjectReference(referenceName, "description", referenceType);

    assertEquals(originalType, mappedPojo.getMembers().apply(1).getType());
    assertEquals(originalType, mappedPojo.getAdditionalProperties().getType());
  }
}
