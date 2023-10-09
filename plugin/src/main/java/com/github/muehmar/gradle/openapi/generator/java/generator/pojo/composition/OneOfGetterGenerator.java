package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaDocGenerator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.java.MethodGen;
import java.util.function.Function;
import lombok.Value;

public class OneOfGetterGenerator {
  private static final Function<JavaObjectPojo, String> JAVA_DOC_FOR_POJO =
      pojo ->
          String.format(
              "Returns {@link %s} of the oneOf composition in case it is valid against the schema %s wrapped in an "
                  + "{@link Optional}, empty otherwise.",
              pojo.getClassName(), pojo.getSchemaName());

  private OneOfGetterGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> oneOfGetterGenerator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendOptional(oneOfGetterGeneratorForComposition(), JavaObjectPojo::getOneOfComposition);
  }

  private static Generator<JavaOneOfComposition, PojoSettings>
      oneOfGetterGeneratorForComposition() {
    return Generator.<JavaOneOfComposition, PojoSettings>emptyGen()
        .appendList(singleOneOfGetter(), CompositionAndPojo::fromComposition, newLine())
        .append(ref(JavaRefs.JAVA_UTIL_OPTIONAL));
  }

  private static Generator<CompositionAndPojo, PojoSettings> singleOneOfGetter() {
    final MethodGen<CompositionAndPojo, PojoSettings> method =
        JavaGenerators.<CompositionAndPojo, PojoSettings>methodGen()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType(CompositionAndPojo::getReturnType)
            .methodName(CompositionAndPojo::getMethodName)
            .noArguments()
            .content(singleOneOfGetterContent())
            .build();
    return JavaDocGenerator.<PojoSettings>javaDoc()
        .contraMap(JAVA_DOC_FOR_POJO)
        .contraMap(CompositionAndPojo::getPojo)
        .append(method);
  }

  private static Generator<CompositionAndPojo, PojoSettings> singleOneOfGetterContent() {
    return Generator.<CompositionAndPojo, PojoSettings>emptyGen()
        .append((cp, s, w) -> w.println("return foldOneOf(%s);", cp.getArguments()));
  }

  @Value
  private static class CompositionAndPojo {
    JavaOneOfComposition composition;
    JavaObjectPojo pojo;

    static PList<CompositionAndPojo> fromComposition(JavaOneOfComposition composition) {
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
          .add("Optional::empty")
          .toPList()
          .mkString(", ");
    }
  }
}
