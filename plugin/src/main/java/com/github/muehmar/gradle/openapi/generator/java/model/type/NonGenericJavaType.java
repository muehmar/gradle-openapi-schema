package com.github.muehmar.gradle.openapi.generator.java.model.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.ClassName;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public abstract class NonGenericJavaType extends BaseJavaType {
  protected NonGenericJavaType(ClassName className, Type type) {
    super(className, type);
  }

  @Override
  public PList<Name> getAllQualifiedClassNames() {
    return PList.single(className.getQualifiedClassName());
  }

  @Override
  public Name getFullClassName() {
    return className.getClassName();
  }
}
