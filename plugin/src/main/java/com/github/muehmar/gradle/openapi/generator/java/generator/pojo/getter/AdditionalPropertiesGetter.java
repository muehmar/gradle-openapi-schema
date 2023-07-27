package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.javaTypeRefs;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaTypeGenerators.deepAnnotatedFullClassName;
import static com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties.additionalPropertiesName;
import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaAnyType.javaAnyType;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaMapType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.model.type.MapType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

public class AdditionalPropertiesGetter {
  private static final String SINGLE_PROP_GETTER_JAVA_DOC =
      "Returns the additional property with {@code key} wrapped "
          + "in an {@link Optional} if present, {@link Optional#empty()} otherwise";
  private static final String CAST_ADDITIONAL_PROPERTY_METHOD_NAME = "castAdditionalProperty";

  private AdditionalPropertiesGetter() {}

  public static <T extends JavaPojo>
      Generator<T, PojoSettings> additionalPropertiesGetterGenerator() {
    final Generator<JavaObjectPojo, PojoSettings> getterGenerator =
        standardGetter()
            .appendSingleBlankLine()
            .append(singlePropGetter())
            .appendSingleBlankLine()
            .append(additionalPropertyCastMethod())
            .contraMap(JavaObjectPojo::getAdditionalProperties)
            .filter(pojo -> pojo.getAdditionalProperties().isAllowed());
    return Generator.<T, PojoSettings>emptyGen()
        .appendOptional(getterGenerator, JavaPojo::asObjectPojo);
  }

  private static Generator<JavaAdditionalProperties, PojoSettings> standardGetter() {
    final Generator<JavaAdditionalProperties, PojoSettings> method =
        MethodGenBuilder.<JavaAdditionalProperties, PojoSettings>create()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType(
                deepAnnotatedFullClassName()
                    .contraMap(AdditionalPropertiesGetter::createMapTypeFromValueType))
            .methodName("getAdditionalProperties")
            .noArguments()
            .content(standardGetterContent())
            .build()
            .append(ref(JavaRefs.JAVA_UTIL_MAP))
            .append(javaTypeRefs(), JavaAdditionalProperties::getType);
    return Generator.<JavaAdditionalProperties, PojoSettings>emptyGen()
        .append(JacksonAnnotationGenerator.jsonAnyGetter())
        .append(method);
  }

  private static Generator<JavaAdditionalProperties, PojoSettings> standardGetterContent() {
    return Generator.<JavaAdditionalProperties, PojoSettings>constant(
            "return %s;", additionalPropertiesName())
        .filter(AdditionalPropertiesGetter::isObjectAdditionalPropertiesType)
        .append(standardGetterContentForNonObjectValueType());
  }

  private static Generator<JavaAdditionalProperties, PojoSettings>
      standardGetterContentForNonObjectValueType() {
    return Generator.<JavaAdditionalProperties, PojoSettings>emptyGen()
        .append(
            (p, s, w) ->
                w.println(
                    "final Map<String, %s> props = new HashMap<>();",
                    p.getType().getFullClassName()))
        .append(constant("%s.forEach(", additionalPropertiesName()))
        .append(
            constant(
                String.format(
                    "(key, value) -> %s(value).ifPresent(v -> props.put(key, v)));",
                    CAST_ADDITIONAL_PROPERTY_METHOD_NAME)),
            2)
        .append(constant("return props;"))
        .filter(AdditionalPropertiesGetter::isNotObjectAdditionalPropertiesType);
  }

  private static JavaType createMapTypeFromValueType(JavaAdditionalProperties props) {
    final MapType mapType =
        MapType.ofKeyAndValueType(StringType.noFormat(), props.getType().getType());
    return JavaMapType.wrap(mapType, TypeMappings.empty());
  }

  private static Generator<JavaAdditionalProperties, PojoSettings> singlePropGetter() {
    final Generator<JavaAdditionalProperties, PojoSettings> method =
        MethodGenBuilder.<JavaAdditionalProperties, PojoSettings>create()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType(props -> String.format("Optional<%s>", props.getType().getFullClassName()))
            .methodName("getAdditionalProperty")
            .singleArgument(ignore -> "String key")
            .content(singlePropGetterContent())
            .build()
            .append(ref(JavaRefs.JAVA_UTIL_OPTIONAL))
            .append(javaTypeRefs(), JavaAdditionalProperties::getType);
    return Generator.<JavaAdditionalProperties, PojoSettings>emptyGen()
        .append(JavaDocGenerator.ofJavaDocString(SINGLE_PROP_GETTER_JAVA_DOC))
        .append(method);
  }

  private static Generator<JavaAdditionalProperties, PojoSettings> singlePropGetterContent() {
    final Generator<JavaAdditionalProperties, PojoSettings> flatMapCastProperty =
        Generator.<JavaAdditionalProperties, PojoSettings>emptyGen()
            .append(
                (p, s, w) ->
                    w.println()
                        .tab(2)
                        .print(".flatMap(this::%s)", CAST_ADDITIONAL_PROPERTY_METHOD_NAME))
            .filter(AdditionalPropertiesGetter::isNotObjectAdditionalPropertiesType);
    return Generator.<JavaAdditionalProperties, PojoSettings>emptyGen()
        .append(
            (p, s, w) ->
                w.print("return Optional.ofNullable(%s.get(key))", additionalPropertiesName()))
        .append(flatMapCastProperty)
        .append((p, s, w) -> w.println(";"));
  }

  private static Generator<JavaAdditionalProperties, PojoSettings> additionalPropertyCastMethod() {
    return MethodGenBuilder.<JavaAdditionalProperties, PojoSettings>create()
        .modifiers(PRIVATE)
        .noGenericTypes()
        .returnType(p -> String.format("Optional<%s>", p.getType().getFullClassName()))
        .methodName(CAST_ADDITIONAL_PROPERTY_METHOD_NAME)
        .singleArgument(p -> "Object property")
        .content(additionalPropertyCastMethodContent())
        .build()
        .filter(AdditionalPropertiesGetter::isNotObjectAdditionalPropertiesType);
  }

  private static Generator<JavaAdditionalProperties, PojoSettings>
      additionalPropertyCastMethodContent() {
    return Generator.<JavaAdditionalProperties, PojoSettings>emptyGen()
        .append(constant("try {"))
        .append(
            (p, s, w) ->
                w.println("return Optional.of((%s) property);", p.getType().getFullClassName()),
            1)
        .append(constant("} catch (ClassCastException e) {"))
        .append(constant("return Optional.empty();"), 1)
        .append(constant("}"));
  }

  private static boolean isObjectAdditionalPropertiesType(
      JavaAdditionalProperties additionalProperties) {
    return additionalProperties.getType().equals(javaAnyType());
  }

  private static boolean isNotObjectAdditionalPropertiesType(
      JavaAdditionalProperties additionalProperties) {
    return not(isObjectAdditionalPropertiesType(additionalProperties));
  }
}
