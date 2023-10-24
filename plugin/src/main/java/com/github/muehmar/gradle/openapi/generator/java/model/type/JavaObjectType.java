package com.github.muehmar.gradle.openapi.generator.java.model.type;

import com.github.muehmar.gradle.openapi.generator.java.model.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
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
      Constraints constraints,
      ObjectType objectType,
      TypeOrigin origin) {
    super(className, objectType);
    this.constraints = constraints;
    this.origin = origin;
  }

  public static JavaObjectType fromClassName(QualifiedClassName className) {
    return new JavaObjectType(
        className,
        Constraints.empty(),
        ObjectType.ofName(PojoName.ofName(className.getClassName())),
        TypeOrigin.CUSTOM);
  }

  public static JavaObjectType fromObjectPojo(JavaObjectPojo javaObjectPojo) {
    return new JavaObjectType(
        QualifiedClassName.ofQualifiedClassName(javaObjectPojo.getClassName().asString()),
        Constraints.empty(),
        ObjectType.ofName(javaObjectPojo.getJavaPojoName().asPojoName()),
        TypeOrigin.OPENAPI);
  }

  public static JavaObjectType wrap(ObjectType objectType) {
    final QualifiedClassName className = QualifiedClassName.ofPojoName(objectType.getName());
    return new JavaObjectType(
        className, objectType.getConstraints(), objectType, TypeOrigin.OPENAPI);
  }

  @Override
  public boolean isJavaArray() {
    return false;
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
