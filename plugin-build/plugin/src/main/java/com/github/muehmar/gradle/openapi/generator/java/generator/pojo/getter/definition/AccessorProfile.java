package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition;

import static com.github.muehmar.gradle.openapi.generator.java.GeneratorUtil.noSettingsGen;
import static io.github.muehmar.codegenerator.java.JavaDocGenerator.javaDoc;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.MemberNameScope;
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

  /**
   * The names of the accessors this profile actually emits which are part of the api of the
   * generated dto: they are referenced from outside the declaration and must therefore never be
   * renamed, as opposed to the anchors. See {@link MemberNameScope}.
   */
  public PList<JavaName> contractGetterNames(JavaPojoMember member, PojoSettings settings) {
    final PList<JavaName> apiGetterNames =
        shape == Shape.OPTIONAL && !isPackagePrivate()
            ? PList.of(
                member.getGetterNameWithSuffix(settings), member.getGetterName().append("Or"))
            : PList.single(member.getGetterNameWithSuffix(settings));
    return hasReadableFlagAccessor()
        ? apiGetterNames.cons(member.getFlagGetterName())
        : apiGetterNames;
  }

  /**
   * The names of the builder setters which are part of the api of the generated builder. The {@code
   * _} variant is the setter of a container with a nullable value type.
   */
  public PList<JavaName> contractSetterNames(JavaPojoMember member, PojoSettings settings) {
    final JavaName setterName = member.prefixedMethodName(settings.getBuilderMethodPrefix());
    return rendering == Rendering.CONTAINER && member.getJavaType().isNullableContainerValueType()
        ? PList.of(setterName, setterName.append("_"))
        : PList.single(setterName);
  }
}
