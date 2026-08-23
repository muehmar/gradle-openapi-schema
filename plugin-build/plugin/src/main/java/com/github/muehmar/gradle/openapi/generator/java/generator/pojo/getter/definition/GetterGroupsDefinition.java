package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition;

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

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.AccessorProfile.Rendering;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

/** Which getters are generated for a property, derived from its {@link AccessorProfile}. */
public class GetterGroupsDefinition {

  private GetterGroupsDefinition() {}

  public static Generator<JavaPojoMember, PojoSettings> create() {
    return (member, settings, writer) ->
        chainOf(AccessorProfile.of(member)).generate(member, settings, writer);
  }

  private static Generator<JavaPojoMember, PojoSettings> chainOf(AccessorProfile profile) {
    return generatorsOf(profile)
        .map(GetterGenerator::create)
        .foldLeft(
            Generator.<JavaPojoMember, PojoSettings>emptyGen(),
            (gen1, gen2) -> gen1.append(gen2).appendSingleBlankLine())
        .append(RefsGenerator.fieldRefs());
  }

  /** The api accessors first, then the anchors which are not part of the api. */
  private static PList<GetterGenerator> generatorsOf(AccessorProfile profile) {
    return apiAccessors(profile)
        .add(generator(JSON_GETTER, profile))
        .concat(validationGetter(profile))
        .concat(flagAccessor(profile));
  }

  private static PList<GetterGenerator> apiAccessors(AccessorProfile profile) {
    final boolean container = profile.getRendering() == Rendering.CONTAINER;
    switch (profile.getShape()) {
      case STANDARD:
        return PList.single(
            generator(container ? CONTAINER_STANDARD_GETTER : STANDARD_GETTER, profile));
      case OPTIONAL:
        // The ...Or(defaultValue) accessor is a convenience of the public api only.
        return profile.isPackagePrivate()
            ? PList.single(
                generator(container ? CONTAINER_OPTIONAL_GETTER : OPTIONAL_GETTER, profile))
            : PList.of(
                generator(container ? CONTAINER_OPTIONAL_GETTER : OPTIONAL_GETTER, profile),
                generator(container ? CONTAINER_OPTIONAL_OR_GETTER : OPTIONAL_OR_GETTER, profile));
      default:
        return PList.single(
            generator(container ? CONTAINER_TRISTATE_GETTER : TRISTATE_GETTER, profile));
    }
  }

  private static PList<GetterGenerator> validationGetter(AccessorProfile profile) {
    return profile.hasOwnConstraints()
        ? PList.single(generator(VALIDATION_GETTER, profile))
        : PList.empty();
  }

  private static PList<GetterGenerator> flagAccessor(AccessorProfile profile) {
    if (profile.hasPresenceFlag() && profile.hasOwnConstraints()) {
      return PList.single(generator(FLAG_VALIDATION_GETTER, profile));
    } else if (profile.hasReadableFlagAccessor()) {
      return PList.single(generator(FLAG_GETTER, profile));
    } else {
      return PList.empty();
    }
  }

  private static GetterGenerator generator(GetterMethod getterMethod, AccessorProfile profile) {
    return new GetterGenerator(getterMethod, profile.getVisibility());
  }
}
