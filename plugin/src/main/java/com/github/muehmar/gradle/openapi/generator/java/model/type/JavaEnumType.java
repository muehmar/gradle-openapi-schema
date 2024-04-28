package com.github.muehmar.gradle.openapi.generator.java.model.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.EnumConstantName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
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
      Optional<QualifiedClassName> apiClassName,
      PList<EnumConstantName> members,
      Nullability nullability) {
    super(className, apiClassName, nullability);
    this.members = members;
  }

  public static JavaEnumType wrap(EnumType enumType, Optional<QualifiedClassName> apiClassName) {
    final QualifiedClassName className = QualifiedClassName.ofName(enumType.getName());
    return new JavaEnumType(
        className,
        apiClassName,
        enumType.getMembers().map(EnumConstantName::ofString),
        enumType.getNullability());
  }

  public static JavaEnumType wrap(EnumType enumType) {
    return wrap(enumType, Optional.empty());
  }

  public static JavaType wrap(EnumType enumType, TypeMappings typeMappings) {
    final Optional<QualifiedClassName> apiClassName =
        enumType
            .getFormat()
            .flatMap(
                format ->
                    QualifiedClassName.fromFormatTypeMapping(
                        format, typeMappings.getFormatTypeMappings()));
    return wrap(enumType, apiClassName);
  }

  public JavaEnumType asInnerClassOf(JavaName outerClassName) {
    return new JavaEnumType(
        internalClassName.asInnerClassOf(outerClassName), apiClassName, members, getNullability());
  }

  @Override
  public boolean isJavaArray() {
    return false;
  }

  @Override
  public JavaType withNullability(Nullability nullability) {
    return new JavaEnumType(internalClassName, apiClassName, members, nullability);
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
