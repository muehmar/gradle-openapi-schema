package com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo;

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
import java.util.function.UnaryOperator;

public class EqualsGenerator {
  private EqualsGenerator() {}

  public static Generator<JavaPojo, PojoSettings> equalsMethod() {
    final Generator<JavaPojo, PojoSettings> method =
        JavaGenerators.<JavaPojo, PojoSettings>methodGen()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType("boolean")
            .methodName("equals")
            .singleArgument(pojo -> "Object obj")
            .content(equalsMethodContent())
            .build();
    return AnnotationGenerator.<JavaPojo, PojoSettings>override()
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

  private static Generator<JavaPojo, PojoSettings> equalsMethodContent() {
    return equalsCheckIdentity()
        .append(equalsCheckNullAndSameClass())
        .append(equalsCastObjectToCompare())
        .append(equalsCompareFields());
  }

  private static Generator<JavaPojo, PojoSettings> equalsCheckIdentity() {
    return Generator.constant("if (this == obj) return true;");
  }

  private static UnaryOperator<Writer> equalsCheckNullAndSameClass() {
    return w -> w.println("if (obj == null || this.getClass() != obj.getClass()) return false;");
  }

  private static Generator<JavaPojo, PojoSettings> equalsCastObjectToCompare() {
    return (p, s, w) -> w.println("final %s other = (%s) obj;", p.getName(), p.getName());
  }

  private static Generator<JavaPojo, PojoSettings> equalsCompareFields() {
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
        w.print("Objects.equals(%s, other.%s)", fieldName, fieldName)
            .ref(JavaRefs.JAVA_UTIL_OBJECTS);
  }
}
