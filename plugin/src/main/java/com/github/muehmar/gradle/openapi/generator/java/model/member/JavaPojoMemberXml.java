package com.github.muehmar.gradle.openapi.generator.java.model.member;

import com.github.muehmar.gradle.openapi.generator.model.PojoMemberXml;
import java.util.Optional;
import lombok.Value;

@Value
public class JavaPojoMemberXml {
  Optional<String> name;
  Optional<Boolean> isAttribute;
  Optional<JavaArrayXml> arrayXml;

  public static JavaPojoMemberXml noDefinition() {
    return new JavaPojoMemberXml(Optional.empty(), Optional.empty(), Optional.empty());
  }

  public static JavaPojoMemberXml fromPojoMemberXml(PojoMemberXml pojoMemberXml) {
    return new JavaPojoMemberXml(
        pojoMemberXml.getName(),
        pojoMemberXml.getIsAttribute(),
        pojoMemberXml.getArrayXml().flatMap(JavaArrayXml::fromArrayXml));
  }

  public boolean hasDefinition() {
    return name.isPresent() || isAttribute.isPresent() || arrayXml.isPresent();
  }

  @Value
  public static class JavaArrayXml {
    Optional<String> wrapperName;
    Optional<Boolean> wrapped;
    Optional<String> itemName;

    public static Optional<JavaArrayXml> fromArrayXml(PojoMemberXml.ArrayXml arrayXml) {
      return Optional.of(
              new JavaArrayXml(
                  arrayXml.getWrapperName(), arrayXml.getWrapped(), arrayXml.getItemName()))
          .filter(JavaArrayXml::hasDefinition);
    }

    public boolean hasDefinition() {
      return wrapperName.isPresent() || wrapped.isPresent() || itemName.isPresent();
    }
  }
}
