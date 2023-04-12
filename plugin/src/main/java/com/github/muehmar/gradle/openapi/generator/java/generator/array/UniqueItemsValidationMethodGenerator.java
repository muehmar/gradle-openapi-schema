package com.github.muehmar.gradle.openapi.generator.java.generator.array;

import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.SettingsFunctions;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.ValidationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaArrayPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

public class UniqueItemsValidationMethodGenerator {
  private UniqueItemsValidationMethodGenerator() {}

  public static Generator<JavaArrayPojo, PojoSettings> generator() {
    final MethodGen<JavaArrayPojo, PojoSettings> method =
        MethodGenBuilder.<JavaArrayPojo, PojoSettings>create()
            .modifiers(SettingsFunctions::validationMethodModifiers)
            .noGenericTypes()
            .returnType("boolean")
            .methodName("isAllUniqueItems")
            .noArguments()
            .content("return new HashSet<>(value).size() == value.size();")
            .build();
    return ValidationGenerator.<JavaArrayPojo>assertTrue(
            pojo -> String.format("%s does not contain unique items", pojo.getClassName()))
        .append(method)
        .append(w -> w.ref(JavaRefs.JAVA_UTIL_HASH_SET))
        .filter(UniqueItemsValidationMethodGenerator::generateMethod);
  }

  private static boolean generateMethod(JavaArrayPojo pojo, PojoSettings settings) {
    return pojo.getConstraints().isUniqueItems() && settings.isEnableValidation();
  }
}
