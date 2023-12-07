package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.GeneratorUtil.noSettingsGen;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.CommonGetter.tristateGetterMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.AnnotationGenerator.deprecatedAnnotationForValidationMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters.isJacksonJson;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters.isValidationEnabled;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIncludeNonNull;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonProperty;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator.validationAnnotationsForMember;
import static io.github.muehmar.codegenerator.java.JavaDocGenerator.javaDoc;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.JavaDocGenerators;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterGenerator.GeneratorOption;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;

class OptionalNullableGetter {
  private OptionalNullableGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> optionalNullableGetterGenerator(
      GeneratorOption option) {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(noSettingsGen(javaDoc()), JavaPojoMember::getDescription)
        .append(jsonIgnore())
        .append(tristateGetterMethod(PUBLIC))
        .append(jacksonSerializerMethodWithAnnotations())
        .append(validationMethod(option))
        .append(RefsGenerator.fieldRefs())
        .filter(JavaPojoMember::isOptionalAndNullable);
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
        .doesNotThrow()
        .content(
            f ->
                String.format(
                    "return %s ? new JacksonNullContainer<>(%s) : %s;",
                    f.getIsNullFlagName(), f.getName(), f.getName()))
        .build()
        .append(RefsGenerator.fieldRefs())
        .append(w -> w.ref(OpenApiUtilRefs.JACKSON_NULL_CONTAINER))
        .filter(isJacksonJson());
  }

  private static Generator<JavaPojoMember, PojoSettings> validationMethod(GeneratorOption option) {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .appendNewLine()
        .append(JavaDocGenerators.deprecatedValidationMethodJavaDoc())
        .append(validationAnnotationsForMember())
        .append(deprecatedAnnotationForValidationMethod())
        .append(jsonIgnore())
        .append(CommonGetter.rawGetterMethod())
        .filter(isValidationEnabled())
        .filter(option.validationFilter());
  }
}
