package com.github.muehmar.gradle.openapi.dsl;

import static com.github.muehmar.gradle.openapi.util.Optionals.or;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.settings.JavaModifier;
import io.github.muehmar.pojoextension.annotations.Nullable;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import java.io.Serializable;
import java.util.Optional;
import lombok.Data;
import org.gradle.api.InvalidUserDataException;

@Data
@PojoExtension
public class ValidationGetter implements Serializable, ValidationGetterExtension {
  private static final String DEFAULT_MODIFIER = JavaModifier.PRIVATE.getValue();
  private static final String DEFAULT_SUFFIX = "Validation";
  private static final boolean DEFAULT_DEPRECATED_ANNOTATION = false;

  @Nullable String modifier;
  @Nullable String suffix;
  @Nullable Boolean deprecatedAnnotation;

  public ValidationGetter() {
    this(null, null, null);
  }

  public ValidationGetter(String modifier, String suffix, Boolean deprecatedAnnotation) {
    this.modifier = modifier;
    this.suffix = suffix;
    this.deprecatedAnnotation = deprecatedAnnotation;
  }

  public static ValidationGetter allUndefined() {
    return new ValidationGetter();
  }

  public ValidationGetter withCommonValidationGetter(ValidationGetter commonValidationGetter) {
    return ValidationGetterBuilder.create()
        .andAllOptionals()
        .modifier(or(getModifier(), commonValidationGetter.getModifier()))
        .suffix(or(getSuffix(), commonValidationGetter.getSuffix()))
        .deprecatedAnnotation(
            or(getDeprecatedAnnotation(), commonValidationGetter.getDeprecatedAnnotation()))
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
    return getSuffixOr(DEFAULT_SUFFIX);
  }

  public Optional<Boolean> getDeprecatedAnnotation() {
    return Optional.ofNullable(deprecatedAnnotation);
  }

  public boolean getDeprecatedAnnotationOrDefault() {
    return getDeprecatedAnnotationOr(DEFAULT_DEPRECATED_ANNOTATION);
  }
}
