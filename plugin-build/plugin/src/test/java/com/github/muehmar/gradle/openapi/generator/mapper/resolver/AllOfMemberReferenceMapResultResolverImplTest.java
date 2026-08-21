package com.github.muehmar.gradle.openapi.generator.mapper.resolver;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.MapResult;
import com.github.muehmar.gradle.openapi.generator.mapper.UnresolvedMapResult;
import com.github.muehmar.gradle.openapi.generator.model.*;
import com.github.muehmar.gradle.openapi.generator.model.composition.UnresolvedAllOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.type.StandardObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import java.util.Comparator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AllOfMemberReferenceMapResultResolverImplTest {

  @ParameterizedTest
  @CsvSource({
    "NOT_NULLABLE, NOT_NULLABLE, NOT_NULLABLE",
    "NOT_NULLABLE, NULLABLE, NULLABLE",
    "NULLABLE, NOT_NULLABLE, NULLABLE",
    "NULLABLE, NULLABLE, NULLABLE"
  })
  void resolve_when_allOfMemberReference_then_resolvedSuccessfully(
      Nullability memberReferenceNullability,
      Nullability allOfReferenceNullability,
      Nullability expectedFooNullability) {
    final MapResultResolver mapResultResolver = MapResultResolverImpl.create();

    final UnresolvedMapResult unresolvedMapResult =
        UnresolvedMapResult.ofPojoMemberReference(
                createMemberReferences(memberReferenceNullability))
            .merge(createUnresolvedObjectPojos(allOfReferenceNullability));

    // method call
    final MapResult mapResult =
        assertDoesNotThrow(() -> mapResultResolver.resolve(unresolvedMapResult));

    assertEquals(1, mapResult.getPojos().size());

    final Pojo barPojo = mapResult.getPojos().head();
    assertInstanceOf(ObjectPojo.class, barPojo);

    final ObjectPojo barObjectPojo = (ObjectPojo) barPojo;

    assertEquals(ComponentName.fromSchemaStringAndSuffix("Bar", "Dto"), barObjectPojo.getName());
    final PList<PojoMember> members =
        barObjectPojo
            .getMembers()
            .sort(Comparator.comparing(member -> member.getName().asString()));
    assertEquals(2, members.size());
    assertEquals(
        PList.of("birthdate", "foo"), members.map(PojoMember::getName).map(Name::asString));

    final PojoMember fooMember = members.apply(1);

    assertEquals(
        StringType.noFormat().withNullability(expectedFooNullability), fooMember.getType());
  }

  private static PojoMemberReference createMemberReferences(Nullability nullability) {
    return new PojoMemberReference(
        PojoName.ofNameAndSuffix("Foo", "Dto"),
        "Foo description",
        StringType.noFormat().withNullability(nullability));
  }

  private static UnresolvedMapResult createUnresolvedObjectPojos(Nullability nullability) {
    final UnresolvedObjectPojo barFooPojo =
        UnresolvedObjectPojoBuilder.unresolvedObjectPojoBuilder()
            .name(ComponentName.fromSchemaStringAndSuffix("BarFoo", "Dto"))
            .description("")
            .nullability(nullability)
            .pojoXml(PojoXml.noXmlDefinition())
            .members(PList.empty())
            .requiredAdditionalProperties(PList.empty())
            .constraints(Constraints.empty())
            .additionalProperties(AdditionalProperties.anyTypeAllowed())
            .andOptionals()
            .allOfComposition(
                UnresolvedAllOfComposition.fromComponentNames(
                    PList.of(ComponentName.fromSchemaStringAndSuffix("Foo", "Dto"))))
            .build();

    final PojoMember requiredBirthdate = PojoMembers.requiredBirthdate();
    final PojoMember fooMember =
        PojoMembers.ofType(StandardObjectType.ofName(PojoName.ofNameAndSuffix("BarFoo", "Dto")))
            .withName(Name.ofString("foo"));

    final UnresolvedObjectPojo barPojo =
        UnresolvedObjectPojoBuilder.unresolvedObjectPojoBuilder()
            .name(ComponentName.fromSchemaStringAndSuffix("Bar", "Dto"))
            .description("description")
            .nullability(Nullability.NOT_NULLABLE)
            .pojoXml(PojoXml.noXmlDefinition())
            .members(PList.of(requiredBirthdate, fooMember))
            .requiredAdditionalProperties(PList.empty())
            .constraints(Constraints.empty())
            .additionalProperties(AdditionalProperties.anyTypeAllowed())
            .build();

    return UnresolvedMapResult.ofUnresolvedObjectPojo(barPojo)
        .merge(UnresolvedMapResult.ofUnresolvedObjectPojo(barFooPojo));
  }
}
