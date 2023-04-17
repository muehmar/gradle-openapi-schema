package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.PojoType;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.PojoMembers;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PropertyScope;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class JavaObjectPojoTest {

  @Test
  void wrap_when_objectPojosWithAllPropertiesDefaultScope_then_singlePojoWithTypeDefaultCreated() {
    final PojoMember pojoMember = PojoMembers.requiredString(PropertyScope.DEFAULT);
    final ObjectPojo objectPojo =
        ObjectPojo.of(
            PojoName.ofNameAndSuffix("Object", "Dto"),
            "Description",
            PList.single(pojoMember),
            Constraints.empty());

    final NonEmptyList<JavaObjectPojo> javaObjectPojos =
        JavaObjectPojo.wrap(objectPojo, TypeMappings.empty());

    assertEquals(1, javaObjectPojos.size());
    assertEquals(PojoType.DEFAULT, javaObjectPojos.head().getType());
    assertEquals("ObjectDto", javaObjectPojos.head().getClassName().asString());
  }

  @Test
  void
      wrap_when_objectPojosWithNonDefaultPropertyScopes_then_threePojoTypesCreatedWithCorrectMembers() {
    final PojoMember pojoMember1 = PojoMembers.requiredString(PropertyScope.READ_ONLY);
    final PojoMember pojoMember2 = PojoMembers.requiredBirthdate(PropertyScope.WRITE_ONLY);
    final ObjectPojo objectPojo =
        ObjectPojo.of(
            PojoName.ofNameAndSuffix("Object", "Dto"),
            "Description",
            PList.of(pojoMember1, pojoMember2),
            Constraints.empty());

    final NonEmptyList<JavaObjectPojo> javaObjectPojos =
        JavaObjectPojo.wrap(objectPojo, TypeMappings.empty());

    assertEquals(3, javaObjectPojos.size());

    final Optional<JavaObjectPojo> defaultPojo =
        javaObjectPojos.toPList().find(pojo -> pojo.getType().equals(PojoType.DEFAULT));
    final Optional<JavaObjectPojo> responsePojo =
        javaObjectPojos.toPList().find(pojo -> pojo.getType().equals(PojoType.RESPONSE));
    final Optional<JavaObjectPojo> requestPojo =
        javaObjectPojos.toPList().find(pojo -> pojo.getType().equals(PojoType.REQUEST));

    assertTrue(defaultPojo.isPresent());
    assertTrue(requestPojo.isPresent());
    assertTrue(responsePojo.isPresent());

    assertEquals(2, defaultPojo.get().getMembers().size());
    assertEquals(1, requestPojo.get().getMembers().size());
    assertEquals(1, responsePojo.get().getMembers().size());
    assertEquals(pojoMember1.getName(), responsePojo.get().getMembers().head().getName().asName());
    assertEquals(pojoMember2.getName(), requestPojo.get().getMembers().head().getName().asName());

    assertEquals("ObjectDto", defaultPojo.get().getClassName().asString());
    assertEquals("ObjectResponseDto", responsePojo.get().getClassName().asString());
    assertEquals("ObjectRequestDto", requestPojo.get().getClassName().asString());
  }
}
