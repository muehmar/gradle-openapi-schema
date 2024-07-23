package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaTypeGenerators.deepAnnotatedParameterizedClassName;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSettings;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.DeprecatedMethodGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.SettingsFunctions;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.java.JavaModifier;
import io.github.muehmar.codegenerator.java.JavaModifiers;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

/** This getter is only used for serialisation and/or validation. */
public class FrameworkGetter {
  private FrameworkGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> frameworkGetter(
      GetterGeneratorSettings generatorSettings) {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(deprecatedJavaDocAndAnnotationForValidationMethod(generatorSettings))
        .append(generatorSettings.validationAnnotationGenerator())
        .append(jsonIgnore(generatorSettings))
        .append(jsonProperty(generatorSettings))
        .append(jsonIncludeNonNull(generatorSettings))
        .append(frameworkGetterMethod(generatorSettings))
        .filter(frameworkGetterFilter(generatorSettings));
  }

  private static BiPredicate<JavaPojoMember, PojoSettings> frameworkGetterFilter(
      GetterGeneratorSettings generatorSettings) {
    return (member, settings) ->
        (settings.isJacksonJson() && generatorSettings.isJson())
            || (settings.isEnableValidation() && generatorSettings.isValidation());
  }

  private static Generator<JavaPojoMember, PojoSettings> frameworkGetterMethod(
      GetterGeneratorSettings generatorSettings) {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(modifiers(generatorSettings))
        .noGenericTypes()
        .returnType(returnType(generatorSettings))
        .methodName(methodName(generatorSettings))
        .noArguments()
        .doesNotThrow()
        .content(f -> String.format("return %s;", f.getName()))
        .build();
  }

  private static BiFunction<JavaPojoMember, PojoSettings, JavaModifiers> modifiers(
      GetterGeneratorSettings generatorSettings) {
    if (generatorSettings.isValidation()) {
      return SettingsFunctions::validationMethodModifiers;
    } else {
      return (m, s) -> JavaModifiers.of(JavaModifier.PRIVATE);
    }
  }

  private static BiFunction<JavaPojoMember, PojoSettings, String> methodName(
      GetterGeneratorSettings generatorSettings) {
    if (generatorSettings.isValidation()) {
      return (m, s) -> m.getValidationGetterName(s).asString();
    } else {
      return (m, s) -> m.getGetterName().append("Json").asString();
    }
  }

  private static Generator<JavaPojoMember, PojoSettings> returnType(
      GetterGeneratorSettings generatorSettings) {

    final Generator<JavaPojoMember, PojoSettings> noValidationReturnType =
        Generator.<JavaPojoMember, PojoSettings>emptyGen()
            .append((m, s, w) -> w.println("%s", m.getJavaType().getParameterizedClassName()))
            .filter(generatorSettings.<JavaPojoMember>validationFilter().negate());

    final Generator<JavaPojoMember, PojoSettings> validationReturnType =
        deepAnnotatedParameterizedClassName()
            .contraMap(ValidationAnnotationGenerator.PropertyType::fromMember)
            .filter(generatorSettings.validationFilter());

    return validationReturnType.append(noValidationReturnType);
  }

  private static Generator<JavaPojoMember, PojoSettings>
      deprecatedJavaDocAndAnnotationForValidationMethod(GetterGeneratorSettings generatorSettings) {
    if (generatorSettings.isValidation()) {
      return DeprecatedMethodGenerator.deprecatedJavaDocAndAnnotationForValidationMethod();
    } else {
      return Generator.emptyGen();
    }
  }

  private static Generator<JavaPojoMember, PojoSettings> jsonIgnore(
      GetterGeneratorSettings generatorSettings) {
    return JacksonAnnotationGenerator.<JavaPojoMember>jsonIgnore()
        .filter(generatorSettings.<JavaPojoMember>jsonFilter().negate());
  }

  private static Generator<JavaPojoMember, PojoSettings> jsonProperty(
      GetterGeneratorSettings generatorSettings) {
    return JacksonAnnotationGenerator.jsonProperty().filter(generatorSettings.jsonFilter());
  }

  private static Generator<JavaPojoMember, PojoSettings> jsonIncludeNonNull(
      GetterGeneratorSettings generatorSettings) {
    return JacksonAnnotationGenerator.<JavaPojoMember>jsonIncludeNonNull()
        .filter(generatorSettings.jsonFilter())
        .filter(JavaPojoMember::isOptionalAndNotNullable);
  }
}
