package com.github.muehmar.gradle.openapi.generator.java.model.composition;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
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

  /**
   * Tags the member as discriminator member in case it is the discriminator property of this
   * composition. As the members are rederived from the variant pojos on each call, an inner
   * composition's tag never leaks into an outer composition: flattening into the outer composition
   * retags the member as plain oneOf/anyOf member first.
   */
  default JavaPojoMember tagDiscriminatorMember(JavaPojoMember member) {
    return getDiscriminator()
        .filter(discriminator -> discriminator.getPropertyName().equals(member.getName()))
        .map(discriminator -> member.asDiscriminatorMember())
        .orElse(member);
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
