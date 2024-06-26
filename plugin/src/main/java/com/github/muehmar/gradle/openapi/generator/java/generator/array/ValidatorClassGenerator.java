package com.github.muehmar.gradle.openapi.generator.java.generator.array;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator.PropertyValidationGenerator.memberValidationGenerator;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator.ConditionsWriter;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaArrayPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.function.Predicate;

public class ValidatorClassGenerator {
  private ValidatorClassGenerator() {}

  public static Generator<JavaArrayPojo, PojoSettings> validationClassGenerator() {
    return JavaGenerators.<JavaArrayPojo, PojoSettings>classGen()
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

  private static Generator<JavaArrayPojo, PojoSettings> validationClassContent() {
    return Generator.<JavaArrayPojo, PojoSettings>emptyGen()
        .append(memberValidationGenerator(), JavaArrayPojo::getArrayPojoMember)
        .appendSingleBlankLine()
        .append(isValidMethod());
  }

  private static Generator<JavaArrayPojo, PojoSettings> isValidMethod() {
    return JavaGenerators.<JavaArrayPojo, PojoSettings>methodGen()
        .modifiers(PRIVATE)
        .noGenericTypes()
        .returnType("boolean")
        .methodName("isValid")
        .noArguments()
        .doesNotThrow()
        .content(isValidMethodContent(itemsValidationCondition()))
        .build();
  }

  private static Generator<JavaArrayPojo, PojoSettings> isValidMethodContent(
      Condition... conditions) {
    return (pojo, settings, writer) -> {
      final PList<Writer> conditionWriters =
          PList.fromArray(conditions).map(gen -> gen.generate(pojo, settings, javaWriter()));
      return writer.append(ConditionsWriter.andConditions(conditionWriters));
    };
  }

  private static Condition itemsValidationCondition() {
    return (pojo, settings, writer) ->
        writer.print("is%sValid()", pojo.getArrayPojoMember().getName().startUpperCase());
  }

  private interface Condition extends Generator<JavaArrayPojo, PojoSettings> {
    @Override
    default Condition filter(Predicate<JavaArrayPojo> predicate) {
      final Generator<JavaArrayPojo, PojoSettings> self = this;
      return (p, s, w) -> predicate.test(p) ? self.generate(p, s, w) : w;
    }
  }
}
