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
      return typeDiscriminator(
          pojos, objectPojoDiscriminator, DiscriminatorDefinitionOrigin.PARENT_SCHEMA);
    }

    final Optional<UntypedDiscriminator> discriminatorFromPojos =
        determineDiscriminatorFromComposedPojos();
    return typeDiscriminator(
        pojos, discriminatorFromPojos, DiscriminatorDefinitionOrigin.EACH_COMPOSITION_SCHEMA);
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
      NonEmptyList<Pojo> pojos,
      Optional<UntypedDiscriminator> discriminator,
      DiscriminatorDefinitionOrigin discriminatorOrigin) {
    return discriminator.map(disc -> typeDiscriminator(pojos, disc, discriminatorOrigin));
  }

  private static Discriminator typeDiscriminator(
      NonEmptyList<Pojo> pojos,
      UntypedDiscriminator discriminator,
      DiscriminatorDefinitionOrigin discriminatorOrigin) {
    final Name propertyName = discriminator.getPropertyName();
    final NonEmptyList<DiscriminatorType> types =
        pojos
            .map(
                pojo ->
                    pojo.asObjectPojo()
                        .orElseThrow(
                            () ->
                                new OpenApiGeneratorException(
                                    "Only schemas of type object are supported for compositions, but %s is not of type object",
                                    pojo.getName().getSchemaName())))
            .map(objectPojo -> getDiscriminatorType(objectPojo, propertyName, discriminatorOrigin));

    final boolean allSameType = types.toPList().forall(types.head()::equals);
    if (not(allSameType)) {
      throw new OpenApiGeneratorException(
          "Property for discriminator %s of schemas [%s] are required to have the same type",
          propertyName, pojos.map(p -> p.getName().getSchemaName()).toPList().mkString(", "));
    }

    return Discriminator.typeDiscriminator(discriminator, types.head());
  }

  private static DiscriminatorType getDiscriminatorType(
      ObjectPojo objectPojo, Name propertyName, DiscriminatorDefinitionOrigin discriminatorOrigin) {
    final SchemaName schemaName = objectPojo.getName().getSchemaName();
    final PojoMember discriminatorMember =
        objectPojo
            .getMembersAndAllOfMembers()
            .find(pojoMember -> pojoMember.getName().equals(propertyName))
            .orElseThrow(
                () ->
                    new OpenApiGeneratorException(
                        "Invalid schema: Pojo %s does not have a property named %s used by the discriminator.",
                        schemaName, propertyName));
    assertNecessity(schemaName, discriminatorMember);
    return extractType(schemaName, discriminatorMember, discriminatorOrigin);
  }

  private static DiscriminatorType extractType(
      SchemaName schemaName, PojoMember member, DiscriminatorDefinitionOrigin discriminatorOrigin) {
    final Type type = member.getType();

    final Optional<DiscriminatorType> stringTypeDiscriminator =
        type.asStringType()
            .filter(strType -> strType.getFormat().equals(StringType.Format.NONE))
            .map(DiscriminatorType::fromStringType);

    final Optional<DiscriminatorType> enumTypeDiscriminator =
        type.asEnumType().map(DiscriminatorType::fromEnumType);

    if (enumTypeDiscriminator.isPresent()
        && discriminatorOrigin.equals(DiscriminatorDefinitionOrigin.EACH_COMPOSITION_SCHEMA)) {
      throw new OpenApiGeneratorException(
          "An enum as discriminator (property %s for schema %s) is only supported if it is defined in a single parent schema.",
          member.getName(), schemaName);
    }

    return Optionals.or(stringTypeDiscriminator, enumTypeDiscriminator)
        .orElseThrow(
            () ->
                new OpenApiGeneratorException(
                    "Invalid schema: The type of property %s of schema %s is not supported as discriminator",
                    member.getName(), schemaName));
  }

  private static void assertNecessity(SchemaName schemaName, PojoMember member) {
    if (member.isOptional()) {
      throw new OpenApiGeneratorException(
          "Invalid schema: Property %s of schema %s is not required.",
          member.getName(), schemaName);
    }
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

  private enum DiscriminatorDefinitionOrigin {
    /** Defined in a single parent schema and inherited via allOf composition */
    PARENT_SCHEMA,

    /** Defined in each single schema used in the composition. */
    EACH_COMPOSITION_SCHEMA,
  }
}
