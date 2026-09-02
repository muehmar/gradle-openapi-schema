package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition;

import static com.github.muehmar.gradle.openapi.generator.java.GeneratorUtil.noSettingsGen;
import static io.github.muehmar.codegenerator.java.JavaDocGenerator.javaDoc;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaModifier;
import io.github.muehmar.codegenerator.java.JavaModifiers;
import lombok.Value;

/**
 * Which accessors a property needs, derived from the property itself. The concepts behind {@link
 * Visibility} and {@link Constraints} are described in {@code doc/internal/dto_design.md}.
 */
@Value
public class AccessorProfile {

  public enum Shape {
    STANDARD,
    OPTIONAL,
    TRISTATE
  }

  public enum Rendering {
    PLAIN,
    CONTAINER
  }

  public enum Visibility {
    PUBLIC(JavaModifiers.of(JavaModifier.PUBLIC)),
    PACKAGE_PRIVATE(JavaModifiers.empty());

    private final JavaModifiers modifiers;

    Visibility(JavaModifiers modifiers) {
      this.modifiers = modifiers;
    }

    public JavaModifiers getModifiers() {
      return modifiers;
    }

    public Generator<JavaPojoMember, PojoSettings> javaDocGenerator() {
      return Generator.<JavaPojoMember, PojoSettings>emptyGen()
          .append(noSettingsGen(javaDoc()), JavaPojoMember::getDescription)
          .filter(ignore -> this == PUBLIC);
    }
  }

  public enum Constraints {
    OWN,
    DELEGATED_TO_MEMBER_DTO
  }

  Shape shape;
  Rendering rendering;
  Visibility visibility;
  Constraints constraints;

  public static AccessorProfile of(JavaPojoMember member) {
    return new AccessorProfile(
        shapeOf(member), renderingOf(member), visibilityOf(member), constraintsOf(member));
  }

  private static Shape shapeOf(JavaPojoMember member) {
    if (member.isRequiredAndNotNullable()) {
      return Shape.STANDARD;
    } else if (member.isOptionalAndNullable()) {
      return Shape.TRISTATE;
    } else {
      return Shape.OPTIONAL;
    }
  }

  private static Rendering renderingOf(JavaPojoMember member) {
    return member.getJavaType().isArrayType() || member.getJavaType().isMapType()
        ? Rendering.CONTAINER
        : Rendering.PLAIN;
  }

  private static Visibility visibilityOf(JavaPojoMember member) {
    switch (member.getType()) {
      case ONE_OF_MEMBER:
      case ANY_OF_MEMBER:
        return Visibility.PACKAGE_PRIVATE;
      case OBJECT_MEMBER:
      case ALL_OF_MEMBER:
      case ARRAY_VALUE:
        return Visibility.PUBLIC;
    }
    throw new IllegalStateException("Unhandled member type " + member.getType());
  }

  private static Constraints constraintsOf(JavaPojoMember member) {
    switch (member.getType()) {
      case OBJECT_MEMBER:
      case ARRAY_VALUE:
        return Constraints.OWN;
      case ALL_OF_MEMBER:
      case ONE_OF_MEMBER:
      case ANY_OF_MEMBER:
        return Constraints.DELEGATED_TO_MEMBER_DTO;
    }
    throw new IllegalStateException("Unhandled member type " + member.getType());
  }

  /** A required-and-nullable or optional-and-not-nullable property carries a companion flag. */
  public boolean hasPresenceFlag() {
    return shape == Shape.OPTIONAL;
  }

  public boolean hasOwnConstraints() {
    return constraints == Constraints.OWN;
  }

  public boolean isPackagePrivate() {
    return visibility == Visibility.PACKAGE_PRIVATE;
  }

  /** The flag accessor readable from outside the dto, as opposed to the validation assertion. */
  public boolean hasReadableFlagAccessor() {
    return hasPresenceFlag() && !hasOwnConstraints() && isPackagePrivate();
  }
}
