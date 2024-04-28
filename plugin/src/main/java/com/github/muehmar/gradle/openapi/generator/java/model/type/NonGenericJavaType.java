package com.github.muehmar.gradle.openapi.generator.java.model.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import java.util.Optional;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public abstract class NonGenericJavaType extends BaseJavaType {
  protected NonGenericJavaType(
      QualifiedClassName className,
      Optional<QualifiedClassName> apiClassName,
      Nullability nullability) {
    super(className, apiClassName, nullability);
  }

  @Override
  public PList<QualifiedClassName> getAllQualifiedClassNames() {
    return PList.of(internalClassName).concat(PList.fromOptional(apiClassName));
  }

  @Override
  public ParameterizedClassName getInternalParameterizedClassName() {
    return ParameterizedClassName.fromNonGenericClass(internalClassName);
  }

  @Override
  public Optional<ParameterizedClassName> getApiParameterizedClassName() {
    return apiClassName.map(ParameterizedClassName::fromNonGenericClass);
  }
}
