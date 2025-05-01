package com.github.muehmar.gradle.openapi.generator.model.pojo;

import static com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojoBuilder.fullObjectPojoBuilder;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.*;
import com.github.muehmar.gradle.openapi.generator.model.composition.AllOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.AnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.OneOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.UntypedDiscriminator;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.settings.PojoNameMapping;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

@Value
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@PojoBuilder
@With
public class ObjectPojo implements Pojo {
  ComponentName name;
  String description;
  Nullability nullability;
  PojoXml pojoXml;
  PList<PojoMember> members;
  PList<Name> requiredAdditionalProperties;
  Optional<AllOfComposition> allOfComposition;
  Optional<OneOfComposition> oneOfComposition;
  Optional<AnyOfComposition> anyOfComposition;
  Constraints constraints;
  AdditionalProperties additionalProperties;
  Optional<UntypedDiscriminator> discriminator;

  @Override
  public ObjectPojo replaceObjectType(
      PojoName objectTypeName, String newObjectTypeDescription, Type newObjectType) {
    final PList<PojoMember> mappedMembers =
        members.map(
            member ->
                member.replaceObjectType(objectTypeName, newObjectTypeDescription, newObjectType));
    final Optional<AllOfComposition> mappedAllOfComposition =
        allOfComposition.map(
            composition ->
                composition.replaceObjectType(
                    objectTypeName, newObjectTypeDescription, newObjectType));
    final Optional<OneOfComposition> mappedOneOfComposition =
        oneOfComposition.map(
            composition ->
                composition.replaceObjectType(
                    objectTypeName, newObjectTypeDescription, newObjectType));
    final Optional<AnyOfComposition> mappedAnyOfComposition =
        anyOfComposition.map(
            composition ->
                composition.replaceObjectType(
                    objectTypeName, newObjectTypeDescription, newObjectType));
    final AdditionalProperties mappedAdditionalProperties =
        additionalProperties.replaceObjectType(objectTypeName, newObjectType);
    return fullObjectPojoBuilder()
        .name(name)
        .description(description)
        .nullability(nullability)
        .pojoXml(pojoXml)
        .members(mappedMembers)
        .requiredAdditionalProperties(requiredAdditionalProperties)
        .constraints(constraints)
        .additionalProperties(mappedAdditionalProperties)
        .allOfComposition(mappedAllOfComposition)
        .oneOfComposition(mappedOneOfComposition)
        .anyOfComposition(mappedAnyOfComposition)
        .discriminator(discriminator)
        .build();
  }

  @Override
  public Pojo adjustNullablePojo(PojoName nullablePojo) {
    final PList<PojoMember> mappedMembers =
        members.map(member -> member.withType(member.getType().adjustNullablePojo(nullablePojo)));
    final Optional<AllOfComposition> mappedAllOfComposition =
        allOfComposition.map(composition -> composition.adjustNullablePojo(nullablePojo));
    final Optional<OneOfComposition> mappedOneOfComposition =
        oneOfComposition.map(composition -> composition.adjustNullablePojo(nullablePojo));
    final Optional<AnyOfComposition> mappedAnyOfComposition =
        anyOfComposition.map(composition -> composition.adjustNullablePojo(nullablePojo));
    final AdditionalProperties mappedAdditionalProperties =
        additionalProperties.adjustNullablePojo(nullablePojo);
    return fullObjectPojoBuilder()
        .name(name)
        .description(description)
        .nullability(nullability)
        .pojoXml(pojoXml)
        .members(mappedMembers)
        .requiredAdditionalProperties(requiredAdditionalProperties)
        .constraints(constraints)
        .additionalProperties(mappedAdditionalProperties)
        .allOfComposition(mappedAllOfComposition)
        .oneOfComposition(mappedOneOfComposition)
        .anyOfComposition(mappedAnyOfComposition)
        .discriminator(discriminator)
        .build();
  }

  @Override
  public ObjectPojo applyMapping(PojoNameMapping pojoNameMapping) {
    final PList<PojoMember> mappedMembers =
        members.map(member -> member.applyMapping(pojoNameMapping));
    final Optional<AllOfComposition> mappedAllOfComposition =
        allOfComposition.map(composition -> composition.applyMapping(pojoNameMapping));
    final Optional<OneOfComposition> mappedOneOfComposition =
        oneOfComposition.map(composition -> composition.applyMapping(pojoNameMapping));
    final Optional<AnyOfComposition> mappedAnyOfComposition =
        anyOfComposition.map(composition -> composition.applyMapping(pojoNameMapping));
    final AdditionalProperties mappedAdditionalProperties =
        additionalProperties.applyMapping(pojoNameMapping);
    return fullObjectPojoBuilder()
        .name(name.applyPojoMapping(pojoNameMapping))
        .description(description)
        .nullability(nullability)
        .pojoXml(pojoXml)
        .members(mappedMembers)
        .requiredAdditionalProperties(requiredAdditionalProperties)
        .constraints(constraints)
        .additionalProperties(mappedAdditionalProperties)
        .allOfComposition(mappedAllOfComposition)
        .oneOfComposition(mappedOneOfComposition)
        .anyOfComposition(mappedAnyOfComposition)
        .discriminator(discriminator)
        .build();
  }

  private ObjectPojo mapMembers(UnaryOperator<PojoMember> map) {
    return withMembers(members.map(map));
  }

  @Override
  public <T> T fold(
      Function<ObjectPojo, T> onObjectPojo,
      Function<ArrayPojo, T> onArrayType,
      Function<EnumPojo, T> onEnumPojo) {
    return onObjectPojo.apply(this);
  }

  public boolean containsNoneDefaultPropertyScope() {
    return members.exists(member -> not(member.isDefaultScope()))
        || containsNonDefaultPropertyScope(allOfComposition.map(AllOfComposition::getPojos))
        || containsNonDefaultPropertyScope(oneOfComposition.map(OneOfComposition::getPojos))
        || containsNonDefaultPropertyScope(anyOfComposition.map(AnyOfComposition::getPojos));
  }

  private static boolean containsNonDefaultPropertyScope(Optional<NonEmptyList<Pojo>> pojos) {
    return pojos
        .map(NonEmptyList::toPList)
        .orElseGet(PList::empty)
        .flatMapOptional(Pojo::asObjectPojo)
        .exists(ObjectPojo::containsNoneDefaultPropertyScope);
  }
}
