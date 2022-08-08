package com.github.muehmar.gradle.openapi.generator.java.generator;

import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.JavaResolver;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.function.UnaryOperator;

public class EqualsGenerator {

  public static final JavaResolver RESOLVER = new JavaResolver();

  private EqualsGenerator() {}

  public static Generator<Pojo, PojoSettings> equalsMethod() {
    final Generator<Pojo, PojoSettings> method =
        JavaGenerators.<Pojo, PojoSettings>methodGen()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType("boolean")
            .methodName("equals")
            .singleArgument(pojo -> "Object obj")
            .content(equalsMethodContent())
            .build();
    return AnnotationGenerator.<Pojo, PojoSettings>override().append(method);
  }

  private static Generator<Pojo, PojoSettings> equalsMethodContent() {
    return equalsCheckIdentity()
        .append(equalsCheckNullAndSameClass())
        .append(equalsCastObjectToCompare())
        .append(equalsCompareFields());
  }

  private static Generator<Pojo, PojoSettings> equalsCheckIdentity() {
    return Generator.ofWriterFunction(w -> w.println("if (this == obj) return true;"));
  }

  private static UnaryOperator<Writer> equalsCheckNullAndSameClass() {
    return w -> w.println("if (obj == null || this.getClass() != obj.getClass()) return false;");
  }

  private static Generator<Pojo, PojoSettings> equalsCastObjectToCompare() {
    return (p, s, w) ->
        w.println("final %s other = (%s) obj;", p.className(RESOLVER), p.className(RESOLVER));
  }

  private static Generator<Pojo, PojoSettings> equalsCompareFields() {
    return (members, s, w) -> {
      final PList<String> fieldNames =
          members
              .getMembers()
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
