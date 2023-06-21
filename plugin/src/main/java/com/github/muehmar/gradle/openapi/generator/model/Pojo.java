package com.github.muehmar.gradle.openapi.generator.model;

import com.github.muehmar.gradle.openapi.generator.model.pojo.ArrayPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.EnumPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import java.util.Optional;
import java.util.function.Function;

public interface Pojo {
  PojoName getName();

  String getDescription();

  Pojo addObjectTypeDescription(PojoName objectTypeName, String description);

  Pojo inlineObjectReference(
      PojoName referenceName, String referenceDescription, Type referenceType);

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
