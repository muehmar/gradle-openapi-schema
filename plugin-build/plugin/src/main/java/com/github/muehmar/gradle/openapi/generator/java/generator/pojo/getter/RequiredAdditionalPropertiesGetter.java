package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ConversionGenerationMode.NO_NULL_CHECK;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ConversionGenerationMode.NULL_SAFE;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator.assertTrue;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator.notNullAnnotation;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator.validAnnotationForType;
import static com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties.additionalPropertiesName;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;
import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.DeprecatedMethodGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.SettingsFunctions;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ConversionGenerationMode;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ToApiTypeConversionRenderer;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaRequiredAdditionalProperty;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

public class RequiredAdditionalPropertiesGetter {
  private RequiredAdditionalPropertiesGetter() {}

  public static Generator<JavaObjectPojo, PojoSettings> requiredAdditionalPropertiesGetter() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(annotatedGetter(), JavaObjectPojo::getRequiredAdditionalProperties, newLine());
  }

  private static Generator<JavaRequiredAdditionalProperty, PojoSettings> annotatedGetter() {
    return Generator.<JavaRequiredAdditionalProperty, PojoSettings>emptyGen()
        .append(
            Generator.<JavaRequiredAdditionalProperty, PojoSettings>emptyGen()
                .append(validAnnotationForType(), JavaRequiredAdditionalProperty::getJavaType)
                .filter(RequiredAdditionalPropertiesGetter::validAnnotationOnPublicGetter))
        .append(
            notNullAnnotation(JavaRequiredAdditionalProperty.class)
                .filter(JavaRequiredAdditionalProperty::isAnyType)
                .filter(JavaRequiredAdditionalProperty::isNotNullable))
        .append(jsonIgnore())
        .append(getter())
        .appendSingleBlankLine()
        .append(internalValueGetter())
        .appendSingleBlankLine()
        .append(notNullValidationGetterForSpecificType())
        .appendSingleBlankLine()
        .append(presenceValidationGetterForNullableType())
        .appendSingleBlankLine()
        .append(deepValidationGetter())
        .appendSingleBlankLine()
        .append(correctTypeValidationGetterForSpecificType());
  }

  /**
   * The {@code @Valid} annotation for the deep validation is placed directly on the public getter
   * only if the getter returns the internal value unwrapped. Otherwise — the getter returns an
   * {@link Optional} for nullable properties or converts to the api type, which could not carry the
   * constraints — a separate getter reading the internal value is generated instead (see {@link
   * #deepValidationGetter()}, the exact complement of this condition).
   */
  private static boolean validAnnotationOnPublicGetter(JavaRequiredAdditionalProperty prop) {
    return prop.isNotNullable() && prop.hasNoApiType();
  }

  private static Generator<JavaRequiredAdditionalProperty, PojoSettings>
      presenceValidationGetterForNullableType() {
    return DeprecatedMethodGenerator
        .<JavaRequiredAdditionalProperty>deprecatedJavaDocAndAnnotationForValidationMethod()
        .append(assertTrue(prop -> String.format("Is required but missing: %s", prop.getName())))
        .append(
            MethodGenBuilder.<JavaRequiredAdditionalProperty, PojoSettings>create()
                .modifiers(SettingsFunctions::validationMethodModifiers)
                .noGenericTypes()
                .returnType("boolean")
                .methodName(prop -> String.format("is%sPresent", prop.getName().startUpperCase()))
                .noArguments()
                .doesNotThrow()
                .content(
                    (prop, s, w) ->
                        w.println(
                            "return %s.containsKey(\"%s\");",
                            additionalPropertiesName(), prop.getName()))
                .build())
        .filter(JavaRequiredAdditionalProperty::isNullable)
        .filter(Filters.isValidationEnabled());
  }

  private static Generator<JavaRequiredAdditionalProperty, PojoSettings> getter() {
    return MethodGenBuilder.<JavaRequiredAdditionalProperty, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(RequiredAdditionalPropertiesGetter::getterReturnType)
        .methodName(prop -> String.format("get%s", prop.getName().startUpperCase()))
        .noArguments()
        .doesNotThrow()
        .content(getterContent())
        .build()
        .append(RefsGenerator.javaTypeRefs(), JavaRequiredAdditionalProperty::getJavaType)
        .append(
            RefsGenerator.<JavaRequiredAdditionalProperty, PojoSettings>optionalRef()
                .filter(JavaRequiredAdditionalProperty::isNullable));
  }

  private static String getterReturnType(JavaRequiredAdditionalProperty prop) {
    final String className = prop.getJavaType().getWriteableParameterizedClassName().asString();
    return prop.isNullable() ? String.format("Optional<%s>", className) : className;
  }

  /**
   * Expression reading the internal value of the required additional property from the properties
   * map: null for absent values as well as for values of the wrong type, so callers never throw.
   */
  private static String internalValueReadExpression(JavaRequiredAdditionalProperty prop) {
    final String mapAccess =
        String.format("%s.get(\"%s\")", additionalPropertiesName(), prop.getName());
    if (prop.isAnyType()) {
      return mapAccess;
    }
    final String internalClassName =
        prop.getJavaType().getQualifiedClassName().getClassName().asString();
    return String.format(
        "(%s instanceof %s ? (%s) %s : null)",
        mapAccess, internalClassName, prop.getJavaType().getParameterizedClassName(), mapAccess);
  }

  /**
   * Call of the generated internal value getter, see {@link
   * MethodNames.RequiredAdditionalProperty#internalValueGetterName}.
   */
  public static String internalValueGetterCall(JavaRequiredAdditionalProperty prop) {
    return String.format(
        "%s()", MethodNames.RequiredAdditionalProperty.internalValueGetterName(prop));
  }

  /**
   * Accessor for the internal value of the required additional property, used by the public getter
   * as well as by all validation code.
   */
  private static Generator<JavaRequiredAdditionalProperty, PojoSettings> internalValueGetter() {
    return Generator.<JavaRequiredAdditionalProperty, PojoSettings>emptyGen()
        .append(jsonIgnore())
        .append(
            MethodGenBuilder.<JavaRequiredAdditionalProperty, PojoSettings>create()
                .modifiers(PRIVATE)
                .noGenericTypes()
                .returnType(prop -> prop.getJavaType().getParameterizedClassName().asString())
                .methodName(
                    prop ->
                        MethodNames.RequiredAdditionalProperty.internalValueGetterName(prop)
                            .asString())
                .noArguments()
                .doesNotThrow()
                .content((prop, s, w) -> w.println("return %s;", internalValueReadExpression(prop)))
                .build());
  }

  private static Generator<JavaRequiredAdditionalProperty, PojoSettings> getterContent() {
    return notNullableGetterContent().append(nullableGetterContent());
  }

  private static Generator<JavaRequiredAdditionalProperty, PojoSettings>
      notNullableGetterContent() {
    return Generator.<JavaRequiredAdditionalProperty, PojoSettings>emptyGen()
        .append(
            Generator.<JavaRequiredAdditionalProperty, PojoSettings>emptyGen()
                .append((prop, s, w) -> w.println("return %s;", internalValueGetterCall(prop)))
                .filter(JavaRequiredAdditionalProperty::hasNoApiType))
        .append(
            Generator.<JavaRequiredAdditionalProperty, PojoSettings>emptyGen()
                .append(
                    (prop, s, w) ->
                        w.println(
                            "final %s value = %s;",
                            prop.getJavaType().getParameterizedClassName(),
                            internalValueGetterCall(prop)))
                .append((prop, s, w) -> w.println("return %s;", toApiTypeValue(prop, NULL_SAFE)))
                .filter(JavaRequiredAdditionalProperty::hasApiType))
        .filter(JavaRequiredAdditionalProperty::isNotNullable);
  }

  private static Generator<JavaRequiredAdditionalProperty, PojoSettings> nullableGetterContent() {
    return Generator.<JavaRequiredAdditionalProperty, PojoSettings>emptyGen()
        .append(
            (prop, s, w) ->
                w.println(
                    "return Optional.ofNullable(%s)%s",
                    internalValueGetterCall(prop), prop.hasApiType() ? "" : ";"))
        .append(
            Generator.<JavaRequiredAdditionalProperty, PojoSettings>emptyGen()
                .append(
                    (prop, s, w) ->
                        w.println(".map(value -> %s);", toApiTypeValue(prop, NO_NULL_CHECK)))
                .filter(JavaRequiredAdditionalProperty::hasApiType),
            2)
        .filter(JavaRequiredAdditionalProperty::isNullable);
  }

  private static String toApiTypeValue(
      JavaRequiredAdditionalProperty prop, ConversionGenerationMode mode) {
    return prop.getJavaType()
        .getApiType()
        .map(
            apiType ->
                ToApiTypeConversionRenderer.toApiTypeConversion(apiType, "value", mode).asString())
        .orElse("value");
  }

  /**
   * Getter for the deep validation of the property, reading the internal value directly. Generated
   * whenever the {@code @Valid} annotation cannot be placed on the public getter, see {@link
   * #validAnnotationOnPublicGetter}.
   */
  private static Generator<JavaRequiredAdditionalProperty, PojoSettings> deepValidationGetter() {
    return DeprecatedMethodGenerator
        .<JavaRequiredAdditionalProperty>deprecatedJavaDocAndAnnotationForValidationMethod()
        .append(jsonIgnore())
        .append(validAnnotationForType(), JavaRequiredAdditionalProperty::getJavaType)
        .append(
            MethodGenBuilder.<JavaRequiredAdditionalProperty, PojoSettings>create()
                .modifiers(SettingsFunctions::validationMethodModifiers)
                .noGenericTypes()
                .returnType(prop -> prop.getJavaType().getParameterizedClassName().asString())
                .methodName(
                    (prop, settings) ->
                        String.format(
                            "get%s%s",
                            prop.getName().startUpperCase(),
                            settings.getValidationMethods().getGetterSuffix()))
                .noArguments()
                .doesNotThrow()
                .content((prop, s, w) -> w.println("return %s;", internalValueGetterCall(prop)))
                .build())
        .append(RefsGenerator.javaTypeRefs(), JavaRequiredAdditionalProperty::getJavaType)
        .filter(prop -> not(validAnnotationOnPublicGetter(prop)))
        .filter(prop -> ValidationAnnotationGenerator.shouldValidateDeep(prop.getJavaType()))
        .filter(Filters.isValidationEnabled());
  }

  private static Generator<JavaRequiredAdditionalProperty, PojoSettings>
      notNullValidationGetterForSpecificType() {
    return DeprecatedMethodGenerator
        .<JavaRequiredAdditionalProperty>deprecatedJavaDocAndAnnotationForValidationMethod()
        .append(notNullAnnotation())
        .append(
            MethodGenBuilder.<JavaRequiredAdditionalProperty, PojoSettings>create()
                .modifiers(SettingsFunctions::validationMethodModifiers)
                .noGenericTypes()
                .returnType("Object")
                .methodName(prop -> String.format("get%sAsObject", prop.getName().startUpperCase()))
                .noArguments()
                .doesNotThrow()
                .content(
                    (prop, s, w) ->
                        w.println(
                            "return %s.get(\"%s\");", additionalPropertiesName(), prop.getName()))
                .build())
        .append(RefsGenerator.javaTypeRefs(), JavaRequiredAdditionalProperty::getJavaType)
        .filter(JavaRequiredAdditionalProperty::isNotAnyType)
        .filter(JavaRequiredAdditionalProperty::isNotNullable)
        .filter(Filters.isValidationEnabled());
  }

  private static Generator<JavaRequiredAdditionalProperty, PojoSettings>
      correctTypeValidationGetterForSpecificType() {
    return DeprecatedMethodGenerator
        .<JavaRequiredAdditionalProperty>deprecatedJavaDocAndAnnotationForValidationMethod()
        .append(
            assertTrue(
                prop ->
                    String.format(
                        "Value is not an instance of %s",
                        prop.getJavaType().getQualifiedClassName().getClassName())))
        .append(
            MethodGenBuilder.<JavaRequiredAdditionalProperty, PojoSettings>create()
                .modifiers(SettingsFunctions::validationMethodModifiers)
                .noGenericTypes()
                .returnType("boolean")
                .methodName(
                    prop -> String.format("is%sCorrectType", prop.getName().startUpperCase()))
                .noArguments()
                .doesNotThrow()
                .content(
                    Generator.<JavaRequiredAdditionalProperty, PojoSettings>emptyGen()
                        .append(
                            (prop, s, w) ->
                                w.println(
                                    "Object value = %s.get(\"%s\");",
                                    additionalPropertiesName(), prop.getName()))
                        .append(
                            (prop, s, w) ->
                                w.println(
                                    "return value == null || value instanceof %s;",
                                    prop.getJavaType().getQualifiedClassName().getClassName())))
                .build())
        .append(RefsGenerator.javaTypeRefs(), JavaRequiredAdditionalProperty::getJavaType)
        .filter(JavaRequiredAdditionalProperty::isNotAnyType)
        .filter(Filters.isValidationEnabled());
  }
}
