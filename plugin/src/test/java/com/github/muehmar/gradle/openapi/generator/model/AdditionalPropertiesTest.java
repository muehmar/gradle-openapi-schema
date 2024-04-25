package com.github.muehmar.gradle.openapi.generator.model;

import static com.github.muehmar.gradle.openapi.generator.model.name.PojoNames.pojoName;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StandardObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import org.junit.jupiter.api.Test;

class AdditionalPropertiesTest {
  @Test
  void replaceObjectReference_when_nameMatchesObjectReferenceType_then_typeTypeUsed() {
    final PojoName referenceName = pojoName("MemberReference", "Dto");
    final StringType referenceType = StringType.noFormat();
    final AdditionalProperties props =
        AdditionalProperties.allowed(StandardObjectType.ofName(referenceName));

    final AdditionalProperties inlinedReferenceProps =
        props.replaceObjectType(referenceName, referenceType);

    assertEquals(referenceType, inlinedReferenceProps.getType());
  }

  @Test
  void replaceObjectReference_when_nameDoesNotMatchObjectTypeType_then_originalTypeUsed() {
    final PojoName referenceName = pojoName("MemberReference", "Dto");
    final StringType referenceType = StringType.noFormat();
    final ObjectType originalType = StandardObjectType.ofName(pojoName("Object", "Dto"));
    final AdditionalProperties props = AdditionalProperties.allowed(originalType);

    final AdditionalProperties inlinedReferenceProps =
        props.replaceObjectType(referenceName, referenceType);

    assertEquals(originalType, inlinedReferenceProps.getType());
  }

  @Test
  void applyMapping_when_objectType_then_nameMapped() {
    final ObjectType objectType = StandardObjectType.ofName(pojoName("Object", "Dto"));
    final AdditionalProperties props = AdditionalProperties.allowed(objectType);

    final AdditionalProperties inlinedReferenceProps =
        props.applyMapping(name -> name.appendToName("Mapped"));

    assertEquals(
        "ObjectMappedDto",
        inlinedReferenceProps
            .getType()
            .asObjectType()
            .map(ObjectType::getName)
            .map(PojoName::asString)
            .orElse(""));
  }
}
