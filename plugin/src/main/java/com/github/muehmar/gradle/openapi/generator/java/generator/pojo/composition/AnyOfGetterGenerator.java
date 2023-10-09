package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaDocGenerator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.java.MethodGen;
import java.util.function.Function;
import lombok.Value;

public class AnyOfGetterGenerator {
  private static final Function<JavaObjectPojo, String> JAVA_DOC_FOR_POJO =
      pojo ->
          String.format(
              "Returns {@link %s} of the anyOf composition in case it is valid against the schema %s wrapped in an "
                  + "{@link Optional}, empty otherwise.",
              pojo.getClassName(), pojo.getSchemaName());

  private AnyOfGetterGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> anyOfGetterGenerator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendOptional(anyOfGetterGeneratorForComposition(), JavaObjectPojo::getAnyOfComposition);
  }

  private static Generator<JavaAnyOfComposition, PojoSettings>
      anyOfGetterGeneratorForComposition() {
    return Generator.<JavaAnyOfComposition, PojoSettings>emptyGen()
        .appendList(singleAnyOfGetter(), CompositionAndPojo::fromComposition, newLine())
        .append(ref(JavaRefs.JAVA_UTIL_OPTIONAL));
  }

  private static Generator<CompositionAndPojo, PojoSettings> singleAnyOfGetter() {
    final MethodGen<CompositionAndPojo, PojoSettings> method =
        JavaGenerators.<CompositionAndPojo, PojoSettings>methodGen()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType(CompositionAndPojo::getReturnType)
            .methodName(CompositionAndPojo::getMethodName)
            .noArguments()
            .content(singleAnyOfGetterContent())
            .build();
    return JavaDocGenerator.<PojoSettings>javaDoc()
        .contraMap(JAVA_DOC_FOR_POJO)
        .contraMap(CompositionAndPojo::getPojo)
        .append(method);
  }

  private static Generator<CompositionAndPojo, PojoSettings> singleAnyOfGetterContent() {
    return Generator.<CompositionAndPojo, PojoSettings>emptyGen()
        .append((cp, s, w) -> w.println("return foldAnyOf(%s);", cp.getArguments()));
  }

  @Value
  private static class CompositionAndPojo {
    JavaAnyOfComposition composition;
    JavaObjectPojo pojo;

    static PList<CompositionAndPojo> fromComposition(JavaAnyOfComposition composition) {
      return composition
          .getPojos()
          .toPList()
          .map(pojo -> new CompositionAndPojo(composition, pojo));
    }

    String getReturnType() {
      return String.format("Optional<%s>", pojo.getClassName());
    }

    String getMethodName() {
      return String.format("get%s", pojo.getClassName());
    }

    String getArguments() {
      return composition
          .getPojos()
          .map(p -> p.equals(pojo) ? "Optional::of" : "ignore -> Optional.empty()")
          .toPList()
          .mkString(", ");
    }
  }
}
