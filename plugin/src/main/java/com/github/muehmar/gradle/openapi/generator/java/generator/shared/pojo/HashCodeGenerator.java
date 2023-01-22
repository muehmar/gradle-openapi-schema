package com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo;

import static com.github.muehmar.gradle.openapi.util.Functions.allExceptFirst;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.AnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.writer.Writer;

public class HashCodeGenerator {
  private HashCodeGenerator() {}

  public static <T extends JavaPojo> Generator<T, PojoSettings> hashCodeMethod() {
    final Generator<T, PojoSettings> method =
        JavaGenerators.<T, PojoSettings>methodGen()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType("int")
            .methodName("hashCode")
            .noArguments()
            .content(hashCodeMethodContent())
            .build();
    return AnnotationGenerator.<T, PojoSettings>override()
        .append(method)
        .filter(
            pojo ->
                pojo.fold(
                    arrayPojo -> true,
                    enumPojo -> false,
                    objectPojo -> true,
                    composedPojo -> true,
                    freeFormPojo -> true));
  }

  private static <T extends JavaPojo> Generator<T, PojoSettings> hashCodeMethodContent() {
    return (pojo, s, w) -> {
      final PList<String> fieldNames =
          pojo.getMembersOrEmpty()
              .flatMap(
                  member -> {
                    final Name memberName = member.getName();
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