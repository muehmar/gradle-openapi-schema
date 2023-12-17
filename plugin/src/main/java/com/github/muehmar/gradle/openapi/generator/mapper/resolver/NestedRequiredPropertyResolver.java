package com.github.muehmar.gradle.openapi.generator.mapper.resolver;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.composition.AllOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
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
      final PList<RequiredProperty> requiredProps = findRequiredProperties(pojo, allOfComposition);
      if (requiredProps.nonEmpty()) {
        return Optional.of(
            new RequiredPropertiesForPojo(
                parentPojo, PList.single(pojo), allOfComposition, requiredProps));
      } else {
        return Optional.empty();
      }
    }

    private PList<RequiredProperty> findRequiredProperties(
        ObjectPojo pojo, AllOfComposition allOfComposition) {
      if (not(hasOnlyRequiredAdditionalProperties(pojo))) {
        return PList.empty();
      }

      final PList<ObjectPojo> otherAllOfPojos =
          allOfComposition
              .getPojos()
              .toPList()
              .flatMapOptional(Pojo::asObjectPojo)
              .filter(p -> not(p.equals(pojo)));
      return findRequiredPropertiesDeep(pojo, PList.empty())
          .filter(
              requiredProperty ->
                  otherAllOfPojos.exists(p -> hasPojoProperty(p, requiredProperty)));
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

    private boolean hasPojoProperty(ObjectPojo pojo, RequiredProperty requiredProperty) {
      return requiredProperty.resolveLink(pojo, allObjectPojos).isPresent();
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
          .flatMapOptional(
              allOfPojo ->
                  requiredProperties
                      .flatMapOptional(rp -> rp.toNodeFor(allOfPojo, allObjectPojos))
                      .reduce(PojoNode::merge));
    }

    PList<ObjectPojo> promote(PList<ObjectPojo> allObjectPojos) {
      final PList<PojoNode> nodes = toNodes(allObjectPojos);
      final PList<ObjectPojo> objectPojos = promoteNodes(allObjectPojos, nodes);
      final PList<Pojo> newAllOfPojos = createNewAllOfPojos(nodes, objectPojos);

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

    private Optional<ObjectPojo> resolveLink(
        ObjectPojo currentPojo, PList<ObjectPojo> allObjectPojos) {
      return linkChain
          .foldLeft(
              Optional.of(currentPojo),
              (nextPojo, link) -> nextPojo.flatMap(p -> link.resolveLink(p, allObjectPojos)))
          .filter(pojo -> pojo.getMembers().exists(m -> m.getName().equals(propertyName)));
    }

    Optional<PojoNode> toNodeFor(ObjectPojo pojo, PList<ObjectPojo> allObjectPojos) {
      if (linkChain.isEmpty()) {
        return pojo.getMembers().exists(pojoMember -> pojoMember.getName().equals(propertyName))
            ? Optional.of(
                new PojoNode(
                    pojo.getName().getPojoName(),
                    PList.single(propertyName),
                    Collections.emptyMap()))
            : Optional.empty();
      } else {
        return linkChain
            .headOption()
            .flatMap(
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
                                                    link.propertyName, childNode)))));
      }
    }
  }

  @Value
  private static class SinglePropertyLink {
    Name propertyName;
    PojoName pojoName;

    private Optional<ObjectPojo> resolveLink(
        ObjectPojo currentPojo, PList<ObjectPojo> allObjectPojos) {
      return currentPojo
          .getMembers()
          .find(member -> member.getName().equals(propertyName))
          .flatMap(member -> member.getType().asObjectType())
          .flatMap(
              objectType ->
                  allObjectPojos.find(p -> p.getName().getPojoName().equals(objectType.getName())));
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
          .map(
              pojo -> {
                final Function<ComponentName, ComponentName> mapName =
                    name -> mapComponentName(rootPojoName, name);
                final PList<PojoMember> members =
                    pojo.getMembers()
                        .map(this::promoteMember)
                        .map(m -> renameObjectTypeNames(rootPojoName, m));
                final ComponentName componentName = mapName.apply(pojo.getName());
                return PList.fromIter(children.values())
                    .flatMap(node -> node.promote(rootPojoName, allObjectPojos))
                    .add(pojo.withName(componentName).withMembers(members));
              })
          .orElse(PList.empty());
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
                      ObjectType.ofName(
                          rootPojoName.appendToName(type.getName().getName().asString())))
              .orElse(member.getType());
      return Optional.ofNullable(children.get(member.getName()))
          .map(child -> member.withType(newType))
          .orElse(member);
    }
  }

  private static ComponentName mapComponentName(
      PojoName rootPojoName, ComponentName originalComponentName) {
    return new ComponentName(
        rootPojoName.appendToName(originalComponentName.getPojoName().getName().asString()),
        originalComponentName.getSchemaName());
  }
}
