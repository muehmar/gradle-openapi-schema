package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.CONTAINER_OPTIONAL_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.CONTAINER_OPTIONAL_OR_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.CONTAINER_STANDARD_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.CONTAINER_TRISTATE_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.CROSS_DTO_FLAG_ACCESSOR;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.CROSS_DTO_VALUE_ACCESSOR;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.FLAG_VALIDATION_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.JSON_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.OPTIONAL_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.OPTIONAL_OR_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.STANDARD_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.TRISTATE_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.VALIDATION_GETTER;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberAndNameScope;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.AccessorProfile.Rendering;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

/** Which getters are generated for a property, derived from its {@link AccessorProfile}. */
public class GetterGroupsDefinition {

  private GetterGroupsDefinition() {}

  public static Generator<MemberAndNameScope, PojoSettings> create() {
    return (memberAndNameScope, settings, writer) ->
        chainOf(AccessorProfile.of(memberAndNameScope.getMember()))
            .generate(memberAndNameScope, settings, writer);
  }

  private static Generator<MemberAndNameScope, PojoSettings> chainOf(AccessorProfile profile) {
    return generatorsOf(profile)
        .map(GetterGenerator::create)
        .foldLeft(
            Generator.<MemberAndNameScope, PojoSettings>emptyGen(),
            (gen1, gen2) -> gen1.append(gen2).appendSingleBlankLine())
        .append(RefsGenerator.fieldRefs(), MemberAndNameScope::getMember);
  }

  /** The api accessors first, then the anchors which are not part of the api. */
  private static PList<GetterGenerator> generatorsOf(AccessorProfile profile) {
    return apiAccessors(profile)
        .add(generator(JSON_GETTER, profile))
        .concat(validationGetter(profile))
        .concat(flagAccessor(profile))
        .concat(crossDtoAccessors(profile));
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
    return profile.hasPresenceFlag() && profile.hasOwnConstraints()
        ? PList.single(generator(FLAG_VALIDATION_GETTER, profile))
        : PList.empty();
  }

  /** The accessors read by a composed dto on this dto when it is used as one of its members. */
  private static PList<GetterGenerator> crossDtoAccessors(AccessorProfile profile) {
    final PList<GetterGenerator> valueAccessor =
        profile.hasCrossDtoValueAccessor()
            ? PList.single(generator(CROSS_DTO_VALUE_ACCESSOR, profile))
            : PList.empty();
    return profile.hasCrossDtoFlagAccessor()
        ? valueAccessor.add(generator(CROSS_DTO_FLAG_ACCESSOR, profile))
        : valueAccessor;
  }

  private static GetterGenerator generator(GetterMethod getterMethod, AccessorProfile profile) {
    return new GetterGenerator(getterMethod, profile.getVisibility());
  }
}
