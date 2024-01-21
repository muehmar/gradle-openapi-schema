package com.github.muehmar.gradle.openapi.generator.java.model.composition;

import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import java.util.Optional;

public interface DiscriminatableJavaComposition {

  NonEmptyList<JavaObjectPojo> getPojos();

  Optional<JavaDiscriminator> getDiscriminator();

  Type getType();

  default boolean hasDiscriminator() {
    return getDiscriminator().isPresent();
  }

  enum Type {
    ONE_OF("OneOf"),
    ANY_OF("AnyOf");

    final JavaName javaName;

    Type(String type) {
      javaName = JavaName.fromString(type);
    }

    public JavaName getName() {
      return javaName;
    }
  }
}
