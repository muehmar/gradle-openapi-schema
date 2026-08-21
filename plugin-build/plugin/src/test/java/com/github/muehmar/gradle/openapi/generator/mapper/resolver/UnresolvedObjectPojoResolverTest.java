package com.github.muehmar.gradle.openapi.generator.mapper.resolver;

import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.name.ComponentNames.componentName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.*;
import com.github.muehmar.gradle.openapi.generator.model.composition.AllOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.AnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.OneOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.UnresolvedAllOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.UnresolvedAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.UnresolvedOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import java.util.Comparator;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class UnresolvedObjectPojoResolverTest {

  @Test
  void resolve_when_objectPojoWithAllOfPojoName_then_resolvedToActualPojo() {
    final ObjectPojo pojo1 = Pojos.objectPojo(PList.single(PojoMembers.requiredBirthdate()));

    final UnresolvedObjectPojo unresolvedObjectPojo =
        UnresolvedObjectPojoBuilder.create()
            .name(componentName("ComposedObject", "Dto"))
            .description("Description")
            .nullability(NOT_NULLABLE)
            .pojoXml(PojoXml.noXmlDefinition())
            .members(PList.empty())
            .requiredAdditionalProperties(PList.empty())
            .constraints(Constraints.empty())
            .additionalProperties(AdditionalProperties.notAllowed())
            .andAllOptionals()
            .allOfComposition(
                UnresolvedAllOfComposition.fromComponentNames(PList.of(pojo1.getName())))
            .oneOfComposition(Optional.empty())
            .anyOfComposition(Optional.empty())
            .discriminator(Optional.empty())
            .build();

    // method call
    final PList<Pojo> resolved =
        UnresolvedObjectPojoResolver.resolve(PList.single(unresolvedObjectPojo), PList.of(pojo1))
            .sort(Comparator.comparing(pojo -> pojo.getName().getPojoName().asString()));

    assertEquals(2, resolved.size());

    assertTrue(resolved.apply(0) instanceof ObjectPojo);
    final ObjectPojo resolvedObjectPojo = (ObjectPojo) resolved.apply(0);
    assertEquals(
        Optional.of(AllOfComposition.fromPojos(NonEmptyList.single(pojo1))),
        resolvedObjectPojo.getAllOfComposition());
    assertEquals(Optional.empty(), resolvedObjectPojo.getOneOfComposition());
    assertEquals(Optional.empty(), resolvedObjectPojo.getAnyOfComposition());
    assertEquals(pojo1, resolved.apply(1));
  }

  @Test
  void resolve_when_objectPojoWithOneOfPojoName_then_resolvedToActualPojo() {
    final ObjectPojo pojo1 = Pojos.objectPojo(PList.single(PojoMembers.requiredBirthdate()));

    final UnresolvedObjectPojo unresolvedObjectPojo =
        UnresolvedObjectPojoBuilder.create()
            .name(componentName("ComposedObject", "Dto"))
            .description("Description")
            .nullability(NOT_NULLABLE)
            .pojoXml(PojoXml.noXmlDefinition())
            .members(PList.empty())
            .requiredAdditionalProperties(PList.empty())
            .constraints(Constraints.empty())
            .additionalProperties(AdditionalProperties.notAllowed())
            .andAllOptionals()
            .allOfComposition(Optional.empty())
            .oneOfComposition(
                UnresolvedOneOfComposition.fromComponentNames(PList.of(pojo1.getName())))
            .anyOfComposition(Optional.empty())
            .discriminator(Optional.empty())
            .build();

    // method call
    final PList<Pojo> resolved =
        UnresolvedObjectPojoResolver.resolve(PList.single(unresolvedObjectPojo), PList.of(pojo1))
            .sort(Comparator.comparing(pojo -> pojo.getName().getPojoName().asString()));

    assertEquals(2, resolved.size());

    assertTrue(resolved.apply(0) instanceof ObjectPojo);
    final ObjectPojo resolvedObjectPojo = (ObjectPojo) resolved.apply(0);
    assertEquals(
        Optional.of(OneOfComposition.fromPojos(NonEmptyList.single(pojo1))),
        resolvedObjectPojo.getOneOfComposition());
    assertEquals(Optional.empty(), resolvedObjectPojo.getAllOfComposition());
    assertEquals(Optional.empty(), resolvedObjectPojo.getAnyOfComposition());
    assertEquals(pojo1, resolved.apply(1));
  }

  @Test
  void resolve_when_objectPojoWithAnyOfPojoName_then_resolvedToActualPojo() {
    final ObjectPojo pojo1 = Pojos.objectPojo(PList.single(PojoMembers.requiredBirthdate()));

    final UnresolvedObjectPojo unresolvedObjectPojo =
        UnresolvedObjectPojoBuilder.create()
            .name(componentName("ComposedObject", "Dto"))
            .description("Description")
            .nullability(NOT_NULLABLE)
            .pojoXml(PojoXml.noXmlDefinition())
            .members(PList.empty())
            .requiredAdditionalProperties(PList.empty())
            .constraints(Constraints.empty())
            .additionalProperties(AdditionalProperties.notAllowed())
            .andAllOptionals()
            .allOfComposition(Optional.empty())
            .oneOfComposition(Optional.empty())
            .anyOfComposition(UnresolvedAnyOfComposition.fromPojoNames(PList.of(pojo1.getName())))
            .discriminator(Optional.empty())
            .build();

    // method call
    final PList<Pojo> resolved =
        UnresolvedObjectPojoResolver.resolve(PList.single(unresolvedObjectPojo), PList.of(pojo1))
            .sort(Comparator.comparing(pojo -> pojo.getName().getPojoName().asString()));

    assertEquals(2, resolved.size());

    assertTrue(resolved.apply(0) instanceof ObjectPojo);
    final ObjectPojo resolvedObjectPojo = (ObjectPojo) resolved.apply(0);
    assertEquals(
        Optional.of(AnyOfComposition.fromPojos(NonEmptyList.single(pojo1))),
        resolvedObjectPojo.getAnyOfComposition());
    assertEquals(Optional.empty(), resolvedObjectPojo.getAllOfComposition());
    assertEquals(Optional.empty(), resolvedObjectPojo.getOneOfComposition());
    assertEquals(pojo1, resolved.apply(1));
  }
}
