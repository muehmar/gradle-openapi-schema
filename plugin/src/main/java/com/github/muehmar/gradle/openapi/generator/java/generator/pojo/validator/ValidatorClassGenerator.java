package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.validator;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.validator.PropertyValidationGenerator.propertyValidationGenerator;
import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.util.Strings;
import io.github.muehmar.codegenerator.writer.Writer;

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
        .content(validationClassContent())
        .build();
  }

  private static Generator<JavaObjectPojo, PojoSettings> validationClassContent() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(propertyValidationGenerator(), JavaObjectPojo::getMembers, newLine())
        .appendSingleBlankLine()
        .append(isValidMethod());
  }

  private static Generator<JavaObjectPojo, PojoSettings> isValidMethod() {
    return JavaGenerators.<JavaObjectPojo, PojoSettings>methodGen()
        .modifiers(PRIVATE)
        .noGenericTypes()
        .returnType("boolean")
        .methodName("isValid")
        .noArguments()
        .content(isValidMethodContent(PList.empty()))
        .build();
  }

  private static Generator<JavaObjectPojo, PojoSettings> isValidMethodContent(
      PList<Condition> dtoConditions) {
    return (pojo, settings, writer) -> {
      final PList<Condition> allConditions =
          createPropertyValidationConditions(pojo).concat(dtoConditions);

      final PList<Writer> writers =
          allConditions.map(gen -> gen.generate(pojo, settings, javaWriter()));
      final NonEmptyList<String> formattedConditions =
          NonEmptyList.fromIter(writers.map(Writer::asString).filter(Strings::nonEmptyOrBlank))
              .orElse(NonEmptyList.single("true"));

      final String firstFormatted = formattedConditions.head();
      final PList<String> remainingFormatted = formattedConditions.tail();

      final PList<String> refs = writers.flatMap(Writer::getRefs);

      if (remainingFormatted.isEmpty()) {
        return writer.println("return %s;", firstFormatted).refs(refs);
      } else {
        return remainingFormatted
            .foldLeft(
                writer.print("return %s", firstFormatted),
                (w, f) -> w.println().tab(2).print("&& %s", f))
            .println(";")
            .refs(refs);
      }
    };
  }

  private static PList<Condition> createPropertyValidationConditions(JavaObjectPojo pojo) {
    return pojo.getMembers()
        .map(member -> (p, s, w) -> w.print("%s()", IsPropertyValidMethodName.fromMember(member)));
  }

  private interface Condition extends Generator<JavaObjectPojo, PojoSettings> {}
}
