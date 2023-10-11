package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.validator;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.validator.PropertyValidationGenerator.propertyValidationGenerator;
import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;

import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;

public class ValidatorClassGenerator {
  private ValidatorClassGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> validationClassGenerator() {
    return JavaGenerators.<JavaObjectPojo, PojoSettings>classGen()
        .clazz()
        .nested()
        .packageGen(Generator.emptyGen())
        .noJavaDoc()
        .noAnnotations()
        .modifiers(PRIVATE)
        .className("Validator")
        .noSuperClass()
        .noInterfaces()
        .content(content())
        .build();
  }

  private static Generator<JavaObjectPojo, PojoSettings> content() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(propertyValidationGenerator(), JavaObjectPojo::getMembers, newLine());
  }
}
