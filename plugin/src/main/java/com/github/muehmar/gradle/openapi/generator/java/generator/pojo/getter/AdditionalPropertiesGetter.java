package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.javaTypeRefs;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaTypeGenerators.deepAnnotatedParameterizedClassName;
import static com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties.additionalPropertiesName;
import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaAnyType.javaAnyType;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.name.PropertyInfoName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaDocGenerator;
import io.github.muehmar.codegenerator.java.MethodGen.Argument;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

public class AdditionalPropertiesGetter {
  private static final String SINGLE_PROP_GETTER_JAVA_DOC =
      "Returns the additional property with {@code key} wrapped "
          + "in an {@link Optional} if present, {@link Optional#empty()} otherwise";
  private static final String CAST_ADDITIONAL_PROPERTY_METHOD_NAME = "castAdditionalProperty";

  private AdditionalPropertiesGetter() {}

  public static Generator<JavaObjectPojo, PojoSettings> additionalPropertiesGetterGenerator() {
    return standardGetter()
        .appendSingleBlankLine()
        .append(singlePropGetter(), JavaObjectPojo::getAdditionalProperties)
        .appendSingleBlankLine()
        .append(additionalPropertyCastMethod(), JavaObjectPojo::getAdditionalProperties)
        .filter(pojo -> pojo.getAdditionalProperties().isAllowed());
  }

  private static Generator<JavaObjectPojo, PojoSettings> standardGetter() {
    final Generator<JavaObjectPojo, PojoSettings> method =
        MethodGenBuilder.<JavaObjectPojo, PojoSettings>create()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType(
                deepAnnotatedParameterizedClassName()
                    .contraMap(
                        AdditionalPropertiesGetter::createPropertyTypeForAdditionalProperties))
            .methodName("getAdditionalProperties")
            .noArguments()
            .content(standardGetterContent().contraMap(JavaObjectPojo::getAdditionalProperties))
            .build()
            .append(javaTypeRefs(), pojo -> pojo.getAdditionalProperties().getMapContainerType());
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(JacksonAnnotationGenerator.jsonAnyGetter())
        .append(method);
  }

  private static ValidationAnnotationGenerator.PropertyType
      createPropertyTypeForAdditionalProperties(JavaObjectPojo pojo) {
    final PropertyInfoName propertyInfoName =
        PropertyInfoName.fromPojoNameAndMemberName(
            pojo.getJavaPojoName(), additionalPropertiesName());
    return new ValidationAnnotationGenerator.PropertyType(
        propertyInfoName, pojo.getAdditionalProperties().getMapContainerType());
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
                    p.getType().getParameterizedClassName()))
        .append(constant("%s.forEach(", additionalPropertiesName()))
        .append(
            constant(
                String.format(
                    "(key, value) -> %s(value).ifPresent(v -> props.put(key, v)));",
                    CAST_ADDITIONAL_PROPERTY_METHOD_NAME)),
            2)
        .append(constant("return props;"))
        .append(ref(JavaRefs.JAVA_UTIL_MAP))
        .append(ref(JavaRefs.JAVA_UTIL_HASH_MAP))
        .filter(AdditionalPropertiesGetter::isNotObjectAdditionalPropertiesType);
  }

  private static Generator<JavaAdditionalProperties, PojoSettings> singlePropGetter() {
    final Generator<JavaAdditionalProperties, PojoSettings> method =
        MethodGenBuilder.<JavaAdditionalProperties, PojoSettings>create()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType(
                props -> String.format("Optional<%s>", props.getType().getParameterizedClassName()))
            .methodName("getAdditionalProperty")
            .singleArgument(ignore -> new Argument("String", "key"))
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
        .append((p, s, w) -> w.println(";"))
        .append(ref(JavaRefs.JAVA_UTIL_OPTIONAL));
  }

  private static Generator<JavaAdditionalProperties, PojoSettings> additionalPropertyCastMethod() {
    return MethodGenBuilder.<JavaAdditionalProperties, PojoSettings>create()
        .modifiers(PRIVATE)
        .noGenericTypes()
        .returnType(p -> String.format("Optional<%s>", p.getType().getParameterizedClassName()))
        .methodName(CAST_ADDITIONAL_PROPERTY_METHOD_NAME)
        .singleArgument(p -> new Argument("Object", "property"))
        .content(additionalPropertyCastMethodContent())
        .build()
        .append(ref(JavaRefs.JAVA_UTIL_OPTIONAL))
        .filter(AdditionalPropertiesGetter::isNotObjectAdditionalPropertiesType);
  }

  private static Generator<JavaAdditionalProperties, PojoSettings>
      additionalPropertyCastMethodContent() {
    return Generator.<JavaAdditionalProperties, PojoSettings>emptyGen()
        .append(constant("try {"))
        .append(
            (p, s, w) ->
                w.println(
                    "return Optional.of((%s) property);", p.getType().getParameterizedClassName()),
            1)
        .append(constant("} catch (ClassCastException e) {"))
        .append(constant("return Optional.empty();"), 1)
        .append(constant("}"))
        .append(ref(JavaRefs.JAVA_UTIL_OPTIONAL));
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
