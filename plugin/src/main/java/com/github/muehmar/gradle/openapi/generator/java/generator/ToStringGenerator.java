package com.github.muehmar.gradle.openapi.generator.java.generator;

import static com.github.muehmar.gradle.openapi.util.Functions.allExceptFirst;
import static com.github.muehmar.gradle.openapi.util.Functions.first;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.Resolver;
import com.github.muehmar.gradle.openapi.generator.data.Name;
import com.github.muehmar.gradle.openapi.generator.data.Pojo;
import com.github.muehmar.gradle.openapi.generator.java.JavaResolver;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.writer.Writer;

public class ToStringGenerator {
  private static final Resolver RESOLVER = new JavaResolver();

  private ToStringGenerator() {}

  public static Generator<Pojo, PojoSettings> toStringMethod() {
    final Generator<Pojo, PojoSettings> method =
        JavaGenerators.<Pojo, PojoSettings>methodGen()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType("String")
            .methodName("toString")
            .noArguments()
            .content(toStringMethodContent())
            .build();
    return AnnotationGenerator.<Pojo, PojoSettings>override().append(method);
  }

  private static Generator<Pojo, PojoSettings> toStringMethodContent() {
    return (pojo, s, w) -> {
      final PList<String> fieldNames =
          pojo.getMembers()
              .flatMap(
                  member -> {
                    final Name memberName = member.memberName(RESOLVER);
                    if (member.isRequiredAndNullable()) {
                      final String requiredNullableFlagName =
                          String.format("is%sPresent", memberName.startUpperCase());
                      return PList.of(memberName.asString(), requiredNullableFlagName);
                    } else if (member.isOptionalAndNullable()) {
                      final String optionalNullableFlagName =
                          String.format("is%sNull", memberName.startUpperCase());
                      return PList.of(memberName.asString(), optionalNullableFlagName);
                    } else {
                      return PList.single(memberName.asString());
                    }
                  });

      final Writer writerStartPrinted = w.println("return \"%s{\" +", pojo.className(RESOLVER));

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
