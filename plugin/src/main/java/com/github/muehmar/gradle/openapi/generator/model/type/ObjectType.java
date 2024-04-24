package com.github.muehmar.gradle.openapi.generator.model.type;

import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import java.util.Optional;

public interface ObjectType extends Type {

  PojoName getName();

  ObjectType withNullability(Nullability nullability);

  Optional<EnumObjectType> asEnumObjectType();
}
