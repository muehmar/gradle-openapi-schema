package com.github.muehmar.gradle.openapi.generator.java.model.composition;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TechnicalPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import java.util.Optional;

public interface DiscriminatableJavaComposition {

  NonEmptyList<JavaObjectPojo> getPojos();

  Optional<JavaDiscriminator> getDiscriminator();

  PList<TechnicalPojoMember> getPojosAsTechnicalMembers();

  Type getType();

  default boolean validateExactlyOneMatch(PojoSettings settings) {
    return getType() == Type.ONE_OF
        && not(settings.isNonStrictOneOfValidation() && hasDiscriminator());
  }

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
