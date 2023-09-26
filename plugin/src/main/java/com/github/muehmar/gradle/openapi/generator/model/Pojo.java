package com.github.muehmar.gradle.openapi.generator.model;

import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ArrayPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.EnumPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoNameMapping;
import java.util.Optional;
import java.util.function.Function;

public interface Pojo {
  ComponentName getName();

  String getDescription();

  Pojo addObjectTypeDescription(PojoName objectTypeName, String description);

  Pojo inlineObjectReference(
      PojoName referenceName, String referenceDescription, Type referenceType);

  Pojo applyMapping(PojoNameMapping pojoNameMapping);

  <T> T fold(
      Function<ObjectPojo, T> onObjectPojo,
      Function<ArrayPojo, T> onArrayType,
      Function<EnumPojo, T> onEnumPojo);

  default Optional<EnumPojo> asEnumPojo() {
    return fold(objectPojo -> Optional.empty(), arrayPojo -> Optional.empty(), Optional::of);
  }

  default Optional<ObjectPojo> asObjectPojo() {
    return fold(Optional::of, arrayPojo -> Optional.empty(), enumPojo -> Optional.empty());
  }
}
