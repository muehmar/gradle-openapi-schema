package com.github.muehmar.gradle.openapi.generator.model.pojo;

import static com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojoBuilder.fullObjectPojoBuilder;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.AdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.Type;
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
  PList<PojoMember> members;
  PList<Name> requiredAdditionalProperties;
  Optional<AllOfComposition> allOfComposition;
  Optional<OneOfComposition> oneOfComposition;
  Optional<AnyOfComposition> anyOfComposition;
  Constraints constraints;
  AdditionalProperties additionalProperties;
  Optional<UntypedDiscriminator> discriminator;

  @Override
  public Pojo addObjectTypeDescription(PojoName objectTypeName, String description) {
    return mapMembers(member -> member.addObjectTypeDescription(objectTypeName, description));
  }

  @Override
  public ObjectPojo inlineObjectReference(
      PojoName referenceName, String referenceDescription, Type referenceType) {
    final PList<PojoMember> mappedMembers =
        members.map(
            member ->
                member.inlineObjectReference(referenceName, referenceDescription, referenceType));
    final Optional<AllOfComposition> mappedAllOfComposition =
        allOfComposition.map(
            composition ->
                composition.inlineObjectReference(
                    referenceName, referenceDescription, referenceType));
    final Optional<OneOfComposition> mappedOneOfComposition =
        oneOfComposition.map(
            composition ->
                composition.inlineObjectReference(
                    referenceName, referenceDescription, referenceType));
    final Optional<AnyOfComposition> mappedAnyOfComposition =
        anyOfComposition.map(
            composition ->
                composition.inlineObjectReference(
                    referenceName, referenceDescription, referenceType));
    final AdditionalProperties mappedAdditionalProperties =
        additionalProperties.inlineObjectReference(referenceName, referenceType);
    return fullObjectPojoBuilder()
        .name(name)
        .description(description)
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
    return fullObjectPojoBuilder()
        .name(name)
        .description(description)
        .members(members.map(map))
        .requiredAdditionalProperties(requiredAdditionalProperties)
        .constraints(constraints)
        .additionalProperties(additionalProperties)
        .allOfComposition(allOfComposition)
        .oneOfComposition(oneOfComposition)
        .anyOfComposition(anyOfComposition)
        .discriminator(discriminator)
        .build();
  }

  @Override
  public <T> T fold(
      Function<ObjectPojo, T> onObjectPojo,
      Function<ArrayPojo, T> onArrayType,
      Function<EnumPojo, T> onEnumPojo) {
    return onObjectPojo.apply(this);
  }

  public PList<PojoMember> getMembersAndAllOfMembers() {
    return members.concat(
        allOfComposition
            .map(AllOfComposition::getPojos)
            .map(NonEmptyList::toPList)
            .orElseGet(PList::empty)
            .flatMapOptional(Pojo::asObjectPojo)
            .flatMap(ObjectPojo::getMembersAndAllOfMembers));
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
