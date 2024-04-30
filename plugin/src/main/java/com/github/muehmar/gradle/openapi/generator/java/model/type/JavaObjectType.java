package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;

import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
public class JavaObjectType extends NonGenericJavaType {
  private final TypeOrigin origin;
  private final Constraints constraints;

  private JavaObjectType(
      QualifiedClassName className,
      Nullability nullability,
      Constraints constraints,
      TypeOrigin origin) {
    super(className, Optional.empty(), nullability);
    this.constraints = constraints;
    this.origin = origin;
  }

  public static JavaObjectType fromClassName(QualifiedClassName className) {
    return new JavaObjectType(className, NOT_NULLABLE, Constraints.empty(), TypeOrigin.CUSTOM);
  }

  public static JavaObjectType fromObjectPojo(JavaObjectPojo javaObjectPojo) {
    return new JavaObjectType(
        QualifiedClassName.ofQualifiedClassName(javaObjectPojo.getClassName().asString()),
        NOT_NULLABLE,
        Constraints.empty(),
        TypeOrigin.OPENAPI);
  }

  public static JavaObjectType wrap(ObjectType objectType) {
    final QualifiedClassName className = QualifiedClassName.ofPojoName(objectType.getName());
    return new JavaObjectType(
        className, objectType.getNullability(), objectType.getConstraints(), TypeOrigin.OPENAPI);
  }

  @Override
  public boolean isJavaArray() {
    return false;
  }

  @Override
  public JavaType withNullability(Nullability nullability) {
    return new JavaObjectType(getQualifiedClassName(), nullability, constraints, origin);
  }

  @Override
  public Constraints getConstraints() {
    return constraints;
  }

  public TypeOrigin getOrigin() {
    return origin;
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
    return onObjectType.apply(this);
  }

  public enum TypeOrigin {
    OPENAPI,
    CUSTOM
  }
}
