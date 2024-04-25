package com.github.muehmar.gradle.openapi.generator.mapper.resolver;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.exception.OpenApiGeneratorException;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.composition.AllOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.type.StandardObjectType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.Value;

public class NestedRequiredPropertyResolver {
  private NestedRequiredPropertyResolver() {}

  public static PList<Pojo> resolve(PList<Pojo> pojos) {
    final PList<ObjectPojo> allObjectPojos =
        pojos.flatMapOptional(
            p -> p.fold(Optional::of, ignore -> Optional.empty(), ignore -> Optional.empty()));
    return new Resolver(pojos, allObjectPojos).resolve();
  }

  private static class Resolver {
    private final PList<Pojo> allPojos;
    private final PList<ObjectPojo> allObjectPojos;

    public Resolver(PList<Pojo> allPojos, PList<ObjectPojo> allObjectPojos) {
      this.allPojos = allPojos;
      this.allObjectPojos = allObjectPojos;
    }

    private PList<Pojo> resolve() {
      final PList<Pojo> promotedPojos =
          findRequiredPropertiesForPojos()
              .flatMap(
                  requiredPropertiesForPojo -> requiredPropertiesForPojo.promote(allObjectPojos))
              .map(p -> p);
      return allPojos.foldLeft(
          promotedPojos,
          (pojos, existingPojo) ->
              pojos.exists(p -> p.getName().equals(existingPojo.getName()))
                  ? pojos
                  : pojos.cons(existingPojo));
    }

    private PList<RequiredPropertiesForPojo> findRequiredPropertiesForPojos() {
      return allObjectPojos.flatMapOptional(
          parentPojo ->
              parentPojo
                  .getAllOfComposition()
                  .flatMap(
                      allOfComposition ->
                          allOfComposition
                              .getPojos()
                              .toPList()
                              .flatMapOptional(Pojo::asObjectPojo)
                              .flatMapOptional(
                                  pojo ->
                                      findRequiredPropertiesForAllOfPojo(
                                          parentPojo, allOfComposition, pojo))
                              .reduce(RequiredPropertiesForPojo::merge)));
    }

    private Optional<RequiredPropertiesForPojo> findRequiredPropertiesForAllOfPojo(
        ObjectPojo parentPojo, AllOfComposition allOfComposition, ObjectPojo pojo) {
      final PList<RequiredProperty> requiredProps = findRequiredProperties(pojo);
      if (requiredProps.nonEmpty()) {
        return Optional.of(
            new RequiredPropertiesForPojo(
                parentPojo, PList.single(pojo), allOfComposition, requiredProps));
      } else {
        return Optional.empty();
      }
    }

    private PList<RequiredProperty> findRequiredProperties(ObjectPojo pojo) {
      if (not(hasOnlyRequiredAdditionalProperties(pojo))) {
        return PList.empty();
      }

      return findRequiredPropertiesDeep(pojo, PList.empty());
    }

    private PList<RequiredProperty> findRequiredPropertiesDeep(
        ObjectPojo currentPojo, PList<SinglePropertyLink> currentLinkChain) {
      final boolean hasNonObjectProperties =
          currentPojo
              .getMembers()
              .toStream()
              .anyMatch(member -> not(member.getType().isObjectType()));
      if (hasNonObjectProperties) {
        return PList.empty();
      }
      if (currentLinkChain
          .reverse()
          .drop(1)
          .reverse()
          .exists(link -> link.getPojoName().equals(currentPojo.getName().getPojoName()))) {
        return PList.empty();
      }

      final PList<RequiredProperty> requiredProperties =
          currentPojo
              .getMembers()
              .flatMapOptional(member -> findRequiredPropertiesForMember(currentLinkChain, member))
              .flatMap(list -> list);

      return currentPojo
          .getRequiredAdditionalProperties()
          .concat(currentPojo.getMembers().filter(PojoMember::isRequired).map(PojoMember::getName))
          .map(
              requiredAdditionalProperty ->
                  new RequiredProperty(requiredAdditionalProperty, currentLinkChain))
          .concat(requiredProperties);
    }

    private Optional<PList<RequiredProperty>> findRequiredPropertiesForMember(
        PList<SinglePropertyLink> currentLinkChain, PojoMember member) {
      return member
          .getType()
          .asObjectType()
          .flatMap(
              objectType -> {
                final SinglePropertyLink nextLink =
                    new SinglePropertyLink(member.getName(), objectType.getName());
                return findPojoForPojoName(objectType.getName())
                    .map(
                        nextPojo ->
                            findRequiredPropertiesDeep(nextPojo, currentLinkChain.add(nextLink)));
              });
    }

    private boolean hasOnlyRequiredAdditionalProperties(ObjectPojo pojo) {
      final boolean hasNonObjectTypeProperties =
          pojo.getMembers().toStream().anyMatch(member -> not(member.getType().isObjectType()));
      if (hasNonObjectTypeProperties) {
        return false;
      }
      if (pojo.getAllOfComposition().isPresent()) {
        return false;
      }
      if (pojo.getOneOfComposition().isPresent()) {
        return false;
      }
      if (pojo.getAnyOfComposition().isPresent()) {
        return false;
      }
      if (not(pojo.getAdditionalProperties().isAllowed())
          || not(pojo.getAdditionalProperties().getType().isAnyType())) {
        return false;
      }
      return pojo.getMembers()
          .flatMapOptional(member -> member.getType().asObjectType())
          .flatMapOptional(objectType -> findPojoForPojoName(objectType.getName()))
          .forall(this::hasOnlyRequiredAdditionalProperties);
    }

    private Optional<ObjectPojo> findPojoForPojoName(PojoName pojoName) {
      return allObjectPojos.find(pojo -> pojo.getName().getPojoName().equals(pojoName));
    }
  }

  @Value
  private static class RequiredPropertiesForPojo {
    ObjectPojo parentPojo;
    PList<ObjectPojo> makeRequiredAllOfPojos;
    AllOfComposition composition;
    PList<RequiredProperty> requiredProperties;

    RequiredPropertiesForPojo merge(RequiredPropertiesForPojo other) {
      return new RequiredPropertiesForPojo(
          parentPojo,
          makeRequiredAllOfPojos.concat(other.makeRequiredAllOfPojos),
          composition,
          requiredProperties.concat(requiredProperties));
    }

    PList<PojoNode> toNodes(PList<ObjectPojo> allObjectPojos) {
      return composition
          .getPojos()
          .toPList()
          .flatMapOptional(Pojo::asObjectPojo)
          .filter(p -> not(makeRequiredAllOfPojos.exists(p::equals)))
          .flatMapOptional(
              allOfPojo ->
                  requiredProperties
                      .flatMap(rp -> rp.toNodeFor(allOfPojo, allObjectPojos))
                      .reduce(PojoNode::merge));
    }

    PList<ObjectPojo> promote(PList<ObjectPojo> allObjectPojos) {
      final PList<PojoNode> nodes = toNodes(allObjectPojos);
      final PList<ObjectPojo> objectPojos = promoteNodes(allObjectPojos, nodes);
      final PList<Pojo> newAllOfPojos = createNewAllOfPojos(nodes, objectPojos);

      if (nodes.isEmpty()) {
        return PList.empty();
      }

      final ObjectPojo promotedParentPojo =
          NonEmptyList.fromIter(newAllOfPojos)
              .map(AllOfComposition::fromPojos)
              .map(Optional::of)
              .map(parentPojo::withAllOfComposition)
              .orElse(parentPojo);
      return objectPojos.add(promotedParentPojo);
    }

    private PList<Pojo> createNewAllOfPojos(PList<PojoNode> nodes, PList<ObjectPojo> objectPojos) {
      return composition
          .getPojos()
          .toPList()
          .filter(p -> not(isMakeRequiredAllOfPojo(p)))
          .map(
              p -> {
                if (nodes.exists(n -> n.getPojoName().equals(p.getName().getPojoName()))) {
                  final Function<ComponentName, ComponentName> mapName =
                      name -> mapComponentName(parentPojo.getName().getPojoName(), name);
                  return objectPojos
                      .find(promoted -> promoted.getName().equals(mapName.apply(p.getName())))
                      .<Pojo>map(pj -> pj)
                      .orElse(p);
                } else {
                  return p;
                }
              });
    }

    private PList<ObjectPojo> promoteNodes(
        PList<ObjectPojo> allObjectPojos, PList<PojoNode> nodes) {
      return nodes.flatMap(
          node ->
              node.promote(
                  parentPojo.getName().getPojoName(),
                  allObjectPojos.filter(
                      p ->
                          not(
                              makeRequiredAllOfPojos
                                  .map(ObjectPojo::getName)
                                  .exists(p.getName()::equals)))));
    }

    private boolean isMakeRequiredAllOfPojo(Pojo p) {
      return makeRequiredAllOfPojos.map(ObjectPojo::getName).exists(p.getName()::equals);
    }
  }

  @Value
  private static class RequiredProperty {
    Name propertyName;
    PList<SinglePropertyLink> linkChain;

    RequiredProperty removeFirstLink() {
      return new RequiredProperty(propertyName, linkChain.drop(1));
    }

    PList<PojoNode> toNodeFor(ObjectPojo pojo, PList<ObjectPojo> allObjectPojos) {
      if (linkChain.isEmpty()) {
        final boolean hasMember = hasMemberOrAllOfMemberDeep(pojo, allObjectPojos);
        return hasMember
            ? PList.of(
                new PojoNode(
                    pojo.getName().getPojoName(),
                    PList.single(propertyName),
                    Collections.emptyMap()))
            : PList.empty();
      } else {
        return linkChain
            .headOption()
            .map(
                link ->
                    link.resolveLink(pojo, allObjectPojos)
                        .flatMap(
                            nextPojo ->
                                removeFirstLink()
                                    .toNodeFor(nextPojo, allObjectPojos)
                                    .map(
                                        childNode ->
                                            new PojoNode(
                                                pojo.getName().getPojoName(),
                                                PList.empty(),
                                                Collections.singletonMap(
                                                    link.propertyName, childNode)))))
            .orElse(PList.empty());
      }
    }

    boolean hasMemberOrAllOfMemberDeep(ObjectPojo pojo, PList<ObjectPojo> allObjectPojos) {
      if (pojo.getMembers().exists(pojoMember -> pojoMember.getName().equals(propertyName))) {
        return true;
      }
      return pojo.getAllOfComposition()
          .map(
              allOfComposition ->
                  allOfComposition
                      .getPojos()
                      .toPList()
                      .flatMapOptional(Pojo::asObjectPojo)
                      .exists(
                          allOfSubPojo -> hasMemberOrAllOfMemberDeep(allOfSubPojo, allObjectPojos)))
          .orElse(false);
    }
  }

  @Value
  private static class SinglePropertyLink {
    Name propertyName;
    PojoName pojoName;

    private PList<ObjectPojo> resolveLink(
        ObjectPojo currentPojo, PList<ObjectPojo> allObjectPojos) {
      final Optional<ObjectPojo> memberPojo =
          currentPojo
              .getMembers()
              .find(member -> member.getName().equals(propertyName))
              .flatMap(member -> member.getType().asObjectType())
              .flatMap(
                  objectType ->
                      allObjectPojos.find(
                          p -> p.getName().getPojoName().equals(objectType.getName())));
      final PList<ObjectPojo> allOfPojos =
          currentPojo
              .getAllOfComposition()
              .map(allOfComposition -> allOfComposition.getPojos().toPList())
              .orElse(PList.empty())
              .flatMapOptional(Pojo::asObjectPojo)
              .flatMap(pojo -> resolveLink(pojo, allObjectPojos));
      return allOfPojos.concat(PList.fromOptional(memberPojo));
    }
  }

  @Value
  private static class PojoNode {
    PojoName pojoName;
    PList<Name> requiredProperties;
    Map<Name, PojoNode> children;

    PojoNode merge(PojoNode other) {
      final HashMap<Name, PojoNode> newChildren = new HashMap<>(children);
      other.children.forEach((name, child) -> newChildren.merge(name, child, PojoNode::merge));
      return new PojoNode(
          pojoName,
          requiredProperties.concat(other.requiredProperties).distinct(Function.identity()),
          newChildren);
    }

    PList<ObjectPojo> promote(PojoName rootPojoName, PList<ObjectPojo> allObjectPojos) {
      return allObjectPojos
          .find(pojo -> pojo.getName().getPojoName().equals(pojoName))
          .map(pojo -> promotePojo(rootPojoName, pojo, allObjectPojos))
          .orElseThrow(
              () ->
                  new OpenApiGeneratorException(
                      "Error while resolving nested properties: "
                          + pojoName
                          + " not found. This is most likely an internal error, please report an issue."))
          .merge();
    }

    private PojoNodePromotionResult promotePojo(
        PojoName rootPojoName, ObjectPojo pojo, PList<ObjectPojo> allObjectPojos) {
      final PList<PojoMember> members =
          pojo.getMembers()
              .map(this::promoteMember)
              .map(m -> renameObjectTypeNames(rootPojoName, m));
      final AllOfNodePromotionResult allOfNodePromotionResult =
          promoteAllOfComposition(rootPojoName, pojo.getAllOfComposition(), allObjectPojos);
      final ComponentName componentName = mapComponentName(rootPojoName, pojo.getName());
      final ObjectPojo thisPromotedPojo =
          pojo.withName(componentName)
              .withMembers(members)
              .withAllOfComposition(allOfNodePromotionResult.getComposition());
      final PList<ObjectPojo> newPromotedPojos =
          PList.fromIter(children.values())
              .flatMap(node -> node.promote(rootPojoName, allObjectPojos))
              .concat(allOfNodePromotionResult.getNewPromotedPojos());
      return new PojoNodePromotionResult(thisPromotedPojo, newPromotedPojos);
    }

    private AllOfNodePromotionResult promoteAllOfComposition(
        PojoName rootPojoName,
        Optional<AllOfComposition> allOfComposition,
        PList<ObjectPojo> allObjectPojos) {
      return allOfComposition
          .map(
              composition ->
                  promoteAllOfComposition(
                      rootPojoName, allOfComposition, allObjectPojos, composition))
          .orElse(new AllOfNodePromotionResult(Optional.empty(), PList.empty()));
    }

    private AllOfNodePromotionResult promoteAllOfComposition(
        PojoName rootPojoName,
        Optional<AllOfComposition> allOfComposition,
        PList<ObjectPojo> allObjectPojos,
        AllOfComposition composition) {
      final PList<PojoNodePromotionResult> results =
          composition
              .getPojos()
              .toPList()
              .flatMapOptional(Pojo::asObjectPojo)
              .map(p -> promotePojo(rootPojoName, p, allObjectPojos));
      final PList<ObjectPojo> promotedAllOfSubPojos = results.map(PojoNodePromotionResult::getPojo);
      if (composition.getPojos().size() == promotedAllOfSubPojos.size()) {
        final Optional<AllOfComposition> promotedAllOfComposition =
            NonEmptyList.fromIter(promotedAllOfSubPojos)
                .map(pojos -> AllOfComposition.fromPojos(pojos.map(p -> p)));
        return new AllOfNodePromotionResult(
            promotedAllOfComposition,
            results
                .flatMap(PojoNodePromotionResult::getNewPromotedPojos)
                .concat(promotedAllOfSubPojos));
      } else {
        return new AllOfNodePromotionResult(allOfComposition, PList.empty());
      }
    }

    private PojoMember promoteMember(PojoMember m) {
      return requiredProperties.exists(m.getName()::equals)
          ? m.withNecessity(Necessity.REQUIRED)
          : m;
    }

    private PojoMember renameObjectTypeNames(PojoName rootPojoName, PojoMember member) {
      final Type newType =
          member
              .getType()
              .asObjectType()
              .<Type>map(
                  type ->
                      StandardObjectType.ofName(
                          rootPojoName.appendToName(type.getName().getName().asString())))
              .orElse(member.getType());
      return Optional.ofNullable(children.get(member.getName()))
          .map(child -> member.withType(newType))
          .orElse(member);
    }
  }

  @Value
  private static class AllOfNodePromotionResult {
    Optional<AllOfComposition> composition;
    PList<ObjectPojo> newPromotedPojos;
  }

  @Value
  private static class PojoNodePromotionResult {
    ObjectPojo pojo;
    PList<ObjectPojo> newPromotedPojos;

    PList<ObjectPojo> merge() {
      return newPromotedPojos.add(pojo).distinct(Function.identity());
    }
  }

  private static ComponentName mapComponentName(
      PojoName rootPojoName, ComponentName originalComponentName) {
    return new ComponentName(
        rootPojoName.appendToName(originalComponentName.getPojoName().getName().asString()),
        originalComponentName.getSchemaName());
  }
}
