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

  Pojo replaceObjectType(
      PojoName objectTypeName, String newObjectTypeDescription, Type newObjectType);

  Pojo adjustNullablePojo(PojoName nullablePojo);

  Pojo applyMapping(PojoNameMapping pojoNameMapping);

  Pojo replaceName(ComponentName name);

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
