package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.GeneratorUtil.noSettingsGen;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.AnnotationGenerator.deprecatedRawGetter;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.ValidationGenerator.validationAnnotations;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters.isJacksonJson;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters.isValidationEnabled;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator.javaDoc;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIncludeNonNull;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonProperty;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterGenerator.OptionalNullableGetterGen;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;

public class OptionalNullableGetter {
  private OptionalNullableGetter() {}

  public static OptionalNullableGetterGen getter() {
    final Generator<JavaPojoMember, PojoSettings> gen =
        Generator.<JavaPojoMember, PojoSettings>emptyGen()
            .append(noSettingsGen(javaDoc()), JavaPojoMember::getDescription)
            .append(jsonIgnore())
            .append(tristateGetterMethod())
            .append(jacksonSerializerMethodWithAnnotations())
            .append(validationMethod())
            .append(RefsGenerator.fieldRefs());
    return OptionalNullableGetterGen.wrap(gen);
  }

  private static Generator<JavaPojoMember, PojoSettings> tristateGetterMethod() {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(f -> String.format("Tristate<%s>", f.getJavaType().getFullClassName()))
        .methodName((f, settings) -> f.getGetterNameWithSuffix(settings).asString())
        .noArguments()
        .content(
            f ->
                String.format(
                    "return Tristate.ofNullableAndNullFlag(%s, is%sNull);",
                    f.getName(), f.getName().startUpperCase()))
        .build()
        .append(w -> w.ref(OpenApiUtilRefs.TRISTATE));
  }

  private static Generator<JavaPojoMember, PojoSettings> jacksonSerializerMethodWithAnnotations() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .appendNewLine()
        .append(jsonProperty())
        .append(jsonIncludeNonNull())
        .append(jacksonSerializerMethod())
        .filter(isJacksonJson());
  }

  private static Generator<JavaPojoMember, PojoSettings> jacksonSerializerMethod() {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(PRIVATE)
        .noGenericTypes()
        .returnType("Object")
        .methodName(f -> String.format("%sJackson", f.getGetterName()))
        .noArguments()
        .content(
            f ->
                String.format(
                    "return is%sNull ? new JacksonNullContainer<>(%s) : %s;",
                    f.getName().startUpperCase(), f.getName(), f.getName()))
        .build()
        .append(RefsGenerator.fieldRefs())
        .append(w -> w.ref(OpenApiUtilRefs.JACKSON_NULL_CONTAINER))
        .filter(isJacksonJson());
  }

  private static Generator<JavaPojoMember, PojoSettings> validationMethod() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .appendNewLine()
        .append(validationAnnotations())
        .append(deprecatedRawGetter())
        .append(CommonGetter.rawGetterMethod())
        .filter(isValidationEnabled());
  }
}
