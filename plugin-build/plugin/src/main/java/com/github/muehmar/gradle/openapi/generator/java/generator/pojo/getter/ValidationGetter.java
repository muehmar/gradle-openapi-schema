package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.DeprecatedMethodGenerator.deprecatedJavaDocAndAnnotationForValidationMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaTypeGenerators.deepAnnotatedParameterizedClassName;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator.validationAnnotationsForMember;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberAndNameScope;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.SettingsFunctions;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;

public class ValidationGetter {
  private ValidationGetter() {}

  public static Generator<MemberAndNameScope, PojoSettings> validationGetterGenerator() {
    return Generator.<MemberAndNameScope, PojoSettings>emptyGen()
        .append(deprecatedJavaDocAndAnnotationForValidationMethod(), MemberAndNameScope::getMember)
        .append(validationAnnotationsForMember(), MemberAndNameScope::getMember)
        .append(getterMethod())
        .filter(Filters.isValidationEnabled());
  }

  private static Generator<MemberAndNameScope, PojoSettings> getterMethod() {
    return JavaGenerators.<MemberAndNameScope, PojoSettings>methodGen()
        .modifiers(SettingsFunctions::validationMethodModifiers)
        .noGenericTypes()
        .returnType(returnType())
        .methodName((m, s) -> m.getMember().getValidationGetterName(s, m.getDtoNameScope()))
        .noArguments()
        .doesNotThrow()
        .content(m -> String.format("return %s;", m.getMember().getName()))
        .build();
  }

  private static Generator<MemberAndNameScope, PojoSettings> returnType() {
    return deepAnnotatedParameterizedClassName()
        .contraMap(m -> ValidationAnnotationGenerator.PropertyType.fromMember(m.getMember()));
  }
}
