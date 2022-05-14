package com.github.muehmar.gradle.openapi.generator.java.generator;

import static com.github.muehmar.gradle.openapi.util.Functions.allExceptFirst;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.data.Name;
import com.github.muehmar.gradle.openapi.generator.data.Pojo;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.JavaResolver;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGen;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGenBuilder;
import io.github.muehmar.pojoextension.generator.writer.Writer;

public class HashCodeGenerator {
  private static final JavaResolver RESOLVER = new JavaResolver();

  private HashCodeGenerator() {}

  public static Generator<Pojo, PojoSettings> hashCodeMethod() {
    final MethodGen<Pojo, PojoSettings> method =
        MethodGenBuilder.<Pojo, PojoSettings>create()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType("int")
            .methodName("hashCode")
            .noArguments()
            .content(hashCodeMethodContent())
            .build();
    return AnnotationGenerator.<Pojo, PojoSettings>override().append(method);
  }

  private static Generator<Pojo, PojoSettings> hashCodeMethodContent() {
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

      final Writer writerStartPrinted = w.println("return Objects.hash(");

      final PList<String> mappedFieldNames =
          fieldNames.reverse().zipWithIndex().map(allExceptFirst(name -> name + ",")).reverse();

      return mappedFieldNames
          .foldLeft(writerStartPrinted, (writer, name) -> writer.tab(1).println(name))
          .println(");")
          .ref(JavaRefs.JAVA_UTIL_OBJECTS);
    };
  }
}
