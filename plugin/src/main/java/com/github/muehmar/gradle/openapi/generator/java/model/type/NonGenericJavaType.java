package com.github.muehmar.gradle.openapi.generator.java.model.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import java.util.Optional;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public abstract class NonGenericJavaType extends BaseJavaType {
  protected NonGenericJavaType(
      QualifiedClassName className, Optional<ApiType> apiType, Nullability nullability) {
    super(className, apiType, nullability);
  }

  @Override
  public PList<QualifiedClassName> getAllQualifiedClassNames() {
    return PList.of(className).concat(PList.fromOptional(apiType.map(ApiType::getClassName)));
  }

  @Override
  public ParameterizedClassName getParameterizedClassName() {
    return ParameterizedClassName.fromNonGenericClass(className);
  }
}
