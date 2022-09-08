package com.github.muehmar.gradle.openapi.generator.java.model.type;

import com.github.muehmar.gradle.openapi.generator.java.model.ClassName;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
public class JavaObjectType extends NonGenericJavaType {
  private final ObjectType objectType;

  protected JavaObjectType(ClassName className, ObjectType objectType) {
    super(className);
    this.objectType = objectType;
  }

  public static JavaObjectType wrap(ObjectType objectType) {
    final ClassName className = ClassName.ofName(objectType.getName().asString());
    return new JavaObjectType(className, objectType);
  }
}
