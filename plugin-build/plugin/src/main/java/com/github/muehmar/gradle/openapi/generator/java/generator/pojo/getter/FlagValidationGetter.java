package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.DeprecatedMethodGenerator.deprecatedJavaDocAndAnnotationForValidationMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator.assertTrue;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberAndNameScope;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.SettingsFunctions;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;

public class FlagValidationGetter {
  private FlagValidationGetter() {}

  public static Generator<MemberAndNameScope, PojoSettings> flagValidationGetterGenerator() {
    return Generator.<MemberAndNameScope, PojoSettings>emptyGen()
        .append(deprecatedJavaDocAndAnnotationForValidationMethod(), MemberAndNameScope::getMember)
        .append(assertTrue(mas -> assertionMessage(mas.getMember())))
        .append(method())
        .filter(Filters.isValidationEnabled());
  }

  private static Generator<MemberAndNameScope, PojoSettings> method() {
    return JavaGenerators.<MemberAndNameScope, PojoSettings>methodGen()
        .modifiers(SettingsFunctions::validationMethodModifiers)
        .noGenericTypes()
        .returnType("boolean")
        .methodName(FlagValidationGetter::methodName)
        .noArguments()
        .doesNotThrow()
        .content(mas -> String.format("return %s;", fieldName(mas)))
        .build();
  }

  /**
   * Resolved separately from the field it returns: the method name becomes the property path of a
   * constraint violation, whereas the field is private and freely renameable.
   */
  private static JavaName methodName(MemberAndNameScope mas) {
    return mas.getDtoNameScope().resolveFieldName(plainFlagName(mas.getMember()));
  }

  private static JavaName fieldName(MemberAndNameScope mas) {
    final JavaPojoMember member = mas.getMember();
    return member.isRequiredAndNullable() ? mas.getIsPresentFlagName() : mas.getIsNotNullFlagName();
  }

  private static String assertionMessage(JavaPojoMember member) {
    if (member.isRequiredAndNullable()) {
      return String.format("%s is required but it is not present", member.getName());
    } else {
      return String.format("%s is required to be non-null but is null", member.getName());
    }
  }

  private static JavaName plainFlagName(JavaPojoMember member) {
    return member.isRequiredAndNullable()
        ? member.getIsPresentFlagName()
        : member.getIsNotNullFlagName();
  }
}
