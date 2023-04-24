package com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo;

import static com.github.muehmar.gradle.openapi.util.Functions.allExceptFirst;
import static com.github.muehmar.gradle.openapi.util.Functions.first;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.AnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.writer.Writer;

public class ToStringGenerator {
  private ToStringGenerator() {}

  public static Generator<JavaPojo, PojoSettings> toStringMethod() {
    final Generator<JavaPojo, PojoSettings> method =
        JavaGenerators.<JavaPojo, PojoSettings>methodGen()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType("String")
            .methodName("toString")
            .noArguments()
            .content(toStringMethodContent())
            .build();
    return AnnotationGenerator.<JavaPojo, PojoSettings>override()
        .append(method)
        .filter(JavaPojo::isNotEnum);
  }

  private static Generator<JavaPojo, PojoSettings> toStringMethodContent() {
    return (pojo, s, w) -> {
      final PList<String> fieldNames =
          pojo.getMembersOrEmpty()
              .flatMap(JavaPojoMember::createFieldNames)
              .map(JavaIdentifier::asString);

      final Writer writerStartPrinted = w.println("return \"%s{\" +", pojo.getClassName());

      final PList<String> mappedFieldNames =
          fieldNames
              .zipWithIndex()
              .map(allExceptFirst(name -> "\", " + name + "=\" + " + name + " +"))
              .zipWithIndex()
              .map(first(name -> "\"" + name + "=\" + " + name + " +"));

      return mappedFieldNames
          .foldLeft(writerStartPrinted, (writer, name) -> writer.tab(1).println(name))
          .tab(1)
          .println("\"}\";");
    };
  }
}
