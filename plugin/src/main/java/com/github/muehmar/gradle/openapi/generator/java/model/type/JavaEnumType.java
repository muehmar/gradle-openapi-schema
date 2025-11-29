package com.github.muehmar.gradle.openapi.generator.java.model.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.EnumConstantName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassNames;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.PluginApiType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.TypeMapping;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Pattern;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
public class JavaEnumType extends NonGenericJavaType {
  private final QualifiedClassName enumClassName;
  private final PList<EnumConstantName> members;
  private final Constraints constraints;

  private JavaEnumType(
      QualifiedClassName className,
      Optional<ApiType> apiType,
      QualifiedClassName enumClassName,
      PList<EnumConstantName> members,
      Nullability nullability,
      Constraints constraints) {
    super(className, apiType, nullability);
    this.enumClassName = enumClassName;
    this.members = members;
    this.constraints = constraints;
  }

  private static JavaEnumType of(
      EnumType enumType,
      TypeMapping typeMapping,
      Nullability nullability,
      Constraints constraints) {
    return new JavaEnumType(
        typeMapping.getClassName(),
        typeMapping.getApiType(),
        QualifiedClassName.ofName(enumType.getName()),
        enumType.getMembers().map(EnumConstantName::ofString),
        nullability,
        constraints);
  }

  public static JavaEnumType wrapForDiscriminator(EnumType enumType) {
    return of(
        enumType,
        TypeMapping.fromClassName(QualifiedClassName.ofName(enumType.getName())),
        Nullability.NOT_NULLABLE,
        Constraints.empty());
  }

  public static JavaType wrap(EnumType enumType, TypeMappings typeMappings) {
    final QualifiedClassName enumClassName = QualifiedClassName.ofName(enumType.getName());
    final QualifiedClassName internalClassName = QualifiedClassNames.STRING;
    final Optional<PluginApiType> pluginApiType =
        Optional.of(PluginApiType.useEnumAsApiType(enumClassName));
    final TypeMapping typeMapping =
        enumType
            .getFormat()
            .map(
                format ->
                    TypeMapping.fromFormatMappings(
                        internalClassName,
                        pluginApiType,
                        format,
                        typeMappings.getFormatTypeMappings()))
            .orElseGet(
                () -> TypeMapping.fromClassNameAndPluginApiType(internalClassName, pluginApiType));

    return typeMapping
        .getApiType()
        .<JavaType>map(
            apiType -> {
              final Nullability nullability =
                  Nullability.leastRestrictive(
                      enumType.getNullability(),
                      typeMappings.isAllowNullableForEnums()
                          ? enumType.getLegacyNullability()
                          : Nullability.NOT_NULLABLE);

              final String pattern = enumType.getMembers().mkString("|");

              final Constraints constraints =
                  Constraints.ofPattern(Pattern.ofUnescapedString(pattern));

              return of(enumType, typeMapping, nullability, constraints);
            })
        .orElse(JavaObjectType.fromClassName(typeMapping.getClassName()));
  }

  public JavaEnumType asInnerClassOf(JavaName outerClassName) {
    final QualifiedClassName newClassName =
        className.equals(QualifiedClassNames.STRING)
            ? className
            : className.asInnerClassOf(outerClassName);

    final QualifiedClassName newEnumClassName = enumClassName.asInnerClassOf(outerClassName);

    final Optional<ApiType> newApiType =
        apiType.map(api -> api.replaceClassName(enumClassName, newEnumClassName));

    return new JavaEnumType(
        newClassName, newApiType, newEnumClassName, members, getNullability(), constraints);
  }

  public QualifiedClassName getEnumClassName() {
    return enumClassName;
  }

  @Override
  public boolean isJavaArray() {
    return false;
  }

  @Override
  public JavaType withNullability(Nullability nullability) {
    return new JavaEnumType(className, apiType, enumClassName, members, nullability, constraints);
  }

  @Override
  public Constraints getConstraints() {
    return constraints;
  }

  public PList<EnumConstantName> getMembers() {
    return members;
  }

  @Override
  public <T> T fold(
      Function<JavaArrayType, T> onArrayType,
      Function<JavaBooleanType, T> onBooleanType,
      Function<JavaEnumType, T> onEnumType,
      Function<JavaMapType, T> onMapType,
      Function<JavaAnyType, T> onAnyType,
      Function<JavaNumericType, T> onNumericType,
      Function<JavaIntegerType, T> onIntegerType,
      Function<JavaObjectType, T> onObjectType,
      Function<JavaStringType, T> onStringType) {
    return onEnumType.apply(this);
  }
}
