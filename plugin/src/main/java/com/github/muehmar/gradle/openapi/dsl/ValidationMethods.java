package com.github.muehmar.gradle.openapi.dsl;

import static com.github.muehmar.gradle.openapi.util.Optionals.or;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.settings.JavaModifier;
import io.github.muehmar.pojobuilder.annotations.Nullable;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.io.Serializable;
import java.util.Optional;
import javax.inject.Inject;
import lombok.Data;
import org.gradle.api.InvalidUserDataException;

@Data
@PojoBuilder
public class ValidationMethods implements Serializable {
  private static final String DEFAULT_MODIFIER = JavaModifier.PRIVATE.getValue();
  private static final String DEFAULT_SUFFIX = "Raw";
  private static final boolean DEFAULT_DEPRECATED_ANNOTATION = false;

  @Nullable String modifier;
  @Nullable String getterSuffix;
  @Nullable Boolean deprecatedAnnotation;

  @Inject
  public ValidationMethods() {
    this(null, null, null);
  }

  public ValidationMethods(String modifier, String getterSuffix, Boolean deprecatedAnnotation) {
    this.modifier = modifier;
    this.getterSuffix = getterSuffix;
    this.deprecatedAnnotation = deprecatedAnnotation;
  }

  public static ValidationMethods allUndefined() {
    return new ValidationMethods();
  }

  public ValidationMethods withCommonRawGetter(ValidationMethods commonValidationMethods) {
    return ValidationMethodsBuilder.create()
        .andAllOptionals()
        .modifier(or(getModifier(), commonValidationMethods.getModifier()))
        .getterSuffix(or(getGetterSuffix(), commonValidationMethods.getGetterSuffix()))
        .deprecatedAnnotation(
            or(getDeprecatedAnnotation(), commonValidationMethods.getDeprecatedAnnotation()))
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

  public Optional<String> getGetterSuffix() {
    return Optional.ofNullable(getterSuffix);
  }

  public String getGetterSuffixOrDefault() {
    return getGetterSuffix().orElse(DEFAULT_SUFFIX);
  }

  public Optional<Boolean> getDeprecatedAnnotation() {
    return Optional.ofNullable(deprecatedAnnotation);
  }

  public boolean getDeprecatedAnnotationOrDefault() {
    return getDeprecatedAnnotation().orElse(DEFAULT_DEPRECATED_ANNOTATION);
  }
}
