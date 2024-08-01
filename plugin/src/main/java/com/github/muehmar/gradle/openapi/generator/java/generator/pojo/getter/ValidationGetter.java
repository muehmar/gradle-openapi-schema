package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.DeprecatedMethodGenerator.deprecatedJavaDocAndAnnotationForValidationMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaTypeGenerators.deepAnnotatedParameterizedClassName;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator.validationAnnotationsForMember;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.SettingsFunctions;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import java.util.function.BiFunction;

public class ValidationGetter {
  private ValidationGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> validationGetterGenerator() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(deprecatedJavaDocAndAnnotationForValidationMethod())
        .append(jsonIgnore())
        .append(validationAnnotationsForMember())
        .append(getterMethod());
  }

  private static Generator<JavaPojoMember, PojoSettings> getterMethod() {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(SettingsFunctions::validationMethodModifiers)
        .noGenericTypes()
        .returnType(returnType())
        .methodName(methodName())
        .noArguments()
        .doesNotThrow()
        .content(f -> String.format("return %s;", f.getName()))
        .build();
  }

  private static Generator<JavaPojoMember, PojoSettings> returnType() {
    return deepAnnotatedParameterizedClassName()
        .contraMap(ValidationAnnotationGenerator.PropertyType::fromMember);
  }

  private static BiFunction<JavaPojoMember, PojoSettings, String> methodName() {
    return (member, settings) -> {
      if (member.isRequiredAndNotNullable()
          && not(member.getJavaType().isNullableItemsArrayType())) {
        return member.getGetterName().asString();
      } else {
        return member.getValidationGetterName(settings).asString();
      }
    };
  }
}
