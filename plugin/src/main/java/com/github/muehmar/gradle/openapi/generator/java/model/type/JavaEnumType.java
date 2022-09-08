package com.github.muehmar.gradle.openapi.generator.java.model.type;

import com.github.muehmar.gradle.openapi.generator.java.model.ClassName;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
public class JavaEnumType extends NonGenericJavaType {
  private final EnumType enumType;

  private JavaEnumType(ClassName className, EnumType enumType) {
    super(className);
    this.enumType = enumType;
  }

  public static JavaEnumType wrap(EnumType enumType) {
    final ClassName className = ClassName.ofName(enumType.getName());
    return new JavaEnumType(className, enumType);
  }
}
