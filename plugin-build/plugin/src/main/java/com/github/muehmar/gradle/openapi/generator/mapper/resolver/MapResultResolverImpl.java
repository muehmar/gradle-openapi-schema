package com.github.muehmar.gradle.openapi.generator.mapper.resolver;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.MapResult;
import com.github.muehmar.gradle.openapi.generator.mapper.UnresolvedMapResult;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMemberReference;
import com.github.muehmar.gradle.openapi.generator.model.UnresolvedObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.UnresolvedSchemaReference;
import com.github.muehmar.gradle.openapi.generator.model.composition.UnresolvedAllOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumObjectType;
import java.util.Optional;
import lombok.Value;

public class MapResultResolverImpl implements MapResultResolver {

  public static MapResultResolver create() {
    return new MapResultResolverImpl();
  }

  @Override
  public MapResult resolve(UnresolvedMapResult unresolvedMapResult) {
    final PList<Pojo> pojos = unresolvedMapResult.getPojos();
    final PList<PojoMemberReference> pojoMemberReferences =
        unresolvedMapResult.getPojoMemberReferences();
    final PList<UnresolvedObjectPojo> unresolvedObjectPojos =
        unresolvedMapResult.getUnresolvedObjectPojos();
    final PList<AllOfMemberReference> allOfMemberReferences =
        createAllOfMemberReferences(unresolvedObjectPojos, pojoMemberReferences);
    final PList<UnresolvedSchemaReference> unresolvedSchemaReferences =
        unresolvedMapResult.getUnresolvedSchemaReferences();
    final PList<UnresolvedObjectPojo> filteredUnresolvedObjectPojo =
        unresolvedObjectPojos.filter(
            unresolvedPojo ->
                not(
                    allOfMemberReferences.exists(
                        ref -> ref.unresolvedObjectPojo.equals(unresolvedPojo))));

    final PList<Pojo> resolvedPojos =
        Optional.of(pojos)
            .map(p -> UnresolvedObjectPojoResolver.resolve(filteredUnresolvedObjectPojo, p))
            .map(p -> inlineAllOfMemberReferences(p, allOfMemberReferences))
            .map(p -> inlineMemberReferences(p, pojoMemberReferences))
            .map(this::resolveEnumObjectPojos)
            .map(NullableRootPojoResolver::resolve)
            .map(NestedRequiredPropertyResolver::resolve)
            .map(p -> SchemaReferenceResolver.resolve(p, unresolvedSchemaReferences))
            .orElse(PList.empty());

    return MapResult.of(resolvedPojos, unresolvedMapResult.getUsedSpecs());
  }

  private PList<Pojo> inlineAllOfMemberReferences(
      PList<Pojo> pojos, PList<AllOfMemberReference> allOfMemberReferences) {
    return allOfMemberReferences.foldLeft(
        pojos,
        (p, allOfMemberReference) ->
            p.map(
                pojo ->
                    pojo.replaceObjectType(
                        allOfMemberReference.getUnresolvedObjectPojo().getName().getPojoName(),
                        allOfMemberReference.getAdjustedMemberReference().getDescription(),
                        allOfMemberReference.getAdjustedMemberReference().getType())));
  }

  private PList<Pojo> inlineMemberReferences(
      PList<Pojo> inputPojos, PList<PojoMemberReference> pojoMemberReferences) {
    return pojoMemberReferences.foldLeft(
        inputPojos,
        (pojos, memberReference) ->
            pojos.map(
                pojo ->
                    pojo.replaceObjectType(
                        memberReference.getName(),
                        memberReference.getDescription(),
                        memberReference.getType())));
  }

  private PList<Pojo> resolveEnumObjectPojos(PList<Pojo> inputPojos) {
    return inputPojos
        .flatMapOptional(Pojo::asEnumPojo)
        .foldLeft(
            inputPojos,
            (p, enumPojo) ->
                p.map(
                    pojo -> {
                      final PojoName enumName = enumPojo.getName().getPojoName();
                      final EnumObjectType enumObjectType = EnumObjectType.ofEnumPojo(enumPojo);
                      return pojo.replaceObjectType(
                          enumName, enumPojo.getDescription(), enumObjectType);
                    }));
  }

  private PList<AllOfMemberReference> createAllOfMemberReferences(
      PList<UnresolvedObjectPojo> unresolvedObjectPojos,
      PList<PojoMemberReference> memberReferences) {
    return unresolvedObjectPojos.flatMapOptional(
        unresolvedObjectPojo -> mapToAllOfMemberReference(unresolvedObjectPojo, memberReferences));
  }

  private static Optional<AllOfMemberReference> mapToAllOfMemberReference(
      UnresolvedObjectPojo unresolvedObjectPojo, PList<PojoMemberReference> memberReferences) {
    if (not(unresolvedObjectPojo.getAnyOfComposition().isPresent())
        && not(unresolvedObjectPojo.getOneOfComposition().isPresent())
        && unresolvedObjectPojo.getRequiredAdditionalProperties().nonEmpty()
        && unresolvedObjectPojo.getMembers().nonEmpty()) {
      return Optional.empty();
    }
    return unresolvedObjectPojo
        .getAllOfComposition()
        .flatMap(
            comp ->
                findMatchingMemberReferenceInComposition(
                    unresolvedObjectPojo, comp, memberReferences));
  }

  private static Optional<AllOfMemberReference> findMatchingMemberReferenceInComposition(
      UnresolvedObjectPojo unresolvedObjectPojo,
      UnresolvedAllOfComposition comp,
      PList<PojoMemberReference> memberReferences) {
    if (comp.getComponentNames().size() != 1) {
      return Optional.empty();
    }
    return comp.getComponentNames()
        .headOption()
        .flatMap(name -> memberReferences.find(ref -> ref.getName().equals(name.getPojoName())))
        .map(memberReference -> new AllOfMemberReference(unresolvedObjectPojo, memberReference));
  }

  @Value
  private static class AllOfMemberReference {
    UnresolvedObjectPojo unresolvedObjectPojo;
    PojoMemberReference memberReference;

    PojoMemberReference getAdjustedMemberReference() {
      if (unresolvedObjectPojo.getNullability().isNullable()) {
        return memberReference.makeNullable();
      } else {
        return memberReference;
      }
    }
  }
}
