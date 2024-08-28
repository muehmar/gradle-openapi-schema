package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.additionalproperties;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.javaTypeRefs;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ConversionGenerationMode.NO_NULL_CHECK;
import static com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties.additionalPropertiesName;
import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs.TRISTATE;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ToApiTypeConversion;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedApiClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaDocGenerator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

class SingleAdditionalPropertyGetter {
  private static final String NOT_NULLABLE_PROPERTIES_JAVA_DOC =
      "Returns the additional property with {@code key} wrapped "
          + "in an {@link Optional} if present, {@link Optional#empty()} otherwise";
  private static final String NULLABLE_PROPERTIES_JAVA_DOC =
      "Returns the additional property with {@code key} where the {@link Tristate} class represents "
          + "the possible three states of the property: present and non-null, present and null, absent.";

  private SingleAdditionalPropertyGetter() {}

  public static Generator<JavaObjectPojo, PojoSettings> singleAdditionalPropertyGetterGenerator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(javaDoc(), JavaObjectPojo::getAdditionalProperties)
        .append(getterMethod(), JavaObjectPojo::getAdditionalProperties)
        .filter(pojo -> pojo.getAdditionalProperties().isAllowed());
  }

  private static Generator<JavaAdditionalProperties, PojoSettings> getterMethod() {
    return MethodGenBuilder.<JavaAdditionalProperties, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(SingleAdditionalPropertyGetter::methodReturnType)
        .methodName("getAdditionalProperty")
        .singleArgument(ignore -> new MethodGen.Argument("String", "key"))
        .doesNotThrow()
        .content(getterMethodContent())
        .build()
        .append(javaTypeRefs(), JavaAdditionalProperties::getType);
  }

  private static Generator<JavaAdditionalProperties, PojoSettings> javaDoc() {
    final Generator<JavaAdditionalProperties, PojoSettings> nullableJavaDoc =
        JavaDocGenerator.<JavaAdditionalProperties, PojoSettings>ofJavaDocString(
                NULLABLE_PROPERTIES_JAVA_DOC)
            .filter(props -> props.getType().getNullability().isNullable());
    final Generator<JavaAdditionalProperties, PojoSettings> notNullableJavaDoc =
        JavaDocGenerator.<JavaAdditionalProperties, PojoSettings>ofJavaDocString(
                NOT_NULLABLE_PROPERTIES_JAVA_DOC)
            .filter(props -> props.getType().getNullability().isNotNullable());
    return nullableJavaDoc.append(notNullableJavaDoc);
  }

  private static String methodReturnType(JavaAdditionalProperties props) {
    final String parameterizedClassName =
        ParameterizedApiClassName.fromJavaType(props.getType())
            .map(ParameterizedApiClassName::asString)
            .orElse(props.getType().getParameterizedClassName().asString());
    props.getType().getParameterizedClassName();
    if (props.getType().getNullability().isNullable()) {
      return String.format("Tristate<%s>", parameterizedClassName);
    } else {
      return String.format("Optional<%s>", parameterizedClassName);
    }
  }

  private static Generator<JavaAdditionalProperties, PojoSettings> getterMethodContent() {
    return nullablePropertyGetterContent().append(notNullablePropertyGetterContent());
  }

  private static Generator<JavaAdditionalProperties, PojoSettings> nullablePropertyGetterContent() {
    final Generator<JavaAdditionalProperties, PojoSettings> anyTypeConversion =
        Generator.<JavaAdditionalProperties, PojoSettings>constant(
                "return Optional.ofNullable(%s.get(key))", additionalPropertiesName())
            .append(constant(".map(Tristate::ofValue)"), 2)
            .append(constant(".orElseGet(Tristate::ofNull);"), 2)
            .append(ref(JAVA_UTIL_OPTIONAL))
            .append(ref(TRISTATE))
            .filter(JavaAdditionalProperties::isValueAnyType);
    final Generator<JavaAdditionalProperties, PojoSettings> specificTypeConversion =
        Generator.<JavaAdditionalProperties, PojoSettings>of(
                (props, s, w) ->
                    w.println(
                        "return %s(%s.get(key))%s;",
                        AdditionalPropertiesCastMethod.METHOD_NAME,
                        additionalPropertiesName(),
                        apiTypeMapping(props)))
            .filter(JavaAdditionalProperties::isNotValueAnyType);
    return Generator.<JavaAdditionalProperties, PojoSettings>constant(
            "if (%s.containsKey(key)) {", additionalPropertiesName())
        .append(anyTypeConversion.append(specificTypeConversion), 1)
        .append(constant("} else {"))
        .append(constant("return Tristate.ofAbsent();"), 1)
        .append(constant("}"))
        .append(ref(TRISTATE))
        .filter(props -> props.getType().getNullability().isNullable());
  }

  private static Generator<JavaAdditionalProperties, PojoSettings>
      notNullablePropertyGetterContent() {
    final Generator<JavaAdditionalProperties, PojoSettings> anyTypeConversion =
        Generator.<JavaAdditionalProperties, PojoSettings>constant(
                "return Optional.ofNullable(%s.get(key));", additionalPropertiesName())
            .append(ref(JAVA_UTIL_OPTIONAL))
            .filter(JavaAdditionalProperties::isValueAnyType);
    final Generator<JavaAdditionalProperties, PojoSettings> specificTypeConversion =
        Generator.<JavaAdditionalProperties, PojoSettings>constant(
                "return Optional.ofNullable(%s.get(key))", additionalPropertiesName())
            .append(
                (props, s, w) ->
                    w.println(
                        ".flatMap(this::%s)%s;",
                        AdditionalPropertiesCastMethod.METHOD_NAME, apiTypeMapping(props)),
                2)
            .append(ref(JAVA_UTIL_OPTIONAL))
            .filter(JavaAdditionalProperties::isNotValueAnyType);
    return anyTypeConversion
        .append(specificTypeConversion)
        .filter(props -> props.getType().getNullability().isNotNullable());
  }

  private static String apiTypeMapping(JavaAdditionalProperties props) {
    return props
        .getType()
        .getApiType()
        .map(apiType -> ToApiTypeConversion.toApiTypeConversion(apiType, "val", NO_NULL_CHECK))
        .map(conversionWriter -> String.format(".map(val -> %s)", conversionWriter.asString()))
        .orElse("");
  }
}
