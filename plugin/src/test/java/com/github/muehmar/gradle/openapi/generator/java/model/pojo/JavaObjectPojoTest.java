package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.optionalString;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredEmail;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredInteger;
import static com.github.muehmar.gradle.openapi.generator.java.model.name.JavaPojoNames.fromNameAndSuffix;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.objectPojo;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.oneOfPojo;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo2;
import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes.stringType;
import static com.github.muehmar.gradle.openapi.generator.model.AdditionalProperties.anyTypeAllowed;
import static com.github.muehmar.gradle.openapi.generator.model.name.ComponentNames.componentName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.exception.OpenApiGeneratorException;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.PojoType;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.PojoMembers;
import com.github.muehmar.gradle.openapi.generator.model.PropertyScope;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.name.SchemaName;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojoBuilder;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class JavaObjectPojoTest {

  @Test
  void create_when_pojosHaveMembersWithSameNameButDifferentJavaType_then_throwsException() {
    final JavaPojoMember member = requiredInteger();
    final JavaObjectPojo pojo1 = JavaPojos.objectPojo(PList.single(member));
    final JavaObjectPojo pojo2 = JavaPojos.objectPojo(member.withJavaType(JavaTypes.stringType()));
    final JavaObjectPojoBuilder.Builder builder =
        JavaObjectPojoBuilder.create()
            .name(fromNameAndSuffix("Object", "Dto"))
            .schemaName(SchemaName.ofString("Object"))
            .description("")
            .members(JavaPojoMembers.fromList(PList.of(requiredEmail())))
            .type(PojoType.DEFAULT)
            .requiredAdditionalProperties(PList.empty())
            .additionalProperties(JavaAdditionalProperties.anyTypeAllowed())
            .constraints(Constraints.empty())
            .andOptionals()
            .anyOfComposition(JavaAnyOfComposition.fromPojos(NonEmptyList.of(pojo1, pojo2)));

    assertThrows(OpenApiGeneratorException.class, builder::build);
  }

  @Test
  void getAllMembers_when_pojosHaveSameMembers_then_onlyDistinctMembersReturned() {
    final JavaObjectPojo sampleObjectPojo1 = sampleObjectPojo1();
    final JavaObjectPojo sampleObjectPojo2 = sampleObjectPojo2();
    final JavaObjectPojo objectPojo = JavaPojos.anyOfPojo(sampleObjectPojo1, sampleObjectPojo2);

    final PList<JavaPojoMember> members = objectPojo.getAllMembers();

    assertEquals(
        6, sampleObjectPojo1.getAllMembers().size() + sampleObjectPojo2.getAllMembers().size());
    assertEquals(5, members.size());
    assertEquals(
        "stringVal,intVal,doubleVal,birthdate,email",
        members.map(JavaPojoMember::getName).mkString(","));
  }

  @Test
  void wrap_when_objectPojosWithAllPropertiesDefaultScope_then_singlePojoWithTypeDefaultCreated() {
    final PojoMember pojoMember = PojoMembers.requiredString(PropertyScope.DEFAULT);
    final ObjectPojo objectPojo =
        ObjectPojoBuilder.create()
            .name(componentName("Object", "Dto"))
            .description("Description")
            .members(PList.single(pojoMember))
            .requiredAdditionalProperties(PList.empty())
            .constraints(Constraints.empty())
            .additionalProperties(anyTypeAllowed())
            .build();

    final NonEmptyList<JavaObjectPojo> javaObjectPojos =
        JavaObjectPojo.wrap(objectPojo, TypeMappings.empty())
            .asList()
            .map(JavaObjectPojo.class::cast);

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
        ObjectPojoBuilder.create()
            .name(componentName("Object", "Dto"))
            .description("Description")
            .members(PList.of(pojoMember1, pojoMember2))
            .requiredAdditionalProperties(PList.empty())
            .constraints(Constraints.empty())
            .additionalProperties(anyTypeAllowed())
            .build();

    final NonEmptyList<JavaObjectPojo> javaObjectPojos =
        JavaObjectPojo.wrap(objectPojo, TypeMappings.empty())
            .asList()
            .map(JavaObjectPojo.class::cast);

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
    assertEquals(
        pojoMember1.getName(), responsePojo.get().getMembers().head().getName().getOriginalName());
    assertEquals(
        pojoMember2.getName(), requestPojo.get().getMembers().head().getName().getOriginalName());

    assertEquals("ObjectDto", defaultPojo.get().getClassName().asString());
    assertEquals("ObjectResponseDto", responsePojo.get().getClassName().asString());
    assertEquals("ObjectRequestDto", requestPojo.get().getClassName().asString());
  }

  @Test
  void
      getComposedMembers_when_composedPojoWithSameMemberInDifferentPojo_then_stringValMemberReturnedOnlyOnce() {
    final JavaObjectPojo pojo =
        JavaPojos.anyOfPojo(sampleObjectPojo1())
            .withOneOfComposition(
                Optional.of(JavaOneOfComposition.fromPojos(NonEmptyList.of(sampleObjectPojo2()))));

    final PList<JavaPojoMember> composedMembers = pojo.getComposedMembers();

    assertEquals(
        PList.of("birthdate", "doubleVal", "email", "intVal", "stringVal"),
        composedMembers
            .map(JavaPojoMember::getName)
            .map(JavaName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  @Test
  void getAllMembers_when_nestedComposedPojo_then_correctOuterClassUsed() {
    final JavaObjectPojo oneOfPojoWithEnum =
        oneOfPojo(
            objectPojo(TestJavaPojoMembers.requiredColorEnum())
                .withName(fromNameAndSuffix("ColorPojo", "Dto")));

    final JavaObjectPojo pojo =
        JavaPojos.anyOfPojo(oneOfPojoWithEnum)
            .withMembers(
                JavaPojoMembers.fromList(
                    PList.single(TestJavaPojoMembers.requiredDirectionEnum())));

    final PList<JavaPojoMember> members = pojo.getAllMembers();

    assertEquals(2, members.size());
    assertEquals(
        PList.of("Direction", "ColorPojoDto.Color"),
        members.map(m -> m.getJavaType().getParameterizedClassName().asString()));
  }

  @Test
  void getRequiredPropertyCount_when_noRequiredAdditionalProperties_then_requiredMemberCount() {
    final JavaObjectPojo javaObjectPojo =
        objectPojo(requiredEmail(), requiredInteger(), optionalString());

    assertEquals(2, javaObjectPojo.getRequiredMemberCount());
  }

  @Test
  void
      getRequiredPropertyCount_when_hasRequiredAdditionalProperties_then_countIncludesRequiredAdditionalProperties() {
    final JavaRequiredAdditionalProperty requiredAdditionalProperty =
        new JavaRequiredAdditionalProperty(JavaName.fromString("addProp"), stringType());
    final JavaObjectPojo noRequiredAdditionalProperties =
        objectPojo(requiredEmail(), requiredInteger(), optionalString());
    final JavaObjectPojo javaObjectPojo =
        noRequiredAdditionalProperties.withRequiredAdditionalProperties(
            PList.single(requiredAdditionalProperty));

    assertEquals(3, javaObjectPojo.getRequiredMemberCount());
  }
}
