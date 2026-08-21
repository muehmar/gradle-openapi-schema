package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIgnore;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;

public class FlagGetter {
  private FlagGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> flagGetterGenerator() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(jsonIgnore())
        .append(getterMethod())
        .filter(member -> member.isRequiredAndNullable() || member.isOptionalAndNotNullable());
  }

  private static Generator<JavaPojoMember, PojoSettings> getterMethod() {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers()
        .noGenericTypes()
        .returnType("boolean")
        .methodName(FlagGetter::methodName)
        .noArguments()
        .doesNotThrow()
        .content(FlagGetter::getterContent)
        .build()
        .append(RefsGenerator.fieldRefs());
  }

  private static String methodName(JavaPojoMember member) {
    if (member.isRequiredAndNullable()) {
      return member.getIsPresentFlagName().startUpperCase().prefix("get").asString();
    } else {
      return member.getIsNotNullFlagName().startUpperCase().prefix("get").asString();
    }
  }

  private static String getterContent(JavaPojoMember member) {
    if (member.isRequiredAndNullable()) {
      return String.format("return %s;", member.getIsPresentFlagName().asString());
    } else {
      return String.format("return %s;", member.getIsNotNullFlagName());
    }
  }
}
