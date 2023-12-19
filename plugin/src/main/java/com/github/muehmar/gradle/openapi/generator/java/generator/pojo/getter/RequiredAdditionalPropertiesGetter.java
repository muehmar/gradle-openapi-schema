package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator.assertTrue;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator.notNullAnnotation;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator.validAnnotationForType;
import static com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties.additionalPropertiesName;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.DeprecatedMethodGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.SettingsFunctions;
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
        .append(validAnnotationForType(), JavaRequiredAdditionalProperty::getJavaType)
        .append(
            notNullAnnotation(JavaRequiredAdditionalProperty.class)
                .filter(JavaRequiredAdditionalProperty::isAnyType))
        .append(jsonIgnore())
        .append(getter())
        .appendSingleBlankLine()
        .append(notNullValidationGetterForSpecificType())
        .appendSingleBlankLine()
        .append(correctTypeValidationGetterForSpecificType());
  }

  private static Generator<JavaRequiredAdditionalProperty, PojoSettings> getter() {
    return MethodGenBuilder.<JavaRequiredAdditionalProperty, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(prop -> prop.getJavaType().getParameterizedClassName())
        .methodName(prop -> String.format("get%s", prop.getName().startUpperCase()))
        .noArguments()
        .doesNotThrow()
        .content(getterContent())
        .build()
        .append(RefsGenerator.javaTypeRefs(), JavaRequiredAdditionalProperty::getJavaType);
  }

  private static Generator<JavaRequiredAdditionalProperty, PojoSettings> getterContent() {
    return getterContentForAnyType().append(getterContentForSpecificType());
  }

  private static Generator<JavaRequiredAdditionalProperty, PojoSettings> getterContentForAnyType() {
    return Generator.<JavaRequiredAdditionalProperty, PojoSettings>emptyGen()
        .append(
            (prop, s, w) ->
                w.println("return %s.get(\"%s\");", additionalPropertiesName(), prop.getName()))
        .filter(JavaRequiredAdditionalProperty::isAnyType);
  }

  private static Generator<JavaRequiredAdditionalProperty, PojoSettings>
      getterContentForSpecificType() {
    return Generator.<JavaRequiredAdditionalProperty, PojoSettings>emptyGen()
        .append(constant("try {"))
        .append(
            (prop, s, w) ->
                w.println(
                    "return (%s) %s.get(\"%s\");",
                    prop.getJavaType().getParameterizedClassName(),
                    additionalPropertiesName(),
                    prop.getName()),
            1)
        .append(constant("}"))
        .append(constant("catch (ClassCastException e) {"))
        .append(constant("return null;"), 1)
        .append(constant("}"))
        .filter(JavaRequiredAdditionalProperty::isNotAnyType);
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
