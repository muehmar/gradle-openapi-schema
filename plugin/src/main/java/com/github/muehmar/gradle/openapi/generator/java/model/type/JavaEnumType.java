package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.EnumConstantName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.TypeMapping;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
public class JavaEnumType extends NonGenericJavaType {
  private final PList<EnumConstantName> members;

  private JavaEnumType(
      QualifiedClassName className,
      Optional<ApiType> apiType,
      PList<EnumConstantName> members,
      Nullability nullability) {
    super(className, apiType, nullability);
    this.members = members;
  }

  private static JavaEnumType of(
      EnumType enumType, TypeMapping typeMapping, Nullability nullability) {
    return new JavaEnumType(
        typeMapping.getClassName(),
        typeMapping.getApiType(),
        enumType.getMembers().map(EnumConstantName::ofString),
        nullability);
  }

  public static JavaEnumType wrapForDiscriminator(EnumType enumType) {
    return of(
        enumType,
        TypeMapping.fromClassName(QualifiedClassName.ofName(enumType.getName())),
        Nullability.NOT_NULLABLE);
  }

  public static JavaType wrap(EnumType enumType, TypeMappings typeMappings) {
    final QualifiedClassName className = QualifiedClassName.ofName(enumType.getName());
    final TypeMapping typeMapping =
        enumType
            .getFormat()
            .map(
                format ->
                    TypeMapping.fromFormatMappings(
                        className, Optional.empty(), format, typeMappings.getFormatTypeMappings()))
            .orElseGet(() -> TypeMapping.fromClassName(className));
    if (not(typeMapping.getApiType().isPresent())
        && not(typeMapping.getClassName().equals(className))) {
      return JavaObjectType.fromClassName(typeMapping.getClassName());
    }

    final Nullability nullability =
        Nullability.leastRestrictive(
            enumType.getNullability(),
            typeMappings.isAllowNullableForEnums()
                ? enumType.getLegacyNullability()
                : Nullability.NOT_NULLABLE);

    return of(enumType, typeMapping, nullability);
  }

  public JavaEnumType asInnerClassOf(JavaName outerClassName) {
    return new JavaEnumType(
        className.asInnerClassOf(outerClassName), apiType, members, getNullability());
  }

  @Override
  public boolean isJavaArray() {
    return false;
  }

  @Override
  public JavaType withNullability(Nullability nullability) {
    return new JavaEnumType(className, apiType, members, nullability);
  }

  @Override
  public Constraints getConstraints() {
    return Constraints.empty();
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
