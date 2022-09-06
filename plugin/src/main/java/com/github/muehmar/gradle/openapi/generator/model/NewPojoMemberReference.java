package com.github.muehmar.gradle.openapi.generator.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
/** A schema description which is a definition of a member used as reference. */
public class NewPojoMemberReference {
  private final PojoName name;
  private final String description;
  private final NewType type;

  public NewPojoMemberReference(PojoName name, String description, NewType type) {
    this.name = name;
    this.description = description;
    this.type = type;
  }

  public PojoName getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public NewType getType() {
    return type;
  }
}
