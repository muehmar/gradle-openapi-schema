package com.github.muehmar.gradle.openapi.generator.java.model.type;

import com.github.muehmar.gradle.openapi.generator.java.model.ClassName;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
public class JavaObjectType extends NonGenericJavaType {
  private final Constraints constraints;

  private JavaObjectType(ClassName className, Constraints constraints, ObjectType objectType) {
    super(className, objectType);
    this.constraints = constraints;
  }

  public static JavaObjectType fromClassName(ClassName className) {
    return new JavaObjectType(
        className,
        Constraints.empty(),
        ObjectType.ofName(PojoName.ofName(className.getClassName())));
  }

  public static JavaObjectType wrap(ObjectType objectType) {
    final ClassName className = ClassName.ofName(objectType.getName().asString());
    return new JavaObjectType(className, objectType.getConstraints(), objectType);
  }

  @Override
  public boolean isJavaArray() {
    return false;
  }

  @Override
  public Constraints getConstraints() {
    return constraints;
  }

  @Override
  public <T> T fold(
      Function<JavaArrayType, T> onArrayType,
      Function<JavaBooleanType, T> onBooleanType,
      Function<JavaEnumType, T> onEnumType,
      Function<JavaMapType, T> onMapType,
      Function<JavaNoType, T> onNoType,
      Function<JavaNumericType, T> onNumericType,
      Function<JavaIntegerType, T> onIntegerType,
      Function<JavaObjectType, T> onObjectType,
      Function<JavaStringType, T> onStringType) {
    return onObjectType.apply(this);
  }
}
