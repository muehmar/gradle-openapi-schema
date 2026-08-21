package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.additionalproperties;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs.TRISTATE;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;

import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

public class AdditionalPropertiesCastMethod {
  static final String METHOD_NAME = "castAdditionalProperty";

  private AdditionalPropertiesCastMethod() {}

  public static Generator<JavaObjectPojo, PojoSettings> additionalPropertiesCastMethodGenerator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(castNotNullableAdditionalPropertyMethod(), JavaObjectPojo::getAdditionalProperties)
        .append(castNullableAdditionalPropertyMethod(), JavaObjectPojo::getAdditionalProperties)
        .filter(pojo -> pojo.getAdditionalProperties().isNotValueAnyType());
  }

  private static Generator<JavaAdditionalProperties, PojoSettings>
      castNotNullableAdditionalPropertyMethod() {
    return MethodGenBuilder.<JavaAdditionalProperties, PojoSettings>create()
        .modifiers(PRIVATE)
        .noGenericTypes()
        .returnType(p -> String.format("Optional<%s>", p.getType().getParameterizedClassName()))
        .methodName(METHOD_NAME)
        .singleArgument(p -> new MethodGen.Argument("Object", "property"))
        .doesNotThrow()
        .content(castNotNullableAdditionalPropertyMethodContent())
        .build()
        .append(ref(JAVA_UTIL_OPTIONAL))
        .filter(props -> props.getType().getNullability().isNotNullable());
  }

  private static Generator<JavaAdditionalProperties, PojoSettings>
      castNotNullableAdditionalPropertyMethodContent() {
    return Generator.<JavaAdditionalProperties, PojoSettings>emptyGen()
        .append(constant("if (property == null) {"))
        .append(constant("return Optional.empty();"), 1)
        .append(constant("}"))
        .append(constant("try {"))
        .append(
            (p, s, w) ->
                w.println(
                    "return Optional.of((%s) property);", p.getType().getParameterizedClassName()),
            1)
        .append(constant("} catch (ClassCastException e) {"))
        .append(constant("return Optional.empty();"), 1)
        .append(constant("}"))
        .append(ref(JAVA_UTIL_OPTIONAL));
  }

  private static Generator<JavaAdditionalProperties, PojoSettings>
      castNullableAdditionalPropertyMethod() {
    return MethodGenBuilder.<JavaAdditionalProperties, PojoSettings>create()
        .modifiers(PRIVATE)
        .noGenericTypes()
        .returnType(p -> String.format("Tristate<%s>", p.getType().getParameterizedClassName()))
        .methodName(METHOD_NAME)
        .singleArgument(p -> new MethodGen.Argument("Object", "property"))
        .doesNotThrow()
        .content(castNullableAdditionalPropertyMethodContent())
        .build()
        .append(ref(TRISTATE))
        .filter(props -> props.getType().getNullability().isNullable());
  }

  private static Generator<JavaAdditionalProperties, PojoSettings>
      castNullableAdditionalPropertyMethodContent() {
    return Generator.<JavaAdditionalProperties, PojoSettings>emptyGen()
        .append(constant("if (property == null) {"))
        .append(constant("return Tristate.ofNull();"), 1)
        .append(constant("}"))
        .append(constant("try {"))
        .append(
            (p, s, w) ->
                w.println(
                    "return Tristate.ofValue((%s) property);",
                    p.getType().getParameterizedClassName()),
            1)
        .append(constant("} catch (ClassCastException e) {"))
        .append(constant("return Tristate.ofAbsent();"), 1)
        .append(constant("}"))
        .append(ref(TRISTATE));
  }
}
