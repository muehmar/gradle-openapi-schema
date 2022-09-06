package com.github.muehmar.gradle.openapi.generator.model;

import com.github.muehmar.gradle.openapi.generator.model.pojo.ArrayPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.EnumPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import java.util.Optional;
import java.util.function.Function;

public interface NewPojo {
  PojoName getName();

  String getDescription();

  NewPojo addObjectTypeDescription(PojoName objectTypeName, String description);

  NewPojo inlineObjectReference(
      PojoName referenceName, String referenceDescription, NewType referenceType);

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
