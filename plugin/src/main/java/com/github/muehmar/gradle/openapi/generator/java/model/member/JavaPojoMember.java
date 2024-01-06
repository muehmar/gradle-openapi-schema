package com.github.muehmar.gradle.openapi.generator.java.model.member;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.enumpojo.EnumContentBuilder;
import com.github.muehmar.gradle.openapi.generator.java.generator.enumpojo.EnumGenerator.EnumContent;
import com.github.muehmar.gradle.openapi.generator.java.model.name.IsNotNullFlagName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.IsNullFlagName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.IsPresentFlagName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaPojoName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.PropertyInfoName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaEnumType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
import com.github.muehmar.gradle.openapi.generator.settings.GetterSuffixes;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.With;

/**
 * This member corresponds to a property in the specification. To support all possible states af a
 * member, a single {@link JavaPojoMember} may result in more than one {@link TechnicalPojoMember}
 * in a pojo.
 */
@EqualsAndHashCode
@ToString
@PojoBuilder
@With
public class JavaPojoMember {
  private final JavaPojoName pojoName;
  private final JavaName name;
  private final String description;
  private final JavaType javaType;
  private final Necessity necessity;
  private final Nullability nullability;
  private final MemberType type;

  private static final String TRISTATE_TO_PROPERTY =
      "onValue(val -> val).onNull(() -> null).onAbsent(() -> null)";
  private static final String TRISTATE_TO_ISNULL_FLAG =
      "onValue(ignore -> false).onNull(() -> true).onAbsent(() -> false)";

  JavaPojoMember(
      JavaPojoName pojoName,
      JavaName name,
      String description,
      JavaType javaType,
      Necessity necessity,
      Nullability nullability,
      MemberType type) {
    this.pojoName = pojoName;
    this.javaType = javaType;
    this.name = name;
    this.description = description;
    this.necessity = necessity;
    this.nullability = nullability;
    this.type = type;
  }

  public static JavaPojoMember wrap(
      PojoMember pojoMember, JavaPojoName pojoName, TypeMappings typeMappings) {
    final JavaType javaType = JavaType.wrap(pojoMember.getType(), typeMappings);
    return new JavaPojoMember(
        pojoName,
        JavaName.fromName(pojoMember.getName()),
        pojoMember.getDescription(),
        javaType,
        pojoMember.getNecessity(),
        pojoMember.getNullability(),
        MemberType.OBJECT_MEMBER);
  }

  public PropertyInfoName getPropertyInfoName() {
    return PropertyInfoName.fromPojoNameAndMemberName(pojoName, name);
  }

  public JavaName getName() {
    return name;
  }

  public Nullability getNullability() {
    return nullability;
  }

  public Necessity getNecessity() {
    return necessity;
  }

  public String getDescription() {
    return description;
  }

  public JavaType getJavaType() {
    return javaType;
  }

  public MemberType getType() {
    return type;
  }

  public boolean isTechnicallyEquals(JavaPojoMember other) {
    return this.getMemberKey().equals(other.getMemberKey());
  }

  public MemberKey getMemberKey() {
    return new MemberKey(name, javaType);
  }

  public JavaPojoMember asAllOfMember() {
    return withType(MemberType.ALL_OF_MEMBER);
  }

  public JavaPojoMember asOneOfMember() {
    return withType(MemberType.ONE_OF_MEMBER);
  }

  public JavaPojoMember asAnyOfMember() {
    return withType(MemberType.ANY_OF_MEMBER);
  }

  public boolean isOptional() {
    return necessity.isOptional();
  }

  public boolean isRequired() {
    return necessity.isRequired();
  }

  public boolean isNullable() {
    return nullability.isNullable();
  }

  public boolean isNotNullable() {
    return nullability.isNotNullable();
  }

  public boolean isRequiredAndNullable() {
    return isRequired() && isNullable();
  }

  public boolean isRequiredAndNotNullable() {
    return isRequired() && isNotNullable();
  }

  public boolean isOptionalAndNullable() {
    return isOptional() && isNullable();
  }

  public boolean isOptionalAndNotNullable() {
    return isOptional() && isNotNullable();
  }

  public JavaName getWitherName() {
    return prefixedMethodName("with");
  }

  public JavaName getIsPresentFlagName() {
    return IsPresentFlagName.fromName(name).getName();
  }

  public JavaName getIsNullFlagName() {
    return IsNullFlagName.fromName(name).getName();
  }

  public JavaName getIsNotNullFlagName() {
    return IsNotNullFlagName.fromName(name).getName();
  }

  public JavaName getGetterName() {
    return prefixedMethodName("get");
  }

  public JavaName getValidationGetterName(PojoSettings settings) {
    return JavaName.fromString(getGetterName().asString())
        .append(settings.getValidationMethods().getGetterSuffix());
  }

  public JavaName getGetterNameWithSuffix(PojoSettings settings) {
    return JavaName.fromString(getGetterName().asString()).append(determineSuffix(settings));
  }

  private String determineSuffix(PojoSettings settings) {
    final GetterSuffixes getterSuffixes = settings.getGetterSuffixes();
    if (isRequiredAndNotNullable()) {
      return getterSuffixes.getRequiredSuffix();
    } else if (isRequiredAndNullable()) {
      return getterSuffixes.getRequiredNullableSuffix();
    } else if (isOptionalAndNotNullable()) {
      return getterSuffixes.getOptionalSuffix();
    } else {
      return getterSuffixes.getOptionalNullableSuffix();
    }
  }

  public JavaName prefixedMethodName(String prefix) {
    return name.prefixedMethodName(prefix);
  }

  public PList<TechnicalPojoMember> getTechnicalMembers() {
    final TechnicalPojoMember technicalPojoMember = TechnicalPojoMember.wrapPojoMember(this);
    if (isRequiredAndNullable()) {
      return PList.of(
          technicalPojoMember, TechnicalPojoMember.isPresentFlagMember(getIsPresentFlagName()));
    } else if (isOptionalAndNotNullable()) {
      return PList.of(
          technicalPojoMember, TechnicalPojoMember.isNotNullFlagMember(getIsNotNullFlagName()));
    } else if (isOptionalAndNullable()) {
      return PList.of(
          technicalPojoMember, TechnicalPojoMember.isNullFlagMember(getIsNullFlagName()));
    } else {
      return PList.single(technicalPojoMember);
    }
  }

  public String tristateToProperty() {
    return TRISTATE_TO_PROPERTY;
  }

  public String tristateToIsNullFlag() {
    return TRISTATE_TO_ISNULL_FLAG;
  }

  public Optional<JavaPojoMember> mergeToLeastRestrictive(JavaPojoMember other) {
    if (this.getMemberKey().equals(other.getMemberKey())) {
      final Nullability leastRestrictiveNullability =
          Nullability.leastRestrictive(this.getNullability(), other.getNullability());
      final Necessity leastRestrictiveNecessity =
          Necessity.leastRestrictive(this.getNecessity(), other.getNecessity());
      return Optional.of(
          this.withNullability(leastRestrictiveNullability)
              .withNecessity(leastRestrictiveNecessity));
    } else {
      return Optional.empty();
    }
  }

  public Optional<JavaPojoMember> mergeToMostRestrictive(JavaPojoMember other) {
    if (this.getMemberKey().equals(other.getMemberKey())) {
      final Nullability mostRestrictiveNullability =
          Nullability.mostRestrictive(this.getNullability(), other.getNullability());
      final Necessity mostRestrictiveNecessity =
          Necessity.mostRestrictive(this.getNecessity(), other.getNecessity());
      return Optional.of(
          this.withNullability(mostRestrictiveNullability).withNecessity(mostRestrictiveNecessity));
    } else {
      return Optional.empty();
    }
  }

  /** Creates {@link EnumContent} for this member in case its type is an {@link EnumType}. */
  public Optional<EnumContent> asEnumContent() {
    final Function<JavaEnumType, Optional<EnumContent>> toEnumPojo =
        enumType ->
            Optional.of(
                EnumContentBuilder.create()
                    .className(JavaName.fromName(enumType.getQualifiedClassName().getClassName()))
                    .description(getDescription())
                    .members(enumType.getMembers())
                    .build());
    return javaType.fold(
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        toEnumPojo,
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty());
  }

  /**
   * In case this member is an enum type, it converts the classname of the enum so that it's
   * referenced via outer-class {@code javaPojoName}.
   */
  public JavaPojoMember asInnerEnumOf(JavaName javaPojoName) {
    final JavaType newType =
        javaType.fold(
            arrayType -> arrayType,
            booleanType -> booleanType,
            enumType -> enumType.asInnerClassOf(javaPojoName),
            mapType -> mapType,
            noType -> noType,
            numericType -> numericType,
            integerType -> integerType,
            objectType -> objectType,
            stringType -> stringType);
    return new JavaPojoMember(pojoName, name, description, newType, necessity, nullability, type);
  }

  public enum MemberType {
    OBJECT_MEMBER,
    ADDITIONAL_PROPERTY_MEMBER,
    ALL_OF_MEMBER,
    ONE_OF_MEMBER,
    ANY_OF_MEMBER,
    ARRAY_VALUE
  }
}
