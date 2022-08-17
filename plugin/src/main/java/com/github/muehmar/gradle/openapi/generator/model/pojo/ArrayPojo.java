package com.github.muehmar.gradle.openapi.generator.model.pojo;

import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.NewPojo;
import com.github.muehmar.gradle.openapi.generator.model.NewType;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ArrayPojo implements NewPojo {
  private final Name name;
  private final String description;
  private final String suffix;
  private final NewType itemType;

  private ArrayPojo(Name name, String description, String suffix, NewType itemType) {
    this.name = name;
    this.description = description;
    this.suffix = suffix;
    this.itemType = itemType;
  }

  @Override
  public Name getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public <T> T fold(
      Function<ObjectPojo, T> onObjectPojo,
      Function<ArrayPojo, T> onArrayType,
      Function<EnumPojo, T> onEnumPojo) {
    return onArrayType.apply(this);
  }

  public NewType getItemType() {
    return itemType;
  }
}
