package com.github.muehmar.gradle.openapi.generator.java.model.type;

import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.java.model.ClassName;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
public class JavaObjectType extends NonGenericJavaType {
  private final Constraints constraints;

  protected JavaObjectType(ClassName className, Constraints constraints) {
    super(className);
    this.constraints = constraints;
  }

  public static JavaObjectType wrap(ObjectType objectType) {
    final ClassName className = ClassName.ofName(objectType.getName().asString());
    return new JavaObjectType(className, objectType.getConstraints());
  }

  @Override
  public JavaType asPrimitive() {
    return this;
  }
}
