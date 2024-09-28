package com.github.muehmar.gradle.openapi.generator.model;

import static com.github.muehmar.gradle.openapi.generator.model.name.PojoNames.pojoName;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.type.ArrayType;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StandardObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import org.junit.jupiter.api.Test;

class PojoMemberTest {

  @Test
  void replaceObjectReference_when_nameMatchesObjectReferenceType_then_typeTypeUsed() {
    final PojoName referenceName = pojoName("MemberReference", "Dto");
    final StringType referenceType = StringType.noFormat();
    final PojoMember pojoMember = PojoMembers.ofType(StandardObjectType.ofName(referenceName));

    final PojoMember inlinedMember = pojoMember.replaceObjectType(referenceName, "", referenceType);

    assertEquals(referenceType, inlinedMember.getType());
  }

  @Test
  void replaceObjectReference_when_nameDoesNotMatchObjectTypeType_then_originalTypeUsed() {
    final PojoName referenceName = pojoName("MemberReference", "Dto");
    final StringType referenceType = StringType.noFormat();
    final ObjectType originalType = StandardObjectType.ofName(pojoName("Object", "Dto"));
    final PojoMember pojoMember = PojoMembers.ofType(originalType);

    final PojoMember inlinedMember = pojoMember.replaceObjectType(referenceName, "", referenceType);

    assertEquals(originalType, inlinedMember.getType());
  }

  @Test
  void replaceObjectReference_when_nameMatchesArrayObjectItemType_then_typeTypeUsed() {
    final PojoName referenceName = pojoName("MemberReference", "Dto");
    final StringType referenceType = StringType.noFormat();
    final StandardObjectType itemType = StandardObjectType.ofName(referenceName);
    final ArrayType arrayType = ArrayType.ofItemType(itemType, Nullability.NOT_NULLABLE);
    final PojoMember pojoMember = PojoMembers.ofType(arrayType);

    final PojoMember mappedMember = pojoMember.replaceObjectType(referenceName, "", referenceType);

    assertEquals(
        ArrayType.ofItemType(referenceType, Nullability.NOT_NULLABLE), mappedMember.getType());
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
