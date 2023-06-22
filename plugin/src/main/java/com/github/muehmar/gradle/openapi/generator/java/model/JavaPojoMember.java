package com.github.muehmar.gradle.openapi.generator.java.model;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.enumpojo.EnumContentBuilder;
import com.github.muehmar.gradle.openapi.generator.java.generator.enumpojo.EnumGenerator.EnumContent;
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

@EqualsAndHashCode
@ToString
@PojoBuilder
public class JavaPojoMember {
  private final JavaMemberName name;
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
      JavaMemberName name,
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
        JavaMemberName.wrap(pojoMember.getName()),
        pojoMember.getDescription(),
        javaType,
        pojoMember.getNecessity(),
        pojoMember.getNullability(),
        MemberType.OBJECT_MEMBER);
  }

  public JavaMemberName getName() {
    return name;
  }

  public JavaIdentifier getNameAsIdentifier() {
    return name.asJavaName().asIdentifier();
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

  public JavaIdentifier getWitherName() {
    return prefixedMethodName("with");
  }

  public JavaIdentifier getIsPresentFlagName() {
    return name.asJavaName().startUpperCase().prefix("is").append("Present").asIdentifier();
  }

  public JavaIdentifier getIsNullFlagName() {
    return name.asJavaName().startUpperCase().prefix("is").append("Null").asIdentifier();
  }

  public JavaIdentifier getGetterName() {
    return prefixedMethodName("get");
  }

  public JavaIdentifier getValidationGetterName(PojoSettings settings) {
    return JavaName.fromString(getGetterName().asString())
        .append(settings.getValidationMethods().getGetterSuffix())
        .asIdentifier();
  }

  public JavaIdentifier getGetterNameWithSuffix(PojoSettings settings) {
    return JavaName.fromString(getGetterName().asString())
        .append(determineSuffix(settings))
        .asIdentifier();
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

  public JavaIdentifier prefixedMethodName(String prefix) {
    if (prefix.isEmpty()) {
      return name.asJavaName().asIdentifier();
    } else {
      return name.asJavaName().startUpperCase().prefix(prefix).asIdentifier();
    }
  }

  public PList<JavaIdentifier> createFieldNames() {
    final JavaIdentifier memberName = name.asIdentifier();
    if (isRequiredAndNullable()) {
      return PList.of(memberName, getIsPresentFlagName());
    } else if (isOptionalAndNullable()) {
      return PList.of(memberName, getIsNullFlagName());
    } else {
      return PList.single(memberName);
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
                    .className(
                        JavaIdentifier.fromName(enumType.getQualifiedClassName().getClassName()))
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
  public JavaPojoMember asInnerEnumOf(JavaIdentifier javaPojoName) {
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
    OBJECT_MEMBER,
    ALL_OF_MEMBER,
    ONE_OF_MEMBER,
    ANY_OF_MEMBER,
    ARRAY_VALUE
  }
}
