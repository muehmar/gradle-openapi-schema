package com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo;

import static com.github.muehmar.gradle.openapi.util.Functions.allExceptFirst;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.AnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.writer.Writer;
import lombok.Value;

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
        .append(arraysRefGenerator())
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
      final Writer writerStartPrinted = w.println("return Objects.hash(");

      return pojo.getMembersOrEmpty()
          .map(HashCodeMember::new)
          .flatMap(HashCodeMember::toHashCodeMethodArguments)
          .reverse()
          .zipWithIndex()
          .map(allExceptFirst(arg -> arg.concat(",")))
          .reverse()
          .foldLeft(writerStartPrinted, (writer, name) -> writer.tab(1).println(name))
          .println(");")
          .ref(JavaRefs.JAVA_UTIL_OBJECTS);
    };
  }

  private static <T extends JavaPojo> Generator<T, PojoSettings> arraysRefGenerator() {
    return Generator.<T, PojoSettings>emptyGen()
        .appendConditionally(
            HashCodeGenerator::hasArrayMemberType,
            Generator.ofWriterFunction(w -> w.ref(JavaRefs.JAVA_UTIL_ARRAYS)));
  }

  private static <T extends JavaPojo> boolean hasArrayMemberType(T p) {
    return p.getMembersOrEmpty().exists(m -> m.getJavaType().isJavaArray());
  }

  @Value
  private static class HashCodeMember {
    JavaPojoMember member;

    private PList<String> toHashCodeMethodArguments() {
      return member.createFieldNames().map(JavaIdentifier::asString).map(this::mapArrayArguments);
    }

    private String mapArrayArguments(String argument) {
      return member.getJavaType().isJavaArray()
              && argument.equals(member.getName().asIdentifier().asString())
          ? String.format("Arrays.hashCode(%s)", argument)
          : argument;
    }
  }
}
