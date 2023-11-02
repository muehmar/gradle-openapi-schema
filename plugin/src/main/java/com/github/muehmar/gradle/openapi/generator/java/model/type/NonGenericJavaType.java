package com.github.muehmar.gradle.openapi.generator.java.model.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public abstract class NonGenericJavaType extends BaseJavaType {
  protected NonGenericJavaType(QualifiedClassName className, Type type) {
    super(className, type);
  }

  @Override
  public PList<QualifiedClassName> getAllQualifiedClassNames() {
    return PList.single(qualifiedClassName);
  }

  @Override
  public ParameterizedClassName getParameterizedClassName() {
    return ParameterizedClassName.fromNonGenericClass(qualifiedClassName);
  }
}
