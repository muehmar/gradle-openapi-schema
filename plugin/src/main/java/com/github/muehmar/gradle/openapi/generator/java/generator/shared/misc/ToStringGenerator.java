package com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc;

import static com.github.muehmar.gradle.openapi.util.Functions.allExceptFirst;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.AnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.TechnicalPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassNames;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.writer.Writer;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import lombok.Value;

public class ToStringGenerator {
  private static final String SINGLE_PROPERTY_FORMAT = "\"%s=\" + %s +";

  private ToStringGenerator() {}

  public static Generator<ToStringContent, PojoSettings> toStringMethod() {
    final Generator<ToStringContent, PojoSettings> method =
        JavaGenerators.<ToStringContent, PojoSettings>methodGen()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType("String")
            .methodName("toString")
            .noArguments()
            .content(toStringMethodContent())
            .build();
    return AnnotationGenerator.<ToStringContent, PojoSettings>override()
        .append(method)
        .append(arraysRefGenerator());
  }

  private static Generator<ToStringContent, PojoSettings> arraysRefGenerator() {
    return Generator.<ToStringContent, PojoSettings>emptyGen()
        .appendConditionally(
            Generator.ofWriterFunction(w -> w.ref(JavaRefs.JAVA_UTIL_ARRAYS)),
            ToStringContent::hasArrayMemberType);
  }

  private static Generator<ToStringContent, PojoSettings> toStringMethodContent() {
    return (content, s, w) -> {
      final Writer writerStartPrinted = w.println("return \"%s{\" +", content.getClassName());

      return content
          .getTechnicalPojoMembers()
          .map(
              member ->
                  String.format(
                      SINGLE_PROPERTY_FORMAT,
                      member.getName().getOriginalName(),
                      toRightHandExpression(member)))
          .reverse()
          .zipWithIndex()
          .map(allExceptFirst(line -> line.concat(" \", \" +")))
          .reverse()
          .foldLeft(writerStartPrinted, (writer, name) -> writer.tab(1).println(name))
          .tab(1)
          .println("\"}\";");
    };
  }

  private static String toRightHandExpression(TechnicalPojoMember member) {
    if (member.getJavaType().isJavaArray()) {
      return String.format("Arrays.toString(%s)", member.getName());
    } else if (member.getJavaType().getQualifiedClassName().equals(QualifiedClassNames.STRING)) {
      return String.format("\"'\" + %s + \"'\"", member.getName());
    } else {
      return member.getName().asString();
    }
  }

  @PojoBuilder(builderName = "ToStringContentBuilder")
  @Value
  public static class ToStringContent {
    JavaName className;

    PList<TechnicalPojoMember> technicalPojoMembers;

    public boolean hasArrayMemberType() {
      return technicalPojoMembers.exists(m -> m.getJavaType().isJavaArray());
    }
  }
}
