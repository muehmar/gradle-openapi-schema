package com.github.muehmar.gradle.openapi.generator.java.model;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.enumpojo.EnumContentBuilder;
import com.github.muehmar.gradle.openapi.generator.java.generator.enumpojo.EnumGenerator.EnumContent;
import com.github.muehmar.gradle.openapi.generator.java.model.name.IsNullFlagName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.IsPresentFlagName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
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
      JavaName name,
      String description,
      JavaType javaType,
      Necessity necessity,
      Nullability nullability,
      MemberType type) {
    this.javaType = javaType;
    this.name = name;
    this.description = description;
    this.necessity = necessity;
    this.nullability = nullability;
    this.type = type;
  }

  public static JavaPojoMember wrap(PojoMember pojoMember, TypeMappings typeMappings) {
    final JavaType javaType = JavaType.wrap(pojoMember.getType(), typeMappings);
    return new JavaPojoMember(
        JavaName.fromName(pojoMember.getName()),
        pojoMember.getDescription(),
        javaType,
        pojoMember.getNecessity(),
        pojoMember.getNullability(),
        MemberType.OBJECT_MEMBER);
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

  public JavaPojoMember asObjectMember() {
    return JavaPojoMemberBuilder.create()
        .name(name)
        .description(description)
        .javaType(javaType)
        .necessity(necessity)
        .nullability(nullability)
        .type(MemberType.OBJECT_MEMBER)
        .andAllOptionals()
        .build();
  }

  public JavaPojoMember asAllOfMember() {
    return JavaPojoMemberBuilder.create()
        .name(name)
        .description(description)
        .javaType(javaType)
        .necessity(necessity)
        .nullability(nullability)
        .type(MemberType.ALL_OF_MEMBER)
        .andAllOptionals()
        .build();
  }

  public JavaPojoMember asOneOfMember() {
    return JavaPojoMemberBuilder.create()
        .name(name)
        .description(description)
        .javaType(javaType)
        .necessity(necessity)
        .nullability(nullability)
        .type(MemberType.ONE_OF_MEMBER)
        .andAllOptionals()
        .build();
  }

  public JavaPojoMember asAnyOfMember() {
    return JavaPojoMemberBuilder.create()
        .name(name)
        .description(description)
        .javaType(javaType)
        .necessity(necessity)
        .nullability(nullability)
        .type(MemberType.ANY_OF_MEMBER)
        .andAllOptionals()
        .build();
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
    return new JavaPojoMember(name, description, newType, necessity, nullability, type);
  }

  public enum MemberType {
    OBJECT_MEMBER(false),
    ALL_OF_MEMBER(true),
    ONE_OF_MEMBER(true),
    ANY_OF_MEMBER(true),
    ARRAY_VALUE(false);

    private final boolean isComposedMember;

    MemberType(boolean isComposedMember) {
      this.isComposedMember = isComposedMember;
    }

    public boolean isComposedMember() {
      return isComposedMember;
    }
  }
}
