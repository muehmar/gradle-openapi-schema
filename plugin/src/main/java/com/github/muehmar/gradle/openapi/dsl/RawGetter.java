package com.github.muehmar.gradle.openapi.dsl;

import static com.github.muehmar.gradle.openapi.util.Optionals.or;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.settings.JavaModifier;
import io.github.muehmar.pojobuilder.annotations.Nullable;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.io.Serializable;
import java.util.Optional;
import lombok.Data;
import org.gradle.api.InvalidUserDataException;

@Data
@PojoBuilder
public class RawGetter implements Serializable {
  private static final String DEFAULT_MODIFIER = JavaModifier.PRIVATE.getValue();
  private static final String DEFAULT_SUFFIX = "Raw";
  private static final boolean DEFAULT_DEPRECATED_ANNOTATION = false;

  @Nullable String modifier;
  @Nullable String suffix;
  @Nullable Boolean deprecatedAnnotation;

  public RawGetter() {
    this(null, null, null);
  }

  public RawGetter(String modifier, String suffix, Boolean deprecatedAnnotation) {
    this.modifier = modifier;
    this.suffix = suffix;
    this.deprecatedAnnotation = deprecatedAnnotation;
  }

  public static RawGetter allUndefined() {
    return new RawGetter();
  }

  public RawGetter withCommonRawGetter(RawGetter commonRawGetter) {
    return RawGetterBuilder.create()
        .andAllOptionals()
        .modifier(or(getModifier(), commonRawGetter.getModifier()))
        .suffix(or(getSuffix(), commonRawGetter.getSuffix()))
        .deprecatedAnnotation(
            or(getDeprecatedAnnotation(), commonRawGetter.getDeprecatedAnnotation()))
        .build();
  }

  public Optional<String> getModifier() {
    return Optional.ofNullable(modifier);
  }

  public JavaModifier getModifierOrDefault() {
    final String modifierString = getModifier().orElse(DEFAULT_MODIFIER);

    return JavaModifier.fromString(modifierString)
        .orElseThrow(
            () ->
                new InvalidUserDataException(
                    "Unsupported value for modifier: '"
                        + modifierString
                        + "'. Supported values are ["
                        + PList.of(JavaModifier.values())
                            .map(JavaModifier::getValue)
                            .mkString(", ")));
  }

  public Optional<String> getSuffix() {
    return Optional.ofNullable(suffix);
  }

  public String getSuffixOrDefault() {
    return getSuffix().orElse(DEFAULT_SUFFIX);
  }

  public Optional<Boolean> getDeprecatedAnnotation() {
    return Optional.ofNullable(deprecatedAnnotation);
  }

  public boolean getDeprecatedAnnotationOrDefault() {
    return getDeprecatedAnnotation().orElse(DEFAULT_DEPRECATED_ANNOTATION);
  }
}
