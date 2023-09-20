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
import java.util.Optional;
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
  void inlineObjectReference_when_referenceTypeMatchesMember_then_referenceTypeUsed() {
    final PojoName referenceName = PojoName.ofNameAndSuffix("MemberReference", "Dto");
    final StringType referenceType = StringType.noFormat();
    final ObjectPojo objectPojo =
        Pojos.objectPojo(
            PList.of(
                PojoMembers.requiredUsername(),
                PojoMembers.ofType(ObjectType.ofName(referenceName))));

    final ObjectPojo mappedPojo =
        objectPojo.inlineObjectReference(referenceName, "description", referenceType);

    assertEquals(PojoMembers.requiredUsername(), mappedPojo.getMembers().apply(0));
    assertEquals(referenceType, mappedPojo.getMembers().apply(1).getType());
  }

  @Test
  void inlineObjectReference_when_referenceTypeDoesNotMatchMember_then_pojoUnchanged() {
    final PojoName referenceName = PojoName.ofNameAndSuffix("MemberReference", "Dto");
    final StringType referenceType = StringType.noFormat();
    final ObjectType originalType = ObjectType.ofName(PojoName.ofNameAndSuffix("Object", "Dto"));
    final ObjectPojo objectPojo =
        Pojos.objectPojo(
            PList.of(PojoMembers.requiredUsername(), PojoMembers.ofType(originalType)));

    final ObjectPojo mappedPojo =
        objectPojo.inlineObjectReference(referenceName, "description", referenceType);

    assertEquals(objectPojo, mappedPojo);
  }

  @Test
  void inlineObjectReference_when_referenceTypeMatchesAdditionalProperty_then_referenceTypeUsed() {
    final PojoName referenceName = PojoName.ofNameAndSuffix("MemberReference", "Dto");
    final StringType referenceType = StringType.noFormat();
    final ObjectPojo objectPojo =
        Pojos.objectPojo(
            PList.of(PojoMembers.requiredUsername()),
            AdditionalProperties.allowed(ObjectType.ofName(referenceName)));

    final ObjectPojo mappedPojo =
        objectPojo.inlineObjectReference(referenceName, "description", referenceType);

    assertEquals(PojoMembers.requiredUsername(), mappedPojo.getMembers().apply(0));
    assertEquals(referenceType, mappedPojo.getAdditionalProperties().getType());
  }

  @Test
  void inlineObjectReference_when_referenceTypeDoesNotMatchAdditionalProperty_then_pojoUnchanged() {
    final PojoName referenceName = PojoName.ofNameAndSuffix("MemberReference", "Dto");
    final StringType referenceType = StringType.noFormat();
    final ObjectType originalType = ObjectType.ofName(PojoName.ofNameAndSuffix("Object", "Dto"));
    final ObjectPojo objectPojo =
        Pojos.objectPojo(
            PList.of(PojoMembers.requiredUsername()), AdditionalProperties.allowed(originalType));

    final ObjectPojo mappedPojo =
        objectPojo.inlineObjectReference(referenceName, "description", referenceType);

    assertEquals(objectPojo, mappedPojo);
  }

  @Test
  void inlineObjectReference_when_referenceTypeMatchesAllOfMember_then_referenceTypeUsed() {
    final PojoName referenceName = PojoName.ofNameAndSuffix("MemberReference", "Dto");
    final StringType referenceType = StringType.noFormat();
    final ObjectPojo objectPojo =
        Pojos.objectPojo(
            PList.of(PojoMembers.requiredUsername(), PojoMembers.ofType(referenceType)));
    final ObjectPojo allOfPojo = Pojos.allOfPojo(objectPojo);

    final ObjectPojo mappedPojo =
        allOfPojo.inlineObjectReference(referenceName, "description", referenceType);

    assertEquals(
        Optional.of(referenceType),
        mappedPojo
            .getAllOfComposition()
            .flatMap(comp -> comp.getPojos().head().asObjectPojo())
            .map(pojo -> pojo.getMembers().apply(1).getType()));
  }

  @Test
  void inlineObjectReference_when_referenceTypeDoesNotMatchAllOfMember_then_pojoUnchanged() {
    final PojoName referenceName = PojoName.ofNameAndSuffix("MemberReference", "Dto");
    final StringType referenceType = StringType.noFormat();
    final ObjectType originalType = ObjectType.ofName(PojoName.ofNameAndSuffix("Object", "Dto"));
    final ObjectPojo objectPojo =
        Pojos.objectPojo(
            PList.of(PojoMembers.requiredUsername(), PojoMembers.ofType(originalType)));
    final ObjectPojo allOfPojo = Pojos.allOfPojo(objectPojo);

    final ObjectPojo mappedPojo =
        allOfPojo.inlineObjectReference(referenceName, "description", referenceType);

    assertEquals(allOfPojo, mappedPojo);
  }

  @Test
  void inlineObjectReference_when_referenceTypeMatchesOneOfMember_then_referenceTypeUsed() {
    final PojoName referenceName = PojoName.ofNameAndSuffix("MemberReference", "Dto");
    final StringType referenceType = StringType.noFormat();
    final ObjectPojo objectPojo =
        Pojos.objectPojo(
            PList.of(PojoMembers.requiredUsername(), PojoMembers.ofType(referenceType)));
    final ObjectPojo oneOfPojo = Pojos.oneOfPojo(objectPojo);

    final ObjectPojo mappedPojo =
        oneOfPojo.inlineObjectReference(referenceName, "description", referenceType);

    assertEquals(
        Optional.of(referenceType),
        mappedPojo
            .getOneOfComposition()
            .flatMap(comp -> comp.getPojos().head().asObjectPojo())
            .map(pojo -> pojo.getMembers().apply(1).getType()));
  }

  @Test
  void inlineObjectReference_when_referenceTypeDoesNotMatchOneOfMember_then_pojoUnchanged() {
    final PojoName referenceName = PojoName.ofNameAndSuffix("MemberReference", "Dto");
    final StringType referenceType = StringType.noFormat();
    final ObjectType originalType = ObjectType.ofName(PojoName.ofNameAndSuffix("Object", "Dto"));
    final ObjectPojo objectPojo =
        Pojos.objectPojo(
            PList.of(PojoMembers.requiredUsername(), PojoMembers.ofType(originalType)));
    final ObjectPojo oneOfPojo = Pojos.oneOfPojo(objectPojo);

    final ObjectPojo mappedPojo =
        oneOfPojo.inlineObjectReference(referenceName, "description", referenceType);

    assertEquals(oneOfPojo, mappedPojo);
  }

  @Test
  void inlineObjectReference_when_referenceTypeMatchesAnyOfMember_then_referenceTypeUsed() {
    final PojoName referenceName = PojoName.ofNameAndSuffix("MemberReference", "Dto");
    final StringType referenceType = StringType.noFormat();
    final ObjectPojo objectPojo =
        Pojos.objectPojo(
            PList.of(PojoMembers.requiredUsername(), PojoMembers.ofType(referenceType)));
    final ObjectPojo anyOfPojo = Pojos.anyOfPojo(objectPojo);

    final ObjectPojo mappedPojo =
        anyOfPojo.inlineObjectReference(referenceName, "description", referenceType);

    assertEquals(
        Optional.of(referenceType),
        mappedPojo
            .getAnyOfComposition()
            .flatMap(comp -> comp.getPojos().head().asObjectPojo())
            .map(pojo -> pojo.getMembers().apply(1).getType()));
  }

  @Test
  void inlineObjectReference_when_referenceTypeDoesNotMatchAnyOfMember_then_pojoUnchanged() {
    final PojoName referenceName = PojoName.ofNameAndSuffix("MemberReference", "Dto");
    final StringType referenceType = StringType.noFormat();
    final ObjectType originalType = ObjectType.ofName(PojoName.ofNameAndSuffix("Object", "Dto"));
    final ObjectPojo objectPojo =
        Pojos.objectPojo(
            PList.of(PojoMembers.requiredUsername(), PojoMembers.ofType(originalType)));
    final ObjectPojo anyOfPojo = Pojos.anyOfPojo(objectPojo);

    final ObjectPojo mappedPojo =
        anyOfPojo.inlineObjectReference(referenceName, "description", referenceType);

    assertEquals(anyOfPojo, mappedPojo);
  }
}
