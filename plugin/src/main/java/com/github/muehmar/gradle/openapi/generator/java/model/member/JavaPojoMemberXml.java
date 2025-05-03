package com.github.muehmar.gradle.openapi.generator.java.model.member;

import com.github.muehmar.gradle.openapi.generator.model.PojoMemberXml;
import java.util.Optional;
import lombok.Value;

@Value
public class JavaPojoMemberXml {
  Optional<String> name;
  Optional<Boolean> isAttribute;

  public static JavaPojoMemberXml noDefinition() {
    return new JavaPojoMemberXml(Optional.empty(), Optional.empty());
  }

  public static JavaPojoMemberXml fromPojoMemberXml(PojoMemberXml pojoMemberXml) {
    return new JavaPojoMemberXml(pojoMemberXml.getName(), pojoMemberXml.getIsAttribute());
  }

  public boolean hasDefinition() {
    return name.isPresent() || isAttribute.isPresent();
  }
}
