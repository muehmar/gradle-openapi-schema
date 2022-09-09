package com.github.muehmar.gradle.openapi.generator.java.model.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.ClassName;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
public class JavaEnumType extends NonGenericJavaType {
  private final PList<String> members;

  private JavaEnumType(ClassName className, PList<String> members) {
    super(className);
    this.members = members;
  }

  public static JavaEnumType wrap(EnumType enumType) {
    final ClassName className = ClassName.ofName(enumType.getName());
    return new JavaEnumType(className, enumType.getMembers());
  }

  @Override
  public JavaType asPrimitive() {
    return this;
  }
}
