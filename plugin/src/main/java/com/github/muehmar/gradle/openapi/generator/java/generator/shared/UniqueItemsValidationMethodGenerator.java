package com.github.muehmar.gradle.openapi.generator.java.generator.shared;

import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

public class UniqueItemsValidationMethodGenerator {
  private UniqueItemsValidationMethodGenerator() {}

  public static Generator<JavaPojoMember, PojoSettings> uniqueItemsValidationMethodGenerator() {
    final MethodGen<JavaPojoMember, PojoSettings> method =
        MethodGenBuilder.<JavaPojoMember, PojoSettings>create()
            .modifiers(SettingsFunctions::validationMethodModifiers)
            .noGenericTypes()
            .returnType("boolean")
            .methodName(UniqueItemsValidationMethodGenerator::uniqueItemsMethodName)
            .noArguments()
            .content(
                member ->
                    String.format(
                        "return new HashSet<>(%s).size() == %s.size();",
                        member.getName(), member.getName()))
            .build();
    return ValidationGenerator.<JavaPojoMember>assertTrue(
            member -> String.format("%s does not contain unique items", member.getName()))
        .append(method)
        .append(w -> w.ref(JavaRefs.JAVA_UTIL_HASH_SET))
        .filter(UniqueItemsValidationMethodGenerator::generateMethod);
  }

  private static String uniqueItemsMethodName(JavaPojoMember member) {
    return member.getName().startUpperCase().prefix("has").append("UniqueItems").asString();
  }

  private static boolean generateMethod(JavaPojoMember member, PojoSettings settings) {
    return member.getJavaType().getType().isArrayType()
        && member.getJavaType().getConstraints().isUniqueItems()
        && settings.isEnableValidation();
  }
}
