package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.exception.OpenApiGeneratorException;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberContentBuilder;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.WitherContentBuilder;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.WitherGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.ConstructorContentBuilder;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.EqualsContentBuilder;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.EqualsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.HashCodeContentBuilder;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.HashCodeGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.PojoConstructorGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.ToStringContentBuilder;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.ToStringGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoName;
import com.github.muehmar.gradle.openapi.generator.java.model.PojoType;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAllOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@PojoBuilder(packagePrivateBuilder = true)
@EqualsAndHashCode
@ToString
public class JavaObjectPojo implements JavaPojo {
  private final JavaPojoName name;
  private final String description;
  private final PList<JavaPojoMember> members;
  private final Optional<JavaAllOfComposition> allOfComposition;
  private final Optional<JavaOneOfComposition> oneOfComposition;
  private final Optional<JavaAnyOfComposition> anyOfComposition;
  private final PojoType type;
  private final PList<JavaRequiredAdditionalProperty> requiredAdditionalProperties;
  private final JavaAdditionalProperties additionalProperties;
  private final Constraints constraints;

  JavaObjectPojo(
      JavaPojoName name,
      String description,
      PList<JavaPojoMember> members,
      Optional<JavaAllOfComposition> allOfComposition,
      Optional<JavaOneOfComposition> oneOfComposition,
      Optional<JavaAnyOfComposition> anyOfComposition,
      PojoType type,
      PList<JavaRequiredAdditionalProperty> requiredAdditionalProperties,
      JavaAdditionalProperties additionalProperties,
      Constraints constraints) {
    this.name = name;
    this.description = Optional.ofNullable(description).orElse("");
    this.members = members;
    this.allOfComposition = allOfComposition;
    this.oneOfComposition = oneOfComposition;
    this.anyOfComposition = anyOfComposition;
    this.type = type;
    this.requiredAdditionalProperties = requiredAdditionalProperties;
    this.additionalProperties = additionalProperties;
    this.constraints = constraints;
    assertPropertiesHaveNotSameNameAndDifferentAttributes(
        name, members, allOfComposition, oneOfComposition, anyOfComposition);
  }

  private static void assertPropertiesHaveNotSameNameAndDifferentAttributes(
      JavaPojoName name,
      PList<JavaPojoMember> members,
      Optional<JavaAllOfComposition> allOfComposition,
      Optional<JavaOneOfComposition> oneOfComposition,
      Optional<JavaAnyOfComposition> anyOfComposition) {
    final PList<JavaPojoMember> allMembers =
        members
            .concat(allOfComposition.map(JavaAllOfComposition::getMembers).orElseGet(PList::empty))
            .concat(oneOfComposition.map(JavaOneOfComposition::getMembers).orElseGet(PList::empty))
            .concat(anyOfComposition.map(JavaAnyOfComposition::getMembers).orElseGet(PList::empty));

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
    return m1.getName().equals(m2.getName())
        && not(m1.asObjectMember().equals(m2.asObjectMember()));
  }

  public static JavaPojoWrapResult wrap(ObjectPojo objectPojo, TypeMappings typeMappings) {
    if (objectPojo.containsNoneDefaultPropertyScope()) {
      return JavaPojoWrapResultBuilder.create()
          .defaultPojo(createForType(objectPojo, typeMappings, PojoType.DEFAULT))
          .andAllOptionals()
          .requestPojo(createForType(objectPojo, typeMappings, PojoType.REQUEST))
          .responsePojo(createForType(objectPojo, typeMappings, PojoType.RESPONSE))
          .build();
    } else {
      return JavaPojoWrapResult.ofDefaultPojo(
          createForType(objectPojo, typeMappings, PojoType.DEFAULT));
    }
  }

  private static JavaObjectPojo createForType(
      ObjectPojo objectPojo, TypeMappings typeMappings, PojoType type) {
    final PList<JavaPojoMember> members =
        objectPojo
            .getMembers()
            .filter(member -> type.includesPropertyScope(member.getPropertyScope()))
            .map(member -> JavaPojoMember.wrap(member, typeMappings));
    final JavaAdditionalProperties javaAdditionalProperties =
        JavaAdditionalProperties.wrap(objectPojo.getAdditionalProperties(), typeMappings);
    return new JavaObjectPojo(
        JavaPojoName.wrap(type.mapName(objectPojo.getName())),
        objectPojo.getDescription(),
        members,
        objectPojo
            .getAllOfComposition()
            .map(comp -> JavaAllOfComposition.wrap(comp, type, typeMappings)),
        objectPojo
            .getOneOfComposition()
            .map(comp -> JavaOneOfComposition.wrap(comp, type, typeMappings)),
        objectPojo
            .getAnyOfComposition()
            .map(comp -> JavaAnyOfComposition.wrap(comp, type, typeMappings)),
        type,
        objectPojo
            .getRequiredAdditionalProperties()
            .map(
                propName ->
                    JavaRequiredAdditionalProperty.fromNameAndType(
                        propName, javaAdditionalProperties.getType())),
        javaAdditionalProperties,
        objectPojo.getConstraints());
  }

  @Override
  public JavaName getSchemaName() {
    return JavaName.fromName(name.getSchemaName());
  }

  @Override
  public JavaIdentifier getClassName() {
    return name.asJavaName().asIdentifier();
  }

  public JavaIdentifier prefixedClassNameForMethod(String prefix) {
    if (prefix.isEmpty()) {
      return name.asJavaName().startLowerCase().asIdentifier();
    } else {
      return name.asJavaName().startUpperCase().prefix(prefix).asIdentifier();
    }
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

  public boolean hasRequiredMembers() {
    return members.exists(JavaPojoMember::isRequired);
  }

  public boolean hasNotRequiredMembers() {
    return not(hasRequiredMembers());
  }

  public PList<JavaPojoMember> getMembers() {
    return members;
  }

  private PList<JavaPojoMember> getAllOfMembers() {
    return allOfComposition.map(JavaAllOfComposition::getMembers).orElseGet(PList::empty);
  }

  private PList<JavaPojoMember> getOneOfMembers() {
    return oneOfComposition.map(JavaOneOfComposition::getMembers).orElseGet(PList::empty);
  }

  private PList<JavaPojoMember> getAnyOfMembers() {
    return anyOfComposition.map(JavaAnyOfComposition::getMembers).orElseGet(PList::empty);
  }

  public PList<JavaPojoMember> getAllMembersForComposition() {
    return getMembers()
        .map(member -> member.asInnerEnumOf(getClassName()))
        .concat(getComposedMembers());
  }

  public PList<JavaPojoMember> getComposedMembers() {
    return getAllOfMembers()
        .concat(getOneOfMembers())
        .concat(getAnyOfMembers())
        .distinct(Function.identity());
  }

  public PList<JavaPojoMember> getAllMembers() {
    return members.concat(getComposedMembers()).distinct(Function.identity());
  }

  public Optional<JavaAllOfComposition> getAllOfComposition() {
    return allOfComposition;
  }

  public Optional<JavaOneOfComposition> getOneOfComposition() {
    return oneOfComposition;
  }

  public Optional<JavaAnyOfComposition> getAnyOfComposition() {
    return anyOfComposition;
  }

  public PList<JavaRequiredAdditionalProperty> getRequiredAdditionalProperties() {
    return requiredAdditionalProperties;
  }

  public JavaAdditionalProperties getAdditionalProperties() {
    return additionalProperties;
  }

  public Constraints getConstraints() {
    return constraints;
  }

  public MemberGenerator.MemberContent getMemberContent() {
    return MemberContentBuilder.create()
        .isArrayPojo(false)
        .members(getAllMembers())
        .andAllOptionals()
        .additionalProperties(additionalProperties)
        .build();
  }

  public HashCodeGenerator.HashCodeContent getHashCodeContent() {
    return HashCodeContentBuilder.create()
        .members(getAllMembers())
        .andAllOptionals()
        .additionalProperties(additionalProperties)
        .build();
  }

  public EqualsGenerator.EqualsContent getEqualsContent() {
    return EqualsContentBuilder.create()
        .className(getClassName())
        .members(getAllMembers())
        .andAllOptionals()
        .additionalProperties(additionalProperties)
        .build();
  }

  public ToStringGenerator.ToStringContent getToStringContent() {
    return ToStringContentBuilder.create()
        .className(getClassName())
        .members(getAllMembers())
        .andAllOptionals()
        .additionalProperties(additionalProperties)
        .build();
  }

  public PojoConstructorGenerator.ConstructorContent getConstructorContent() {
    return ConstructorContentBuilder.create()
        .isArray(false)
        .className(getClassName())
        .members(getAllMembers())
        .andAllOptionals()
        .additionalProperties(additionalProperties)
        .build();
  }

  public WitherGenerator.WitherContent getWitherContent() {
    return WitherContentBuilder.create()
        .className(getClassName())
        .membersForWithers(getMembers())
        .membersForConstructorCall(getAllMembers())
        .andAllOptionals()
        .additionalProperties(additionalProperties)
        .build();
  }

  @Override
  public <T> T fold(
      Function<JavaArrayPojo, T> onArrayPojo,
      Function<JavaEnumPojo, T> onEnumPojo,
      Function<JavaObjectPojo, T> onObjectPojo) {
    return onObjectPojo.apply(this);
  }
}
