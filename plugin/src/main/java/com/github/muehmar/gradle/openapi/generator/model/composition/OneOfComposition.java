package com.github.muehmar.gradle.openapi.generator.model.composition;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.exception.OpenApiGeneratorException;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.name.SchemaName;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoNameMapping;
import com.github.muehmar.gradle.openapi.util.Optionals;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@EqualsAndHashCode
@ToString
public class OneOfComposition {
  private final NonEmptyList<Pojo> pojos;

  OneOfComposition(NonEmptyList<Pojo> pojos) {
    this.pojos = pojos;
  }

  public Optional<Discriminator> determineDiscriminator(
      Optional<UntypedDiscriminator> objectPojoDiscriminator) {
    if (objectPojoDiscriminator.isPresent()) {
      return typeDiscriminator(pojos, objectPojoDiscriminator);
    }

    final Optional<UntypedDiscriminator> discriminatorFromPojos =
        determineDiscriminatorFromComposedPojos();
    return typeDiscriminator(pojos, discriminatorFromPojos);
  }

  private Optional<UntypedDiscriminator> determineDiscriminatorFromComposedPojos() {
    final CompositionDiscriminators compositionDiscriminators =
        new CompositionDiscriminators(
            pojos.map(OneOfComposition::findDiscriminatorsForPojo).toPList());
    return compositionDiscriminators.findCommonDiscriminator();
  }

  private static PojoDiscriminators findDiscriminatorsForPojo(Pojo pojo) {
    final Optional<UntypedDiscriminator> objectPojoDiscriminator =
        pojo.asObjectPojo().flatMap(ObjectPojo::getDiscriminator);
    final PojoDiscriminators nestedDiscriminators =
        pojo.asObjectPojo()
            .flatMap(ObjectPojo::getAllOfComposition)
            .map(AllOfComposition::getPojos)
            .map(NonEmptyList::toPList)
            .orElse(PList.empty())
            .map(OneOfComposition::findDiscriminatorsForPojo)
            .reduce(PojoDiscriminators::merge)
            .orElseGet(PojoDiscriminators::empty);
    return PojoDiscriminators.fromOptional(objectPojoDiscriminator).merge(nestedDiscriminators);
  }

  public static OneOfComposition fromPojos(NonEmptyList<Pojo> pojos) {
    return new OneOfComposition(pojos);
  }

  public NonEmptyList<Pojo> getPojos() {
    return pojos;
  }

  public OneOfComposition inlineObjectReference(
      PojoName referenceName, String referenceDescription, Type referenceType) {
    final NonEmptyList<Pojo> mappedPojos =
        pojos.map(
            pojo -> pojo.inlineObjectReference(referenceName, referenceDescription, referenceType));
    return new OneOfComposition(mappedPojos);
  }

  public OneOfComposition applyMapping(PojoNameMapping pojoNameMapping) {
    final NonEmptyList<Pojo> mappedPojos = pojos.map(pojo -> pojo.applyMapping(pojoNameMapping));
    return new OneOfComposition(mappedPojos);
  }

  private static Optional<Discriminator> typeDiscriminator(
      NonEmptyList<Pojo> pojos, Optional<UntypedDiscriminator> discriminator) {
    return discriminator.map(disc -> typeDiscriminator(pojos, disc));
  }

  private static Discriminator typeDiscriminator(
      NonEmptyList<Pojo> pojos, UntypedDiscriminator discriminator) {
    final Name propertyName = discriminator.getPropertyName();
    final NonEmptyList<PojoDiscriminatorType> types =
        pojos
            .map(
                pojo ->
                    pojo.asObjectPojo()
                        .orElseThrow(
                            () ->
                                new OpenApiGeneratorException(
                                    "Only schemas of type object are supported for compositions, but %s is not of type object",
                                    pojo.getName().getSchemaName())))
            .map(objectPojo -> getDiscriminatorType(objectPojo, propertyName));

    types.toPList().forEach(type -> type.assertSameType(types.head(), propertyName, pojos));

    return Discriminator.typeDiscriminator(discriminator, types.head().getDiscriminatorType());
  }

  private static PojoDiscriminatorType getDiscriminatorType(
      ObjectPojo objectPojo, Name propertyName) {
    final SchemaName schemaName = objectPojo.getName().getSchemaName();
    return getDiscriminatorTypeDeep(objectPojo, propertyName)
        .headOption()
        .orElseThrow(
            () ->
                new OpenApiGeneratorException(
                    "Invalid schema: Pojo %s does not have a property named %s used by the discriminator.",
                    schemaName, propertyName));
  }

  private static PList<PojoDiscriminatorType> getDiscriminatorTypeDeep(
      ObjectPojo objectPojo, Name propertyName) {
    final SchemaName schemaName = objectPojo.getName().getSchemaName();
    final PList<PojoDiscriminatorType> memberDiscriminatorTypes =
        objectPojo
            .getMembers()
            .filter(pojoMember -> pojoMember.getName().equals(propertyName))
            .map(member -> assertNecessity(schemaName, member))
            .map(member -> extractType(objectPojo, schemaName, member));

    final PList<PojoDiscriminatorType> allOfDiscriminatorTypes =
        objectPojo
            .getAllOfComposition()
            .map(AllOfComposition::getPojos)
            .map(NonEmptyList::toPList)
            .orElse(PList.empty())
            .flatMapOptional(Pojo::asObjectPojo)
            .flatMap(pojo -> getDiscriminatorTypeDeep(pojo, propertyName));

    return memberDiscriminatorTypes.concat(allOfDiscriminatorTypes);
  }

  private static PojoDiscriminatorType extractType(
      ObjectPojo pojo, SchemaName schemaName, PojoMember member) {
    final Type type = member.getType();

    final Optional<DiscriminatorType> stringTypeDiscriminator =
        type.asStringType()
            .filter(strType -> strType.getFormat().equals(StringType.Format.NONE))
            .map(DiscriminatorType::fromStringType);

    final Optional<DiscriminatorType> enumTypeDiscriminator =
        type.asEnumType().map(DiscriminatorType::fromEnumType);

    return Optionals.or(stringTypeDiscriminator, enumTypeDiscriminator)
        .map(discriminatorType -> new PojoDiscriminatorType(pojo, discriminatorType))
        .orElseThrow(
            () ->
                new OpenApiGeneratorException(
                    "Invalid schema: The type of property %s of schema %s is not supported as discriminator",
                    member.getName(), schemaName));
  }

  private static PojoMember assertNecessity(SchemaName schemaName, PojoMember member) {
    if (member.isOptional()) {
      throw new OpenApiGeneratorException(
          "Invalid schema: Property %s of schema %s is not required.",
          member.getName(), schemaName);
    }
    return member;
  }

  @Value
  private static class PojoDiscriminators {
    PList<UntypedDiscriminator> discriminators;

    static PojoDiscriminators empty() {
      return new PojoDiscriminators(PList.empty());
    }

    static PojoDiscriminators fromOptional(Optional<UntypedDiscriminator> discriminator) {
      return new PojoDiscriminators(PList.fromOptional(discriminator));
    }

    PojoDiscriminators merge(PojoDiscriminators other) {
      return new PojoDiscriminators(discriminators.concat(other.discriminators));
    }
  }

  @Value
  private static class CompositionDiscriminators {
    PList<PojoDiscriminators> discriminators;

    public Optional<UntypedDiscriminator> findCommonDiscriminator() {
      return discriminators
          .headOption()
          .flatMap(
              pojoDiscriminators -> {
                final PList<PojoDiscriminators> remaining = discriminators.tail();
                for (UntypedDiscriminator discriminator : pojoDiscriminators.getDiscriminators()) {
                  final boolean existsInAll =
                      remaining.forall(
                          other -> other.getDiscriminators().exists(discriminator::equals));
                  if (existsInAll) {
                    return Optional.of(discriminator);
                  }
                }
                return Optional.empty();
              });
    }
  }

  @Value
  private static class PojoDiscriminatorType {
    ObjectPojo pojo;
    DiscriminatorType discriminatorType;

    void assertSameType(PojoDiscriminatorType other, Name propertyName, NonEmptyList<Pojo> pojos) {
      final NonEmptyList<SchemaName> schemaNames = pojos.map(p -> p.getName().getSchemaName());
      final OpenApiGeneratorException notSameTypeException =
          new OpenApiGeneratorException(
              "Property for discriminator %s of schemas [%s] are required to have the same type",
              propertyName, schemaNames.toPList().mkString(", "));
      other.discriminatorType.fold(
          stringType -> {
            if (not(discriminatorType.equals(other.discriminatorType))) {
              throw notSameTypeException;
            }
            return true;
          },
          enumType -> {
            if (not(discriminatorType.equals(other.discriminatorType))) {
              throw notSameTypeException;
            }
            if (not(pojo.equals(other.pojo))) {
              throw new OpenApiGeneratorException(
                  "An enum as discriminator (property %s for schemas [%s]) is only supported if it is defined in a single parent schema.",
                  propertyName, schemaNames.toPList().mkString(", "));
            }
            return true;
          });
    }
  }
}
