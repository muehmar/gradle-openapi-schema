package com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo;

import static com.github.muehmar.gradle.openapi.util.Functions.allExceptFirst;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.AnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.writer.Writer;
import lombok.Value;

public class ToStringGenerator {
  private static final String SINGLE_PROPERTY_FORMAT = "\"%s=\" + %s +";

  private ToStringGenerator() {}

  public static <T extends JavaPojo> Generator<T, PojoSettings> toStringMethod() {
    final Generator<T, PojoSettings> method =
        JavaGenerators.<T, PojoSettings>methodGen()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType("String")
            .methodName("toString")
            .noArguments()
            .content(toStringMethodContent())
            .build();
    return AnnotationGenerator.<T, PojoSettings>override()
        .append(method)
        .append(arraysRefGenerator())
        .filter(JavaPojo::isNotEnum);
  }

  private static <T extends JavaPojo> Generator<T, PojoSettings> arraysRefGenerator() {
    return Generator.<T, PojoSettings>emptyGen()
        .appendConditionally(
            ToStringGenerator::hasArrayMemberType,
            Generator.ofWriterFunction(w -> w.ref(JavaRefs.JAVA_UTIL_ARRAYS)));
  }

  private static <T extends JavaPojo> boolean hasArrayMemberType(T p) {
    return p.getMembersOrEmpty().exists(m -> m.getJavaType().isJavaArray());
  }

  private static <T extends JavaPojo> Generator<T, PojoSettings> toStringMethodContent() {
    return (pojo, s, w) -> {
      final Writer writerStartPrinted = w.println("return \"%s{\" +", pojo.getClassName());

      final ToStringAdditionalProperty toStringAdditionalProperty =
          new ToStringAdditionalProperty(pojo);

      return pojo.getMembersOrEmpty()
          .map(ToStringMember::new)
          .flatMap(ToStringMember::toStringLines)
          .concat(toStringAdditionalProperty.toStringLines())
          .reverse()
          .zipWithIndex()
          .map(allExceptFirst(line -> line.concat(" \", \" +")))
          .reverse()
          .foldLeft(writerStartPrinted, (writer, name) -> writer.tab(1).println(name))
          .tab(1)
          .println("\"}\";");
    };
  }

  @Value
  private static class ToStringMember {
    JavaPojoMember member;

    private PList<String> toStringLines() {
      return member
          .createFieldNames()
          .map(name -> String.format(SINGLE_PROPERTY_FORMAT, name, toRightHandExpression(name)));
    }

    private String toRightHandExpression(JavaIdentifier name) {
      return member.getJavaType().isJavaArray() && name.equals(member.getNameAsIdentifier())
          ? String.format("Arrays.toString(%s)", name)
          : name.asString();
    }
  }

  @Value
  private static class ToStringAdditionalProperty {
    JavaPojo pojo;

    private PList<String> toStringLines() {
      return PList.fromOptional(
          pojo.asObjectPojo()
              .map(ignore -> JavaAdditionalProperties.getPropertyName())
              .map(name -> String.format(SINGLE_PROPERTY_FORMAT, name, name)));
    }
  }
}
