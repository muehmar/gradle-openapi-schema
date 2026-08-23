package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSetting.NO_JAVA_DOC;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSetting.NO_JSON;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSetting.NO_VALIDATION;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSetting.PACKAGE_PRIVATE;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.CONTAINER_OPTIONAL_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.CONTAINER_OPTIONAL_OR_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.CONTAINER_STANDARD_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.CONTAINER_TRISTATE_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.FLAG_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.FLAG_VALIDATION_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.JSON_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.OPTIONAL_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.OPTIONAL_OR_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.STANDARD_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.TRISTATE_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.VALIDATION_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GroupsDefinitionBuilder.generator;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.AccessorProfile.Rendering;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.AccessorProfile.Shape;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import java.util.function.Predicate;

/**
 * Defines which getters are generated for a property.
 *
 * <p>The getters are <i>derived</i> from the {@link AccessorProfile} of the member instead of being
 * selected from a table of nested predicates. As the profile answers with a single {@link Shape}
 * which api accessor a property needs, generating two of them or none is not representable.
 *
 * <p>Every property gets one api accessor - accompanied by an {@code ...Or(defaultValue)} accessor
 * and a flag accessor where its shape has them - and a json anchor; a property carrying its own
 * constraints additionally gets a validation getter. Neither the json anchor nor the validation
 * getter is conditional on the shape of the api accessor anymore, see the issues #414 and #415.
 */
public class GetterGroupsDefinition {
  private GetterGroupsDefinition() {}

  /** One group per profile: the generators of a group all share the same member filter. */
  public static GetterGroups create() {
    return new GetterGroups(AccessorProfile.all().map(GetterGroupsDefinition::gettersOf));
  }

  private static GetterGroup gettersOf(AccessorProfile profile) {
    return new GetterGroup(hasProfile(profile), generatorsOf(profile));
  }

  private static PList<GetterGenerator> generatorsOf(AccessorProfile profile) {
    // The json anchor precedes the api accessor for a oneOf/anyOf member. This ordering is a wart
    // kept to prove that this refactoring changes no generated output; a follow-up normalizes it.
    final PList<GetterGenerator> jsonAnchorFirst =
        profile.isPackagePrivate() ? PList.single(generator(JSON_GETTER)) : PList.empty();
    final PList<GetterGenerator> jsonAnchorLast =
        profile.isPackagePrivate() ? PList.empty() : PList.single(generator(JSON_GETTER));

    return jsonAnchorFirst
        .concat(apiAccessors(profile))
        .concat(jsonAnchorLast)
        .concat(validationGetter(profile))
        .concat(flagAccessor(profile));
  }

  /**
   * The accessor returning the value in the shape of the api, plus the {@code ...Or(defaultValue)}
   * companion of an {@link Shape#OPTIONAL} one. It never carries the validation or json annotations
   * - those live on the dedicated validation getter and json anchor - and is package-private
   * without java doc for a oneOf/anyOf member.
   */
  private static PList<GetterGenerator> apiAccessors(AccessorProfile profile) {
    final boolean container = profile.getRendering() == Rendering.CONTAINER;
    final GetterGeneratorSetting[] settings = apiAccessorSettings(profile);
    switch (profile.getShape()) {
      case STANDARD:
        return PList.single(
            generator(container ? CONTAINER_STANDARD_GETTER : STANDARD_GETTER, settings));
      case OPTIONAL:
        // The ...Or(defaultValue) accessor is a convenience of the public api, hence it is not
        // generated where the property is not part of it.
        return profile.isPackagePrivate()
            ? PList.single(
                generator(container ? CONTAINER_OPTIONAL_GETTER : OPTIONAL_GETTER, settings))
            : PList.of(
                generator(container ? CONTAINER_OPTIONAL_GETTER : OPTIONAL_GETTER, settings),
                generator(container ? CONTAINER_OPTIONAL_OR_GETTER : OPTIONAL_OR_GETTER, settings));
      default:
        return PList.single(
            generator(container ? CONTAINER_TRISTATE_GETTER : TRISTATE_GETTER, settings));
    }
  }

  private static GetterGeneratorSetting[] apiAccessorSettings(AccessorProfile profile) {
    return profile.isPackagePrivate()
        ? new GetterGeneratorSetting[] {NO_VALIDATION, NO_JSON, PACKAGE_PRIVATE, NO_JAVA_DOC}
        : new GetterGeneratorSetting[] {NO_VALIDATION, NO_JSON};
  }

  private static PList<GetterGenerator> validationGetter(AccessorProfile profile) {
    return profile.hasOwnConstraints() ? PList.single(generator(VALIDATION_GETTER)) : PList.empty();
  }

  /**
   * The flag telling present from absent resp. non-null from null. It carries the
   * {@code @AssertTrue} assertion for a property with own constraints, and is a plain getter for a
   * oneOf/anyOf member, whose composition validation needs to read it.
   */
  private static PList<GetterGenerator> flagAccessor(AccessorProfile profile) {
    if (!profile.hasPresenceFlag()) {
      return PList.empty();
    } else if (profile.hasOwnConstraints()) {
      return PList.single(generator(FLAG_VALIDATION_GETTER));
    } else if (profile.isPackagePrivate()) {
      return PList.single(generator(FLAG_GETTER));
    } else {
      return PList.empty();
    }
  }

  private static Predicate<JavaPojoMember> hasProfile(AccessorProfile profile) {
    return member -> AccessorProfile.of(member).equals(profile);
  }
}
