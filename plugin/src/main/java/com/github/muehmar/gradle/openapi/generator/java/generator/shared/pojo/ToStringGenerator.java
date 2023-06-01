package com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo;

import static com.github.muehmar.gradle.openapi.util.Functions.allExceptFirst;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.AnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.QualifiedClassNames;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.writer.Writer;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.util.Optional;
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
            ToStringContent::hasArrayMemberType,
            Generator.ofWriterFunction(w -> w.ref(JavaRefs.JAVA_UTIL_ARRAYS)));
  }

  private static Generator<ToStringContent, PojoSettings> toStringMethodContent() {
    return (content, s, w) -> {
      final Writer writerStartPrinted = w.println("return \"%s{\" +", content.getClassName());

      final ToStringAdditionalProperty toStringAdditionalProperty =
          new ToStringAdditionalProperty(content.getAdditionalProperties());

      return content
          .getMembers()
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

  @PojoBuilder(builderName = "ToStringContentBuilder")
  @Value
  public static class ToStringContent {
    JavaIdentifier className;
    PList<JavaPojoMember> members;
    Optional<JavaAdditionalProperties> additionalProperties;

    public boolean hasArrayMemberType() {
      return members.exists(m -> m.getJavaType().isJavaArray());
    }
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
      final boolean nameMatchesMemberName = name.equals(member.getNameAsIdentifier());
      if (member.getJavaType().isJavaArray() && nameMatchesMemberName) {
        return String.format("Arrays.toString(%s)", name);
      } else if (member.getJavaType().getQualifiedClassName().equals(QualifiedClassNames.STRING)
          && nameMatchesMemberName) {
        return String.format("\"'\" + %s + \"'\"", name);
      } else {
        return name.asString();
      }
    }
  }

  @Value
  private static class ToStringAdditionalProperty {
    Optional<JavaAdditionalProperties> additionalProperties;

    private PList<String> toStringLines() {
      return PList.fromOptional(
          additionalProperties
              .map(ignore -> JavaAdditionalProperties.getPropertyName())
              .map(name -> String.format(SINGLE_PROPERTY_FORMAT, name, name)));
    }
  }
}
