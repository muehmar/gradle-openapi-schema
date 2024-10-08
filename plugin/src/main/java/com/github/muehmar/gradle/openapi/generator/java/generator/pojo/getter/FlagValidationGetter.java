package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.DeprecatedMethodGenerator.deprecatedJavaDocAndAnnotationForValidationMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator.assertTrue;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.SettingsFunctions;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;

public class FlagValidationGetter {
  private FlagValidationGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> flagValidationGetterGenerator() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(deprecatedJavaDocAndAnnotationForValidationMethod())
        .append(assertTrue(FlagValidationGetter::assertionMessage))
        .append(jsonIgnore())
        .append(method())
        .filter(Filters.isValidationEnabled());
  }

  private static Generator<JavaPojoMember, PojoSettings> method() {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(SettingsFunctions::validationMethodModifiers)
        .noGenericTypes()
        .returnType("boolean")
        .methodName(FlagValidationGetter::flagName)
        .noArguments()
        .doesNotThrow()
        .content(member -> String.format("return %s;", flagName(member)))
        .build();
  }

  private static String assertionMessage(JavaPojoMember member) {
    if (member.isRequiredAndNullable()) {
      return String.format("%s is required but it is not present", member.getName());
    } else {
      return String.format("%s is required to be non-null but is null", member.getName());
    }
  }

  private static JavaName flagName(JavaPojoMember member) {
    if (member.isRequiredAndNullable()) {
      return member.getIsPresentFlagName();
    } else {
      return member.getIsNotNullFlagName();
    }
  }
}
