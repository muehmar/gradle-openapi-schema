package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.nullableitemslist;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.CommonGetter.getterName;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.CommonGetter.jacksonSerialisationMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterGenerator.GeneratorOption.STANDARD;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.DeprecatedMethodGenerator.deprecatedJavaDocAndAnnotationForValidationMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters.isJacksonJsonOrValidation;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator.validationAnnotationsForMember;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.CommonGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;

class OptionalNullableGetter {
  private OptionalNullableGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> optionalNullableGetter(
      GetterGenerator.GeneratorOption option) {
    return tristateGetterMethod()
        .appendSingleBlankLine()
        .append(jacksonSerialisationMethod())
        .appendSingleBlankLine()
        .append(validationGetter().filter(option.validationFilter()))
        .filter(JavaPojoMember::isOptionalAndNullable);
  }

  private static Generator<JavaPojoMember, PojoSettings> tristateGetterMethod() {
    final Generator<JavaPojoMember, PojoSettings> method =
        JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType(
                f ->
                    String.format(
                        "Tristate<%s>",
                        f.getJavaType()
                            .getParameterizedClassName()
                            .asStringWrappingNullableValueType()))
            .methodName(getterName())
            .noArguments()
            .doesNotThrow()
            .content(
                f ->
                    String.format(
                        "return Tristate.ofNullableAndNullFlag(%s(%s), %s);",
                        WrapNullableItemsListMethod.METHOD_NAME,
                        f.getName(),
                        f.getIsNullFlagName()))
            .build()
            .append(w -> w.ref(OpenApiUtilRefs.TRISTATE));
    return JacksonAnnotationGenerator.<JavaPojoMember>jsonIgnore().append(method);
  }

  private static Generator<JavaPojoMember, PojoSettings> validationGetter() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .appendNewLine()
        .append(deprecatedJavaDocAndAnnotationForValidationMethod())
        .append(jsonIgnore())
        .append(validationAnnotationsForMember())
        .append(CommonGetter.rawGetterMethod(STANDARD))
        .filter(isJacksonJsonOrValidation());
  }
}
