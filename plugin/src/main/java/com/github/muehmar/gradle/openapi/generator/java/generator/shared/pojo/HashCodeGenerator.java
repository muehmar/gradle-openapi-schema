package com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo;

import static com.github.muehmar.gradle.openapi.util.Functions.allExceptFirst;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.AnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.writer.Writer;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.util.Optional;
import lombok.Value;

public class HashCodeGenerator {
  private HashCodeGenerator() {}

  public static Generator<HashCodeContent, PojoSettings> hashCodeMethod() {
    final Generator<HashCodeContent, PojoSettings> method =
        JavaGenerators.<HashCodeContent, PojoSettings>methodGen()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType("int")
            .methodName("hashCode")
            .noArguments()
            .content(hashCodeMethodContent())
            .build();
    return AnnotationGenerator.<HashCodeContent, PojoSettings>override()
        .append(method)
        .append(arraysRefGenerator());
  }

  private static Generator<HashCodeContent, PojoSettings> hashCodeMethodContent() {
    return (content, s, w) -> {
      final Writer writerStartPrinted = w.println("return Objects.hash(");

      final PList<String> additionalPropertiesArgument =
          PList.fromOptional(
                  content
                      .getAdditionalProperties()
                      .map(ignore -> JavaAdditionalProperties.getPropertyName()))
              .map(JavaIdentifier::asString);

      return content
          .getMembers()
          .map(HashCodeMember::new)
          .flatMap(HashCodeMember::toHashCodeMethodArguments)
          .concat(additionalPropertiesArgument)
          .reverse()
          .zipWithIndex()
          .map(allExceptFirst(arg -> arg.concat(",")))
          .reverse()
          .foldLeft(writerStartPrinted, (writer, name) -> writer.tab(1).println(name))
          .println(");")
          .ref(JavaRefs.JAVA_UTIL_OBJECTS);
    };
  }

  private static Generator<HashCodeContent, PojoSettings> arraysRefGenerator() {
    return Generator.<HashCodeContent, PojoSettings>emptyGen()
        .appendConditionally(
            HashCodeContent::hasArrayProperty,
            Generator.ofWriterFunction(w -> w.ref(JavaRefs.JAVA_UTIL_ARRAYS)));
  }

  /** Content for the generation of the hashCode method. */
  @PojoBuilder(builderName = "HashCodeContentBuilder")
  @Value
  public static class HashCodeContent {
    PList<JavaPojoMember> members;
    Optional<JavaAdditionalProperties> additionalProperties;

    private boolean hasArrayProperty() {
      return members.exists(m -> m.getJavaType().isJavaArray());
    }
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
