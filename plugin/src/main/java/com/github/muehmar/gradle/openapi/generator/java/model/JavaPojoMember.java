package com.github.muehmar.gradle.openapi.generator.java.model;

import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.GetterSuffixes;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class JavaPojoMember {
  private final Name name;
  private final String description;
  private final JavaType javaType;
  private final Necessity necessity;
  private final Nullability nullability;

  private JavaPojoMember(
      Name name,
      String description,
      JavaType javaType,
      Necessity necessity,
      Nullability nullability) {
    this.javaType = javaType;
    this.name = name;
    this.description = description;
    this.necessity = necessity;
    this.nullability = nullability;
  }

  public static JavaPojoMember of(
      Name name,
      String description,
      JavaType javaType,
      Necessity necessity,
      Nullability nullability) {
    return new JavaPojoMember(name, description, javaType, necessity, nullability);
  }

  public static JavaPojoMember wrap(PojoMember pojoMember, TypeMappings typeMappings) {
    final JavaType javaType = JavaType.wrap(pojoMember.getType(), typeMappings);
    return new JavaPojoMember(
        pojoMember.getName(),
        pojoMember.getDescription(),
        javaType,
        pojoMember.getNecessity(),
        pojoMember.getNullability());
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

  public Name getName() {
    return name;
  }

  public JavaType getJavaType() {
    return javaType;
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

  public Name getGetterName() {
    return prefixedMethodName("get");
  }

  public Name getGetterNameWithSuffix(PojoSettings settings) {
    return getGetterName().append(determineSuffix(settings));
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

  public Name getWitherName() {
    return prefixedMethodName("with");
  }

  public Name prefixedMethodName(String prefix) {
    if (prefix.isEmpty()) {
      return name;
    } else {
      return name.startUpperCase().prefix(prefix);
    }
  }
}
