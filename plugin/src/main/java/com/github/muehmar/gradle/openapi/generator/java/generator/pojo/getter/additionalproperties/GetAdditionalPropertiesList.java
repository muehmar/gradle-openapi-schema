package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.additionalproperties;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties.additionalPropertiesName;
import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_LIST;
import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_STREAM_COLLECTORS;
import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_STREAM_STREAM;
import static com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs.ADDITIONAL_PROPERTY;
import static com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs.NULLABLE_ADDITIONAL_PROPERTY;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.additionalproperties.AdditionalPropertyClassGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.additionalproperties.NullableAdditionalPropertyClassGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;

class GetAdditionalPropertiesList {
  private GetAdditionalPropertiesList() {}

  public static Generator<JavaObjectPojo, PojoSettings> getAdditionalPropertiesListGenerator() {
    return JacksonAnnotationGenerator.<JavaObjectPojo>jsonIgnore()
        .append(method(), JavaObjectPojo::getAdditionalProperties)
        .filter(pojo -> pojo.getAdditionalProperties().isAllowed());
  }

  public static Generator<JavaAdditionalProperties, PojoSettings> method() {
    return JavaGenerators.<JavaAdditionalProperties, PojoSettings>methodGen()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(GetAdditionalPropertiesList::className)
        .methodName("getAdditionalProperties")
        .noArguments()
        .doesNotThrow()
        .content(methodContent())
        .build()
        .append(ref(JAVA_UTIL_LIST));
  }

  private static String className(JavaAdditionalProperties props) {
    final String additionalPropertyClassName =
        props.getType().getNullability().isNullable()
            ? NullableAdditionalPropertyClassGenerator.CLASSNAME
            : AdditionalPropertyClassGenerator.CLASSNAME;
    return String.format(
        "List<%s<%s>>", additionalPropertyClassName, props.getType().getParameterizedClassName());
  }

  private static Generator<JavaAdditionalProperties, PojoSettings> methodContent() {
    return nullablePropertyMethodContent().append(notNullablePropertyMethodContent());
  }

  private static Generator<JavaAdditionalProperties, PojoSettings> nullablePropertyMethodContent() {
    final Generator<JavaAdditionalProperties, PojoSettings> anyTypeMap =
        Generator.<JavaAdditionalProperties, PojoSettings>constant(
                ".map(entry -> %s.ofNullable(entry.getKey(), entry.getValue()))",
                NullableAdditionalPropertyClassGenerator.CLASSNAME)
            .append(ref(NULLABLE_ADDITIONAL_PROPERTY))
            .filter(JavaAdditionalProperties::isValueAnyType);
    final Generator<JavaAdditionalProperties, PojoSettings> specificTypeMap =
        Generator.<JavaAdditionalProperties, PojoSettings>constant(".map(")
            .append(constant("entry -> "), 2)
            .append(
                constant("%s.ofNullable(", NullableAdditionalPropertyClassGenerator.CLASSNAME), 4)
            .append(constant("entry.getKey(),"), 6)
            .append(constant("%s(entry.getValue())", AdditionalPropertiesCastMethod.METHOD_NAME), 6)
            .append(constant(".onValue(val -> val)"), 8)
            .append(constant(".onNull(() -> null)"), 8)
            .append(constant(".onAbsent(() -> null)))"), 8)
            .append(ref(NULLABLE_ADDITIONAL_PROPERTY))
            .filter(JavaAdditionalProperties::isNotValueAnyType);
    return Generator.<JavaAdditionalProperties, PojoSettings>constant(
            "return %s.entrySet().stream()", additionalPropertiesName())
        .append(anyTypeMap.append(specificTypeMap), 2)
        .append(constant(".collect(Collectors.toList());"), 2)
        .append(ref(JAVA_UTIL_STREAM_COLLECTORS))
        .filter(props -> props.getType().getNullability().isNullable());
  }

  private static Generator<JavaAdditionalProperties, PojoSettings>
      notNullablePropertyMethodContent() {
    final Generator<JavaAdditionalProperties, PojoSettings> anyTypeMap =
        Generator.<JavaAdditionalProperties, PojoSettings>constant(
                ".map(entry -> new %s<>(entry.getKey(), entry.getValue()))",
                AdditionalPropertyClassGenerator.CLASSNAME)
            .append(ref(ADDITIONAL_PROPERTY))
            .filter(JavaAdditionalProperties::isValueAnyType);
    final Generator<JavaAdditionalProperties, PojoSettings> specificTypeMap =
        Generator.<JavaAdditionalProperties, PojoSettings>constant(".flatMap(")
            .append(constant("entry -> "), 2)
            .append(constant("%s(entry.getValue())", AdditionalPropertiesCastMethod.METHOD_NAME), 4)
            .append(
                constant(
                    ".map(val -> new %s<>(entry.getKey(), val))",
                    AdditionalPropertyClassGenerator.CLASSNAME),
                6)
            .append(constant(".map(Stream::of)"), 6)
            .append(constant(".orElseGet(Stream::empty))"), 6)
            .append(ref(ADDITIONAL_PROPERTY))
            .append(ref(JAVA_UTIL_STREAM_STREAM))
            .filter(JavaAdditionalProperties::isNotValueAnyType);
    return Generator.<JavaAdditionalProperties, PojoSettings>constant(
            "return %s.entrySet().stream()", additionalPropertiesName())
        .append(anyTypeMap.append(specificTypeMap), 2)
        .append(constant(".collect(Collectors.toList());"), 2)
        .append(ref(JAVA_UTIL_STREAM_COLLECTORS))
        .filter(props -> props.getType().getNullability().isNotNullable());
  }
}
