package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaDocGenerator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.java.MethodGen;
import java.util.function.Function;
import lombok.Value;

public class CompositionGetterGenerator {
  private static final Function<CompositionAndPojo, String> JAVA_DOC_FOR_POJO =
      cp ->
          String.format(
              "Returns {@link %s} of the %s composition in case it is valid against the schema %s wrapped in an "
                  + "{@link Optional}, empty otherwise.",
              cp.getPojo().getClassName(), cp.getType().getName(), cp.getPojo().getSchemaName());

  private CompositionGetterGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> compositionGetterGenerator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendOptional(oneOfGetterGenerator(), JavaObjectPojo::getOneOfComposition)
        .appendSingleBlankLine()
        .appendOptional(anyOfGetterGenerator(), JavaObjectPojo::getAnyOfComposition);
  }

  private static Generator<JavaOneOfComposition, PojoSettings> oneOfGetterGenerator() {
    return Generator.<JavaOneOfComposition, PojoSettings>emptyGen()
        .appendList(singleGetter(), CompositionAndPojo::fromComposition, newLine())
        .append(ref(JavaRefs.JAVA_UTIL_OPTIONAL));
  }

  private static Generator<JavaAnyOfComposition, PojoSettings> anyOfGetterGenerator() {
    return Generator.<JavaAnyOfComposition, PojoSettings>emptyGen()
        .appendList(singleGetter(), CompositionAndPojo::fromComposition, newLine())
        .append(ref(JavaRefs.JAVA_UTIL_OPTIONAL));
  }

  private static Generator<CompositionAndPojo, PojoSettings> singleGetter() {
    final MethodGen<CompositionAndPojo, PojoSettings> method =
        JavaGenerators.<CompositionAndPojo, PojoSettings>methodGen()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType(CompositionAndPojo::getReturnType)
            .methodName(CompositionAndPojo::getMethodName)
            .noArguments()
            .content(singleGetterContent())
            .build();
    return JavaDocGenerator.<PojoSettings>javaDoc()
        .contraMap(JAVA_DOC_FOR_POJO)
        .append(jsonIgnore())
        .append(method);
  }

  private static Generator<CompositionAndPojo, PojoSettings> singleGetterContent() {
    return Generator.<CompositionAndPojo, PojoSettings>emptyGen()
        .append((cp, s, w) -> w.println("return %s;", cp.getMethodCall()));
  }

  @Value
  private static class CompositionAndPojo {
    Type type;
    PList<JavaObjectPojo> composedPojos;
    JavaObjectPojo pojo;

    static PList<CompositionAndPojo> fromComposition(JavaOneOfComposition composition) {
      final PList<JavaObjectPojo> pojos = composition.getPojos().toPList();
      return pojos.map(pojo -> new CompositionAndPojo(Type.ONE_OF, pojos, pojo));
    }

    static PList<CompositionAndPojo> fromComposition(JavaAnyOfComposition composition) {
      final PList<JavaObjectPojo> pojos = composition.getPojos().toPList();
      return pojos.map(pojo -> new CompositionAndPojo(Type.ANY_OF, pojos, pojo));
    }

    String getReturnType() {
      return String.format("Optional<%s>", pojo.getClassName());
    }

    String getMethodName() {
      return String.format("get%s", pojo.getClassName());
    }

    String getArguments() {
      final PList<String> additionalArguments =
          type.equals(Type.ONE_OF) ? PList.single("Optional::empty") : PList.empty();
      return composedPojos
          .map(p -> p.equals(pojo) ? "Optional::of" : "ignore -> Optional.empty()")
          .concat(additionalArguments)
          .mkString(", ");
    }

    String getMethodCall() {
      if (type.equals(Type.ONE_OF)) {
        return String.format("foldOneOf(%s)", getArguments());
      } else {
        return String.format(
            "this.<Optional<%s>>foldAnyOf(%s).stream().findFirst().flatMap(Function.identity())",
            pojo.getClassName(), getArguments());
      }
    }
  }

  private enum Type {
    ONE_OF("oneOf"),
    ANY_OF("anyOf");

    private final String name;

    Type(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }
}
