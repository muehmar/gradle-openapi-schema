package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.javaTypeRefs;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaTypeGenerators.deepAnnotatedFullClassName;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
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
          + "in and {@link Optional} if present, {@link Optional#empty()} otherwise";

  private AdditionalPropertiesGetter() {}

  public static <T extends JavaPojo> Generator<T, PojoSettings> getter() {
    final Generator<JavaObjectPojo, PojoSettings> tPojoSettingsGenerator =
        standardGetter()
            .appendSingleBlankLine()
            .append(singlePropGetter())
            .contraMap(JavaObjectPojo::getAdditionalProperties)
            .filter(pojo -> pojo.getAdditionalProperties().isAllowed());
    return Generator.<T, PojoSettings>emptyGen()
        .appendOptional(tPojoSettingsGenerator, JavaPojo::asObjectPojo);
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
            .content(props -> String.format("return %s;", props.getPropertyName()))
            .build()
            .append(ref(JavaRefs.JAVA_UTIL_MAP))
            .append(javaTypeRefs(), JavaAdditionalProperties::getType);
    return Generator.<JavaAdditionalProperties, PojoSettings>emptyGen()
        .append(JacksonAnnotationGenerator.jsonAnyGetter())
        .append(method);
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
            .content(
                props ->
                    String.format(
                        "return Optional.ofNullable(%s.get(key));", props.getPropertyName()))
            .build()
            .append(ref(JavaRefs.JAVA_UTIL_OPTIONAL))
            .append(javaTypeRefs(), JavaAdditionalProperties::getType);
    return Generator.<JavaAdditionalProperties, PojoSettings>emptyGen()
        .append(JavaDocGenerator.ofJavaDocString(SINGLE_PROP_GETTER_JAVA_DOC))
        .append(method);
  }
}
