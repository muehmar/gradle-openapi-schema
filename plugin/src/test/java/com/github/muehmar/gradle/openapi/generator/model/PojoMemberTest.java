package com.github.muehmar.gradle.openapi.generator.model;

import static com.github.muehmar.gradle.openapi.generator.model.name.PojoNames.pojoName;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StandardObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import org.junit.jupiter.api.Test;

class PojoMemberTest {

  @Test
  void inlineObjectReference_when_nameMatchesObjectReferenceType_then_referenceTypeUsed() {
    final PojoName referenceName = pojoName("MemberReference", "Dto");
    final StringType referenceType = StringType.noFormat();
    final PojoMember pojoMember = PojoMembers.ofType(StandardObjectType.ofName(referenceName));

    final PojoMember inlinedMember =
        pojoMember.inlineObjectReference(referenceName, "", referenceType);

    assertEquals(referenceType, inlinedMember.getType());
  }

  @Test
  void inlineObjectReference_when_nameDoesNotMatchObjectReferenceType_then_originalTypeUsed() {
    final PojoName referenceName = pojoName("MemberReference", "Dto");
    final StringType referenceType = StringType.noFormat();
    final ObjectType originalType = StandardObjectType.ofName(pojoName("Object", "Dto"));
    final PojoMember pojoMember = PojoMembers.ofType(originalType);

    final PojoMember inlinedMember =
        pojoMember.inlineObjectReference(referenceName, "", referenceType);

    assertEquals(originalType, inlinedMember.getType());
  }

  @Test
  void applyMapping_when_called_then_nameMappedCorrectly() {
    final PojoMember pojoMember =
        PojoMembers.ofType(StandardObjectType.ofName(pojoName("Object", "Dto")));

    final PojoMember pojoMemberMapped =
        pojoMember.applyMapping(name -> name.appendToName("Mapped"));

    assertEquals(
        "ObjectMappedDto",
        pojoMemberMapped
            .getType()
            .asObjectType()
            .map(ObjectType::getName)
            .map(PojoName::asString)
            .orElse(""));
  }
}
