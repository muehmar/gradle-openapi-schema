package com.github.muehmar.gradle.openapi.generator.model.pojo;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.type.InlinableType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoNameMapping;
import java.util.Optional;
import java.util.function.Function;
import lombok.Value;

@Value
public class EnumPojo implements Pojo {
  ComponentName name;
  String description;
  Nullability nullability;
  PList<String> members;

  /**
   * The format declared for the enum schema. Kept so that a {@code formatTypeMapping} applies to a
   * referenced ({@code $ref}) enum exactly as it does to the identical inline enum.
   */
  Optional<String> format;

  private EnumPojo(
      ComponentName name,
      String description,
      Nullability nullability,
      PList<String> members,
      Optional<String> format) {
    this.name = name;
    this.description = description;
    this.nullability = nullability;
    this.members = members;
    this.format = format;
  }

  public static EnumPojo of(
      ComponentName name, String description, Nullability nullability, PList<String> members) {
    return of(name, description, nullability, members, Optional.empty());
  }

  public static EnumPojo of(
      ComponentName name,
      String description,
      Nullability nullability,
      PList<String> members,
      Optional<String> format) {
    return new EnumPojo(name, description, nullability, members, format);
  }

  @Override
  public Pojo replaceObjectType(
      PojoName objectTypeName, String newObjectTypeDescription, InlinableType newType) {
    return this;
  }

  @Override
  public Pojo adjustNullablePojo(PojoName nullablePojo) {
    return this;
  }

  @Override
  public EnumPojo applyMapping(PojoNameMapping pojoNameMapping) {
    final ComponentName mappedName = name.applyPojoMapping(pojoNameMapping);
    return new EnumPojo(mappedName, description, nullability, members, format);
  }

  @Override
  public Pojo replaceName(ComponentName name) {
    return new EnumPojo(name, description, nullability, members, format);
  }

  @Override
  public <T> T fold(
      Function<ObjectPojo, T> onObjectPojo,
      Function<ArrayPojo, T> onArrayType,
      Function<EnumPojo, T> onEnumPojo) {
    return onEnumPojo.apply(this);
  }
}
