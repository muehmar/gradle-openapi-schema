package com.github.muehmar.gradle.openapi.generator.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import org.junit.jupiter.api.Test;

class AdditionalPropertiesTest {
  @Test
  void inlineObjectReference_when_nameMatchesObjectReferenceType_then_referenceTypeUsed() {
    final PojoName referenceName = PojoName.ofNameAndSuffix("MemberReference", "Dto");
    final StringType referenceType = StringType.noFormat();
    final AdditionalProperties props =
        AdditionalProperties.allowed(ObjectType.ofName(referenceName));

    final AdditionalProperties inlinedReferenceProps =
        props.inlineObjectReference(referenceName, referenceType);

    assertEquals(referenceType, inlinedReferenceProps.getType());
  }

  @Test
  void inlineObjectReference_when_nameDoesNotMatchObjectReferenceType_then_originalTypeUsed() {
    final PojoName referenceName = PojoName.ofNameAndSuffix("MemberReference", "Dto");
    final StringType referenceType = StringType.noFormat();
    final ObjectType originalType = ObjectType.ofName(PojoName.ofNameAndSuffix("Object", "Dto"));
    final AdditionalProperties props = AdditionalProperties.allowed(originalType);

    final AdditionalProperties inlinedReferenceProps =
        props.inlineObjectReference(referenceName, referenceType);

    assertEquals(originalType, inlinedReferenceProps.getType());
  }
}
