package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder;

import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.allof.AllOfBuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.anyof.AnyOfBuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.oneof.OneOfBuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property.LastOptionalPropertyBuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property.LastRequiredPropertyBuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property.OptionalPropertyBuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property.RequiredPropertyBuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import java.util.Optional;
import java.util.function.Function;

public interface BuilderStage {
  String getName();

  static NonEmptyList<BuilderStage> createStages(
      SafeBuilderVariant builderVariant, JavaObjectPojo pojo) {
    if (pojo.isSimpleMapPojo()) {
      return OptionalPropertyBuilderStage.createStages(builderVariant, pojo);
    } else {
      return AllOfBuilderStage.createStages(builderVariant, pojo);
    }
  }

  default <T> T fold(
      Function<AllOfBuilderStage, T> onAllOfBuilderStage,
      Function<OneOfBuilderStage, T> onOneOfBuilderStage,
      Function<AnyOfBuilderStage, T> onAnyOfBuilderStage,
      Function<RequiredPropertyBuilderStage, T> onRequiredPropertyBuilderStage,
      Function<LastRequiredPropertyBuilderStage, T> onLastRequiredPropertyBuilderStage,
      Function<OptionalPropertyBuilderStage, T> onOptionalPropertyBuilderStage,
      Function<LastOptionalPropertyBuilderStage, T> onLastOptionalPropertyBuilderStage) {
    if (this instanceof AllOfBuilderStage) {
      return onAllOfBuilderStage.apply((AllOfBuilderStage) this);
    } else if (this instanceof OneOfBuilderStage) {
      return onOneOfBuilderStage.apply((OneOfBuilderStage) this);
    } else if (this instanceof AnyOfBuilderStage) {
      return onAnyOfBuilderStage.apply((AnyOfBuilderStage) this);
    } else if (this instanceof RequiredPropertyBuilderStage) {
      return onRequiredPropertyBuilderStage.apply((RequiredPropertyBuilderStage) this);
    } else if (this instanceof LastRequiredPropertyBuilderStage) {
      return onLastRequiredPropertyBuilderStage.apply((LastRequiredPropertyBuilderStage) this);
    } else if (this instanceof OptionalPropertyBuilderStage) {
      return onOptionalPropertyBuilderStage.apply((OptionalPropertyBuilderStage) this);
    } else if (this instanceof LastOptionalPropertyBuilderStage) {
      return onLastOptionalPropertyBuilderStage.apply((LastOptionalPropertyBuilderStage) this);
    } else {
      throw new IllegalStateException(
          "Unknown instance of BuilderStage: " + this.getClass().getSimpleName());
    }
  }

  default Optional<AllOfBuilderStage> asAllOfBuilderStage() {
    return fold(
        Optional::of,
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty());
  }

  default Optional<OneOfBuilderStage> asOneOfBuilderStage() {
    return fold(
        ignore -> Optional.empty(),
        Optional::of,
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty());
  }

  default Optional<AnyOfBuilderStage> asAnyOfBuilderStage() {
    return fold(
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        Optional::of,
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty());
  }

  default Optional<RequiredPropertyBuilderStage> asRequiredPropertyBuilderStage() {
    return fold(
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        Optional::of,
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty());
  }

  default Optional<LastRequiredPropertyBuilderStage> asLastRequiredPropertyBuilderStage() {
    return fold(
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        Optional::of,
        ignore -> Optional.empty(),
        ignore -> Optional.empty());
  }

  default Optional<OptionalPropertyBuilderStage> asOptionalPropertyBuilderStage() {
    return fold(
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        Optional::of,
        ignore -> Optional.empty());
  }

  default Optional<LastOptionalPropertyBuilderStage> asLastOptionalPropertyBuilderStage() {
    return fold(
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        Optional::of);
  }
}
