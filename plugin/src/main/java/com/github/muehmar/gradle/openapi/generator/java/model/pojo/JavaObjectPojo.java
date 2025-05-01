package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberContentBuilder.fullMemberContentBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.wither.WitherContentBuilder.fullWitherContentBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.ConstructorContentBuilder.fullConstructorContentBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.EqualsContentBuilder.fullEqualsContentBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.HashCodeContentBuilder.fullHashCodeContentBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.ToStringContentBuilder.fullToStringContentBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember.MemberType.ALL_OF_MEMBER;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember.MemberType.OBJECT_MEMBER;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojoBuilder.fullJavaObjectPojoBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojoWrapResultBuilder.fullJavaObjectPojoWrapResultBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojoWrapResultBuilder.javaObjectPojoWrapResultBuilder;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.exception.OpenApiGeneratorException;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.wither.WitherGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.EqualsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.HashCodeGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.PojoConstructorGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.ToStringGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.PojoType;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.DiscriminatableJavaComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAllOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAllOfComposition.AllOfCompositionPromotionResult;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfComposition.AnyOfCompositionPromotionResult;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition.OneOfCompositionPromotionResult;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TechnicalPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaPojoName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliary.MultiPojoContainer;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliary.SinglePojoContainer;
import com.github.muehmar.gradle.openapi.generator.java.model.promotion.PojoPromotionResult;
import com.github.muehmar.gradle.openapi.generator.java.model.promotion.PromotableMembers;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.name.SchemaName;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import io.github.muehmar.codegenerator.java.JavaModifier;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.With;

@PojoBuilder(packagePrivateBuilder = true)
@EqualsAndHashCode
@ToString
@With
public class JavaObjectPojo implements JavaPojo {
  private final JavaPojoName name;
  private final SchemaName schemaName;
  private final String description;
  private final JavaPojoMembers members;
  private final Optional<JavaAllOfComposition> allOfComposition;
  private final Optional<JavaOneOfComposition> oneOfComposition;
  private final Optional<JavaAnyOfComposition> anyOfComposition;
  private final PojoType type;
  private final PList<JavaRequiredAdditionalProperty> requiredAdditionalProperties;
  private final JavaAdditionalProperties additionalProperties;
  private final JavaPojoXml pojoXml;
  private final Constraints constraints;

  JavaObjectPojo(
      JavaPojoName name,
      SchemaName schemaName,
      String description,
      JavaPojoMembers members,
      Optional<JavaAllOfComposition> allOfComposition,
      Optional<JavaOneOfComposition> oneOfComposition,
      Optional<JavaAnyOfComposition> anyOfComposition,
      PojoType type,
      PList<JavaRequiredAdditionalProperty> requiredAdditionalProperties,
      JavaAdditionalProperties additionalProperties,
      JavaPojoXml pojoXml,
      Constraints constraints) {
    this.name = name;
    this.schemaName = schemaName;
    this.description = Optional.ofNullable(description).orElse("");
    this.members = members;
    this.allOfComposition = allOfComposition;
    this.oneOfComposition = oneOfComposition;
    this.anyOfComposition = anyOfComposition;
    this.type = type;
    this.requiredAdditionalProperties = requiredAdditionalProperties;
    this.additionalProperties = additionalProperties;
    this.pojoXml = pojoXml;
    this.constraints = constraints;
    assertPropertiesHaveNotSameNameAndDifferentAttributes(
        name, members, allOfComposition, oneOfComposition, anyOfComposition);
  }

  private static void assertPropertiesHaveNotSameNameAndDifferentAttributes(
      JavaPojoName name,
      JavaPojoMembers members,
      Optional<JavaAllOfComposition> allOfComposition,
      Optional<JavaOneOfComposition> oneOfComposition,
      Optional<JavaAnyOfComposition> anyOfComposition) {
    final PList<JavaPojoMember> allMembers =
        members
            .asList()
            .concat(
                allOfComposition
                    .map(JavaAllOfComposition::getMembers)
                    .map(JavaPojoMembers::asList)
                    .orElseGet(PList::empty))
            .concat(
                oneOfComposition
                    .map(JavaOneOfComposition::getMembers)
                    .map(JavaPojoMembers::asList)
                    .orElseGet(PList::empty))
            .concat(
                anyOfComposition
                    .map(JavaAnyOfComposition::getMembers)
                    .map(JavaPojoMembers::asList)
                    .orElseGet(PList::empty));

    final PList<JavaPojoMember> invalidMembers =
        allMembers
            .filter(
                member1 ->
                    allMembers.exists(member2 -> sameNameButDifferentAttributes(member1, member2)))
            .duplicates(Comparator.comparing(m -> m.getName().asString()));

    if (invalidMembers.nonEmpty()) {
      throw new OpenApiGeneratorException(
          String.format(
              "Cannot create DTO %s: Two or more properties (defined either in properties or in one of the schemas used "
                  + "for a composition (allOf, oneOf or anyOf) have the same name but different attributes (e.g. type, "
                  + "constraints)! Invalid properties: [%s].",
              name, invalidMembers.map(JavaPojoMember::getName).mkString(", ")));
    }
  }

  private static boolean sameNameButDifferentAttributes(JavaPojoMember m1, JavaPojoMember m2) {
    return m1.getName().equals(m2.getName()) && not(m1.isTechnicallyEquals(m2));
  }

  public static JavaPojoWrapResult wrap(ObjectPojo objectPojo, TypeMappings typeMappings) {
    return wrapJavaObjectPojo(objectPojo, typeMappings).promote();
  }

  private static JavaObjectPojoWrapResult wrapJavaObjectPojo(
      ObjectPojo objectPojo, TypeMappings typeMappings) {
    if (objectPojo.containsNoneDefaultPropertyScope()) {
      return fullJavaObjectPojoWrapResultBuilder()
          .defaultPojo(createForType(objectPojo, typeMappings, PojoType.DEFAULT))
          .requestPojo(createForType(objectPojo, typeMappings, PojoType.REQUEST))
          .responsePojo(createForType(objectPojo, typeMappings, PojoType.RESPONSE))
          .build();
    } else {
      return javaObjectPojoWrapResultBuilder()
          .defaultPojo(createForType(objectPojo, typeMappings, PojoType.DEFAULT))
          .build();
    }
  }

  private static JavaObjectPojo createForType(
      ObjectPojo objectPojo, TypeMappings typeMappings, PojoType type) {
    final JavaPojoName pojoName =
        JavaPojoName.fromPojoName(type.mapName(objectPojo.getName().getPojoName()));
    final PList<JavaPojoMember> members =
        objectPojo
            .getMembers()
            .filter(member -> type.includesPropertyScope(member.getPropertyScope()))
            .map(member -> JavaPojoMember.wrap(member, pojoName, typeMappings));
    final JavaAdditionalProperties javaAdditionalProperties =
        JavaAdditionalProperties.wrap(pojoName, objectPojo.getAdditionalProperties(), typeMappings);
    final PList<JavaRequiredAdditionalProperty> requiredAdditionalProperties =
        objectPojo
            .getRequiredAdditionalProperties()
            .map(
                propName ->
                    JavaRequiredAdditionalProperty.fromNameAndType(
                        propName, javaAdditionalProperties.getType()));
    final Optional<JavaAllOfComposition> allOfComposition =
        objectPojo
            .getAllOfComposition()
            .map(comp -> JavaAllOfComposition.wrap(comp, type, typeMappings));
    final Optional<JavaOneOfComposition> oneOfComposition =
        objectPojo
            .getOneOfComposition()
            .map(
                comp ->
                    JavaOneOfComposition.wrap(
                        comp, objectPojo.getDiscriminator(), type, typeMappings));
    final Optional<JavaAnyOfComposition> anyOfComposition =
        objectPojo
            .getAnyOfComposition()
            .map(
                comp ->
                    JavaAnyOfComposition.wrap(
                        comp, objectPojo.getDiscriminator(), type, typeMappings));
    return fullJavaObjectPojoBuilder()
        .name(pojoName)
        .schemaName(objectPojo.getName().getSchemaName())
        .description(objectPojo.getDescription())
        .members(JavaPojoMembers.fromMembers(members))
        .type(type)
        .requiredAdditionalProperties(requiredAdditionalProperties)
        .additionalProperties(javaAdditionalProperties)
        .pojoXml(new JavaPojoXml(objectPojo.getPojoXml().getName()))
        .constraints(objectPojo.getConstraints())
        .allOfComposition(allOfComposition)
        .oneOfComposition(oneOfComposition)
        .anyOfComposition(anyOfComposition)
        .build();
  }

  public PojoPromotionResult promoteAsRoot() {
    return promote(Optional.empty(), PromotableMembers.fromPojo(this));
  }

  public PojoPromotionResult promote(JavaPojoName rootName, PromotableMembers promotableMembers) {
    return promote(Optional.of(rootName), promotableMembers);
  }

  private PojoPromotionResult promote(
      Optional<JavaPojoName> rootName, PromotableMembers promotableMembers) {
    final JavaPojoMembers promotedMembers =
        members.map(
            originalMember ->
                promotableMembers.findByName(originalMember.getName()).orElse(originalMember));
    final PList<JavaPojoMember> promotedRequiredAdditionalProperties =
        requiredAdditionalProperties.flatMapOptional(
            prop -> promotableMembers.findByName(prop.getName()));
    final PList<JavaRequiredAdditionalProperty> remainingRequiredAdditionalProperties =
        requiredAdditionalProperties.filter(
            prop -> not(promotableMembers.isPromotable(prop.getName())));
    final Optional<AllOfCompositionPromotionResult> promotedAllOfComposition =
        allOfComposition.map(
            composition -> composition.promote(rootName.orElse(name), promotableMembers));
    final Optional<OneOfCompositionPromotionResult> promotedOneOfComposition =
        oneOfComposition.map(
            composition -> composition.promote(rootName.orElse(name), promotableMembers));
    final Optional<AnyOfCompositionPromotionResult> promotedAnyOfComposition =
        anyOfComposition.map(
            composition -> composition.promote(rootName.orElse(name), promotableMembers));

    final JavaPojoName deviatedPromotionName =
        rootName.map(rName -> rName.append(name)).orElse(name);

    final JavaObjectPojo promotedPojo =
        fullJavaObjectPojoBuilder()
            .name(deviatedPromotionName)
            .schemaName(schemaName)
            .description(description)
            .members(promotedMembers.add(promotedRequiredAdditionalProperties))
            .type(type)
            .requiredAdditionalProperties(remainingRequiredAdditionalProperties)
            .additionalProperties(additionalProperties)
            .pojoXml(pojoXml)
            .constraints(constraints)
            .allOfComposition(
                promotedAllOfComposition.map(AllOfCompositionPromotionResult::getComposition))
            .oneOfComposition(
                promotedOneOfComposition.map(OneOfCompositionPromotionResult::getComposition))
            .anyOfComposition(
                promotedAnyOfComposition.map(AnyOfCompositionPromotionResult::getComposition))
            .build();

    if (promotedPojo.withName(name).equals(this)) {
      return PojoPromotionResult.ofUnchangedPojo(this);
    }

    final PList<JavaObjectPojo> newPojos =
        PList.of(
                promotedAllOfComposition.map(AllOfCompositionPromotionResult::getNewPojos),
                promotedOneOfComposition.map(OneOfCompositionPromotionResult::getNewPojos),
                promotedAnyOfComposition.map(AnyOfCompositionPromotionResult::getNewPojos))
            .flatMapOptional(Function.identity())
            .flatMap(pojos -> pojos)
            .cons(promotedPojo);

    return new PojoPromotionResult(promotedPojo, newPojos);
  }

  @Override
  public JavaName getSchemaName() {
    return JavaName.fromName(schemaName.asName());
  }

  @Override
  public JavaName getClassName() {
    return name.asJavaName();
  }

  public JavaPojoName getJavaPojoName() {
    return name;
  }

  public JavaName prefixedClassNameForMethod(String prefix) {
    return name.asJavaName().prefixedMethodName(prefix);
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public PojoType getType() {
    return type;
  }

  public boolean isSimpleMapPojo() {
    return members.isEmpty()
        && not(allOfComposition.isPresent())
        && not(oneOfComposition.isPresent())
        && not(anyOfComposition.isPresent());
  }

  public PList<JavaPojoMember> getMembers() {
    return members.asList();
  }

  /** This includes also any possible required additional property. */
  public int getRequiredMemberCount() {
    return members.getRequiredMemberCount() + requiredAdditionalProperties.size();
  }

  public PList<JavaPojoMember> getAllMembersForComposition() {
    return members
        .map(member -> member.asInnerEnumOf(getClassName()))
        .add(getComposedMembers())
        .asList();
  }

  PList<JavaPojoMember> getComposedMembers() {
    final JavaPojoMembers allOfMembers =
        allOfComposition.map(JavaAllOfComposition::getMembers).orElseGet(JavaPojoMembers::empty);
    final JavaPojoMembers oneOfMembers =
        oneOfComposition.map(JavaOneOfComposition::getMembers).orElseGet(JavaPojoMembers::empty);
    final JavaPojoMembers anyOfMembers =
        anyOfComposition.map(JavaAnyOfComposition::getMembers).orElseGet(JavaPojoMembers::empty);
    return allOfMembers.add(oneOfMembers).add(anyOfMembers).asList();
  }

  public PList<JavaPojoMember> getAllMembers() {
    return members.add(getComposedMembers()).asList();
  }

  public PList<TechnicalPojoMember> getTechnicalMembers() {
    return getAllMembers()
        .flatMap(JavaPojoMember::getTechnicalMembers)
        .add(additionalProperties.asTechnicalPojoMember());
  }

  public Optional<JavaAllOfComposition> getAllOfComposition() {
    return allOfComposition;
  }

  public Optional<JavaOneOfComposition> getOneOfComposition() {
    return oneOfComposition;
  }

  public boolean hasOneOfComposition() {
    return oneOfComposition.isPresent();
  }

  public Optional<JavaAnyOfComposition> getAnyOfComposition() {
    return anyOfComposition;
  }

  public PList<JavaObjectPojo> getAnyOfPojos() {
    return anyOfComposition
        .map(JavaAnyOfComposition::getPojos)
        .map(NonEmptyList::toPList)
        .orElseGet(PList::empty);
  }

  public boolean hasAnyOfComposition() {
    return anyOfComposition.isPresent();
  }

  public PList<DiscriminatableJavaComposition> getDiscriminatableCompositions() {
    return PList.<Optional<DiscriminatableJavaComposition>>of(
            getOneOfComposition().map(c -> c), getAnyOfComposition().map(c -> c))
        .flatMapOptional(Function.identity());
  }

  public PList<JavaRequiredAdditionalProperty> getRequiredAdditionalProperties() {
    return requiredAdditionalProperties;
  }

  public PList<JavaRequiredAdditionalProperty> getAllPermanentRequiredAdditionalProperties() {
    return PList.fromOptional(allOfComposition)
        .flatMap(JavaAllOfComposition::getPojos)
        .flatMap(JavaObjectPojo::getAllPermanentRequiredAdditionalProperties)
        .concat(requiredAdditionalProperties);
  }

  public JavaAdditionalProperties getAdditionalProperties() {
    return additionalProperties;
  }

  public Constraints getConstraints() {
    return constraints;
  }

  public MemberGenerator.MemberContent getMemberContent() {
    return fullMemberContentBuilder()
        .isArrayPojo(false)
        .members(getAllMembers().flatMap(JavaPojoMember::getTechnicalMembers))
        .additionalProperties(additionalProperties)
        .build();
  }

  public HashCodeGenerator.HashCodeContent getHashCodeContent() {
    return fullHashCodeContentBuilder().technicalPojoMembers(getTechnicalMembers()).build();
  }

  public EqualsGenerator.EqualsContent getEqualsContent() {
    return fullEqualsContentBuilder()
        .className(getClassName())
        .technicalPojoMembers(getTechnicalMembers())
        .build();
  }

  public ToStringGenerator.ToStringContent getToStringContent() {
    return fullToStringContentBuilder()
        .className(getClassName())
        .technicalPojoMembers(getTechnicalMembers())
        .build();
  }

  public PojoConstructorGenerator.ConstructorContent getConstructorContent() {
    return fullConstructorContentBuilder()
        .isArray(false)
        .className(getClassName())
        .members(getAllMembers().flatMap(JavaPojoMember::getTechnicalMembers))
        .modifier(Optional.of(JavaModifier.PUBLIC).filter(ignore -> not(isSimpleMapPojo())))
        .additionalProperties(additionalProperties)
        .build();
  }

  public WitherGenerator.WitherContent getWitherContent() {
    final PList<JavaPojoMember> membersForWithers =
        getAllMembers()
            .filter(m -> m.getType().equals(OBJECT_MEMBER) || m.getType().equals(ALL_OF_MEMBER));
    return fullWitherContentBuilder()
        .className(getClassName())
        .membersForWithers(membersForWithers)
        .technicalPojoMembers(getTechnicalMembers())
        .build();
  }

  public PList<SinglePojoContainer> getSinglePojoContainers() {
    return PList.of(
            oneOfComposition.map(c -> new SinglePojoContainer(name, c)),
            anyOfComposition
                .filter(DiscriminatableJavaComposition::hasDiscriminator)
                .map(c -> new SinglePojoContainer(name, c)))
        .flatMapOptional(Function.identity());
  }

  public PList<MultiPojoContainer> getMultiPojoContainer() {
    return PList.fromOptional(
        anyOfComposition
            .filter(composition -> not(composition.hasDiscriminator()))
            .map(composition -> new MultiPojoContainer(name, composition)));
  }

  public boolean hasCompositions() {
    return allOfComposition.isPresent()
        || oneOfComposition.isPresent()
        || anyOfComposition.isPresent();
  }

  @Override
  public <T> T fold(
      Function<JavaArrayPojo, T> onArrayPojo,
      Function<JavaEnumPojo, T> onEnumPojo,
      Function<JavaObjectPojo, T> onObjectPojo) {
    return onObjectPojo.apply(this);
  }
}
