package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.additionalproperties;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.javaTypeRefs;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.additionalproperties.AdditionalPropertiesGetter.CAST_ADDITIONAL_PROPERTY_METHOD_NAME;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.DeprecatedMethodGenerator.deprecatedJavaDocAndAnnotationForValidationMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaTypeGenerators.deepAnnotatedParameterizedClassName;
import static com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties.additionalPropertiesName;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.SettingsFunctions;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.PropertyInfoName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaModifiers;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

class StandardAdditionalPropertiesGetter {
  private StandardAdditionalPropertiesGetter() {}

  public static Generator<JavaObjectPojo, PojoSettings>
      standardAdditionalPropertiesGetterGenerator() {
    return getter().filter(pojo -> pojo.getAdditionalProperties().isAllowed());
  }

  private static Generator<JavaObjectPojo, PojoSettings> getter() {
    final Generator<JavaObjectPojo, PojoSettings> method =
        MethodGenBuilder.<JavaObjectPojo, PojoSettings>create()
            .modifiers(StandardAdditionalPropertiesGetter::standardGetterModifiers)
            .noGenericTypes()
            .returnType(
                deepAnnotatedParameterizedClassName()
                    .contraMap(
                        StandardAdditionalPropertiesGetter
                            ::createPropertyTypeForAdditionalProperties))
            .methodName(StandardAdditionalPropertiesGetter::standardGetterMethodName)
            .noArguments()
            .doesNotThrow()
            .content(getterContent().contraMap(JavaObjectPojo::getAdditionalProperties))
            .build()
            .append(javaTypeRefs(), pojo -> pojo.getAdditionalProperties().getMapContainerType());
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(deprecatedJavaDocAndAnnotationForValidationMethod())
        .append(JacksonAnnotationGenerator.jsonAnyGetter())
        .append(method);
  }

  private static JavaModifiers standardGetterModifiers(
      JavaObjectPojo pojo, PojoSettings settings1) {
    if (pojo.getAdditionalProperties().getType().getNullability().isNullable()) {
      return SettingsFunctions.validationMethodModifiers(pojo, settings1);
    } else {
      return JavaModifiers.of(PUBLIC);
    }
  }

  private static String standardGetterMethodName(JavaObjectPojo pojo, PojoSettings settings) {
    final String baseMethodName = "getAdditionalProperties";
    if (pojo.getAdditionalProperties().getType().getNullability().isNullable()) {
      return JavaName.fromString(baseMethodName)
          .append(settings.getValidationMethods().getGetterSuffix())
          .asString();
    } else {
      return baseMethodName;
    }
  }

  private static ValidationAnnotationGenerator.PropertyType
      createPropertyTypeForAdditionalProperties(JavaObjectPojo pojo) {
    final PropertyInfoName propertyInfoName =
        PropertyInfoName.fromPojoNameAndMemberName(
            pojo.getJavaPojoName(), additionalPropertiesName());
    return new ValidationAnnotationGenerator.PropertyType(
        propertyInfoName, pojo.getAdditionalProperties().getMapContainerType());
  }

  private static Generator<JavaAdditionalProperties, PojoSettings> getterContent() {
    return getterContentForSpecificType().append(getterContentForAnyType());
  }

  private static Generator<JavaAdditionalProperties, PojoSettings> getterContentForAnyType() {
    return Generator.<JavaAdditionalProperties, PojoSettings>constant(
            "return %s;", additionalPropertiesName())
        .filter(props -> props.getType().isAnyType());
  }

  private static Generator<JavaAdditionalProperties, PojoSettings> getterContentForSpecificType() {
    return Generator.<JavaAdditionalProperties, PojoSettings>emptyGen()
        .append(
            (p, s, w) ->
                w.println(
                    "final Map<String, %s> props = new HashMap<>();",
                    p.getType().getParameterizedClassName()))
        .append(constant("%s.forEach(", additionalPropertiesName()))
        .append(getterContentForSpecificNullableTypeLambda())
        .append(getterContentForSpecificNotNullableTypeLambda())
        .append(constant("return props;"))
        .append(ref(JavaRefs.JAVA_UTIL_MAP))
        .append(ref(JavaRefs.JAVA_UTIL_HASH_MAP))
        .filter(props -> not(props.getType().isAnyType()));
  }

  private static Generator<JavaAdditionalProperties, PojoSettings>
      getterContentForSpecificNullableTypeLambda() {
    return Generator.<JavaAdditionalProperties, PojoSettings>emptyGen()
        .append(constant("(key, value) ->"), 2)
        .append(constant("%s(value)", CAST_ADDITIONAL_PROPERTY_METHOD_NAME), 4)
        .append(constant(".onValue(val -> props.put(key, val))"), 6)
        .append(constant(".onNull(() -> props.put(key, null))"), 6)
        .append(constant(".onAbsent(() -> null));"), 6)
        .filter(props -> props.getType().getNullability().isNullable());
  }

  private static Generator<JavaAdditionalProperties, PojoSettings>
      getterContentForSpecificNotNullableTypeLambda() {
    return Generator.<JavaAdditionalProperties, PojoSettings>emptyGen()
        .append(constant("(key, value) ->"), 2)
        .append(
            constant(
                "%s(value).ifPresent(v -> props.put(key, v)));",
                CAST_ADDITIONAL_PROPERTY_METHOD_NAME),
            4)
        .filter(props -> props.getType().getNullability().isNotNullable());
  }
}
