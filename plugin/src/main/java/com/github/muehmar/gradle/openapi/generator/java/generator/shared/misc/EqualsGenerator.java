package com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc;

import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.AnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.TechnicalPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.java.MethodGen.Argument;
import io.github.muehmar.codegenerator.writer.Writer;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.util.function.UnaryOperator;
import lombok.Value;

public class EqualsGenerator {
  private EqualsGenerator() {}

  public static Generator<EqualsContent, PojoSettings> equalsMethod() {
    final Generator<EqualsContent, PojoSettings> method =
        JavaGenerators.<EqualsContent, PojoSettings>methodGen()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType("boolean")
            .methodName("equals")
            .singleArgument(pojo -> new Argument("Object", "obj"))
            .content(equalsMethodContent())
            .build();
    return AnnotationGenerator.<EqualsContent, PojoSettings>override().append(method);
  }

  private static Generator<EqualsContent, PojoSettings> equalsMethodContent() {
    return EqualsGenerator.<EqualsContent>equalsCheckIdentity()
        .append(equalsCheckNullAndSameClass())
        .append(equalsCastObjectToCompare())
        .append(equalsCompareFields());
  }

  private static Generator<EqualsContent, PojoSettings> equalsCheckIdentity() {
    return Generator.constant("if (this == obj) return true;");
  }

  private static UnaryOperator<Writer> equalsCheckNullAndSameClass() {
    return w -> w.println("if (obj == null || this.getClass() != obj.getClass()) return false;");
  }

  private static Generator<EqualsContent, PojoSettings> equalsCastObjectToCompare() {
    return (content, s, w) ->
        w.println("final %s other = (%s) obj;", content.getClassName(), content.getClassName());
  }

  private static Generator<EqualsContent, PojoSettings> equalsCompareFields() {
    return (content, s, w) -> {
      final PList<String> fieldNames =
          content
              .getTechnicalPojoMembers()
              .map(TechnicalPojoMember::getName)
              .map(JavaName::asString);
      final Writer writerAfterFirstField =
          fieldNames
              .headOption()
              .map(field -> equalsCompareField().generate(field, s, w.print("return ")))
              .orElse(w.print("return true"));
      return fieldNames
          .drop(1)
          .foldLeft(
              writerAfterFirstField,
              (writer, field) ->
                  equalsCompareField().generate(field, s, writer.println().tab(2).print("&& ")))
          .println(";");
    };
  }

  private static Generator<String, PojoSettings> equalsCompareField() {
    return (fieldName, s, w) ->
        w.print("Objects.deepEquals(this.%s, other.%s)", fieldName, fieldName)
            .ref(JavaRefs.JAVA_UTIL_OBJECTS);
  }

  @PojoBuilder(builderName = "EqualsContentBuilder")
  @Value
  public static class EqualsContent {
    JavaName className;
    PList<TechnicalPojoMember> technicalPojoMembers;
  }
}
