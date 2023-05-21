package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberContentBuilder;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.WitherContentBuilder;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.WitherGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.builder.NormalBuilderContentBuilder;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.builder.NormalBuilderGenerator;
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
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoName;
import com.github.muehmar.gradle.openapi.generator.java.model.PojoType;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class JavaObjectPojo implements JavaPojo {
  private final JavaPojoName name;
  private final String description;
  private final PList<JavaPojoMember> members;
  private final PojoType type;
  private final JavaAdditionalProperties additionalProperties;
  private final Constraints constraints;

  private JavaObjectPojo(
      JavaPojoName name,
      String description,
      PList<JavaPojoMember> members,
      PojoType type,
      JavaAdditionalProperties additionalProperties,
      Constraints constraints) {
    this.name = name;
    this.description = Optional.ofNullable(description).orElse("");
    this.members = members;
    this.type = type;
    this.additionalProperties = additionalProperties;
    this.constraints = constraints;
  }

  public static JavaObjectPojo from(
      JavaPojoName name,
      String description,
      PList<JavaPojoMember> members,
      PojoType type,
      JavaAdditionalProperties additionalProperties,
      Constraints constraints) {
    return new JavaObjectPojo(name, description, members, type, additionalProperties, constraints);
  }

  public static JavaObjectPojo from(
      PojoName name,
      String description,
      PList<JavaPojoMember> members,
      PojoType type,
      JavaAdditionalProperties additionalProperties,
      Constraints constraints) {
    return from(
        JavaPojoName.wrap(name), description, members, type, additionalProperties, constraints);
  }

  public static NonEmptyList<JavaObjectPojo> wrap(
      ObjectPojo objectPojo, TypeMappings typeMappings) {
    if (objectPojo.containsNoneDefaultPropertyScope()) {
      return NonEmptyList.of(
          createForType(objectPojo, typeMappings, PojoType.DEFAULT),
          createForType(objectPojo, typeMappings, PojoType.REQUEST),
          createForType(objectPojo, typeMappings, PojoType.RESPONSE));
    } else {
      return NonEmptyList.single(createForType(objectPojo, typeMappings, PojoType.DEFAULT));
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
        type,
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

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public PojoType getType() {
    return type;
  }

  public PList<JavaPojoMember> getMembers() {
    return members;
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
        .members(members)
        .andAllOptionals()
        .additionalProperties(additionalProperties)
        .build();
  }

  public HashCodeGenerator.HashCodeContent getHashCodeContent() {
    return HashCodeContentBuilder.create()
        .members(members)
        .andAllOptionals()
        .additionalProperties(additionalProperties)
        .build();
  }

  public EqualsGenerator.EqualsContent getEqualsContent() {
    return EqualsContentBuilder.create()
        .className(getClassName())
        .members(members)
        .andAllOptionals()
        .additionalProperties(additionalProperties)
        .build();
  }

  public ToStringGenerator.ToStringContent getToStringContent() {
    return ToStringContentBuilder.create()
        .className(getClassName())
        .members(members)
        .andAllOptionals()
        .additionalProperties(additionalProperties)
        .build();
  }

  public PojoConstructorGenerator.ConstructorContent getConstructorContent() {
    return ConstructorContentBuilder.create()
        .isArray(false)
        .className(getClassName())
        .members(members)
        .andAllOptionals()
        .additionalProperties(additionalProperties)
        .build();
  }

  public NormalBuilderGenerator.NormalBuilderContent getNormalBuilderContent() {
    return NormalBuilderContentBuilder.create()
        .className(getClassName())
        .members(members)
        .andAllOptionals()
        .additionalProperties(additionalProperties)
        .build();
  }

  public WitherGenerator.WitherContent getWitherContent() {
    return WitherContentBuilder.create()
        .className(getClassName())
        .members(members)
        .andAllOptionals()
        .additionalProperties(additionalProperties)
        .build();
  }

  @Override
  public <T> T fold(
      Function<JavaArrayPojo, T> onArrayPojo,
      Function<JavaEnumPojo, T> onEnumPojo,
      Function<JavaObjectPojo, T> onObjectPojo,
      Function<JavaComposedPojo, T> onComposedPojo) {
    return onObjectPojo.apply(this);
  }
}
