package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import lombok.Value;

/**
 * Which accessors a property needs, derived from the property itself instead of selected from a
 * table of predicate combinations.
 *
 * <p>Every property gets exactly one api accessor group, a json anchor and - if it carries its own
 * constraints - a validation getter. The four fields below are independent of each other and each
 * one is derived from a single aspect of the member, see the accessors of this class.
 *
 * <p>The concepts behind {@link #visibility()} and {@link #constraints()} are described in {@code
 * doc/internal/dto_design.md}.
 */
@Value
public class AccessorProfile {

  /** Shape of the api accessor, determined by necessity and nullability. */
  public enum Shape {
    /** Returns the value directly. */
    STANDARD,
    /** Returns an {@code Optional}, accompanied by an {@code ...Or(default)} accessor. */
    OPTIONAL,
    /** Returns a {@code Tristate}, distinguishing absent from present-and-null. */
    TRISTATE
  }

  /** How the value is rendered, determined by the type. */
  public enum Rendering {
    PLAIN,
    /** Array or map, whose values may need to be mapped element-wise. */
    CONTAINER
  }

  /** Visibility of the api accessor, determined by the member type. */
  public enum Visibility {
    PUBLIC,
    /**
     * A property of a {@code oneOf}/{@code anyOf} composition has no well-defined meaning at the
     * level of the composition - it belongs to whichever member schema happens to match - hence the
     * public api is the decomposition and not the property.
     */
    PACKAGE_PRIVATE
  }

  /** Where the constraints of the property live, determined by the member type. */
  public enum Constraints {
    /** The property carries its own constraints, hence a validation getter is generated. */
    OWN,
    /**
     * A composed DTO carries no constraints of its own, it validates by converting itself into its
     * member DTOs which do carry them. No validation getter is generated.
     */
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

  /**
   * All combinations of the four fields. Not every one of them is inhabited by an actual member -
   * {@code allOf} members are public but delegate their constraints, {@code oneOf}/{@code anyOf}
   * members are package-private and delegate them, and no member is package-private while carrying
   * its own constraints - but an uninhabited combination simply yields a group which never matches.
   */
  public static PList<AccessorProfile> all() {
    return PList.of(Shape.values())
        .flatMap(
            shape ->
                PList.of(Rendering.values())
                    .flatMap(
                        rendering ->
                            PList.of(Visibility.values())
                                .flatMap(
                                    visibility ->
                                        PList.of(Constraints.values())
                                            .map(
                                                constraints ->
                                                    new AccessorProfile(
                                                        shape,
                                                        rendering,
                                                        visibility,
                                                        constraints)))));
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
      default:
        return Visibility.PUBLIC;
    }
  }

  private static Constraints constraintsOf(JavaPojoMember member) {
    switch (member.getType()) {
      case OBJECT_MEMBER:
      case ARRAY_VALUE:
        return Constraints.OWN;
      default:
        return Constraints.DELEGATED_TO_MEMBER_DTO;
    }
  }

  /**
   * Whether the api accessor is accompanied by a flag accessor telling present from absent ({@code
   * required} and nullable) or non-null from null (optional and not-nullable). A {@link
   * Shape#STANDARD} property is always present and a {@link Shape#TRISTATE} one answers both
   * questions through the {@code Tristate} itself.
   */
  public boolean hasPresenceFlag() {
    return shape == Shape.OPTIONAL;
  }

  public boolean hasOwnConstraints() {
    return constraints == Constraints.OWN;
  }

  public boolean isPackagePrivate() {
    return visibility == Visibility.PACKAGE_PRIVATE;
  }
}
