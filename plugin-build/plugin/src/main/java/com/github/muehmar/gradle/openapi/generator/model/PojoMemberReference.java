package com.github.muehmar.gradle.openapi.generator.model;

import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.type.InlinableType;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
/**
 * A schema description which is a definition of a member used as reference. Such a schema is not
 * mapped to a pojo but inlined at its usages, hence its type is an {@link InlinableType}.
 */
public class PojoMemberReference {
  private final PojoName name;
  private final String description;
  private final InlinableType type;

  public PojoMemberReference(PojoName name, String description, InlinableType type) {
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

  public InlinableType getType() {
    return type;
  }

  public PojoMemberReference makeNullable() {
    return new PojoMemberReference(name, description, type.makeNullable());
  }
}
