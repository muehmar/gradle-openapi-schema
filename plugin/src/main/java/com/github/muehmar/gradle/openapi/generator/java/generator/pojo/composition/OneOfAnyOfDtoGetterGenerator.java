package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static com.github.muehmar.gradle.openapi.generator.java.model.composition.DiscriminatableJavaComposition.Type.ANY_OF;
import static com.github.muehmar.gradle.openapi.generator.java.model.composition.DiscriminatableJavaComposition.Type.ONE_OF;
import static com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames.Composition.foldCompositionMethodName;
import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.DiscriminatableJavaComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaDocGenerator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.java.MethodGen;
import java.util.function.Function;
import lombok.Value;

public class OneOfAnyOfDtoGetterGenerator {
  private static final Function<CompositionAndPojo, String> JAVA_DOC_FOR_POJO =
      cp ->
          String.format(
              "Returns {@link %s} of the %s composition in case it is valid against the schema %s wrapped in an "
                  + "{@link Optional}, empty otherwise.",
              cp.getPojo().getClassName(),
              cp.getType().getName().startLowerCase(),
              cp.getPojo().getSchemaName());

  private OneOfAnyOfDtoGetterGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> oneOfAnyOfDtoGetterGenerator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendOptional(getterGenerator(), JavaObjectPojo::getOneOfComposition)
        .appendSingleBlankLine()
        .appendOptional(getterGenerator(), JavaObjectPojo::getAnyOfComposition);
  }

  private static <T extends DiscriminatableJavaComposition>
      Generator<T, PojoSettings> getterGenerator() {
    return Generator.<T, PojoSettings>emptyGen()
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
            .doesNotThrow()
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
    DiscriminatableJavaComposition.Type type;
    PList<JavaObjectPojo> composedPojos;
    DiscriminatableJavaComposition composition;
    JavaObjectPojo pojo;

    static PList<CompositionAndPojo> fromComposition(DiscriminatableJavaComposition composition) {
      final PList<JavaObjectPojo> pojos = composition.getPojos().toPList();
      return pojos.map(
          pojo -> new CompositionAndPojo(composition.getType(), pojos, composition, pojo));
    }

    String getReturnType() {
      return String.format("Optional<%s>", pojo.getClassName());
    }

    String getMethodName() {
      return String.format("get%s", pojo.getClassName());
    }

    boolean isSingleResultComposition() {
      return type.equals(ONE_OF) || (type.equals(ANY_OF) && composition.hasDiscriminator());
    }

    String getArguments() {
      final PList<String> additionalArguments =
          isSingleResultComposition() ? PList.single("Optional::empty") : PList.empty();
      return composedPojos
          .map(p -> p.equals(pojo) ? "Optional::of" : "ignore -> Optional.empty()")
          .concat(additionalArguments)
          .mkString(", ");
    }

    String getMethodCall() {
      if (isSingleResultComposition()) {
        return String.format("%s(%s)", foldCompositionMethodName(type), getArguments());
      } else {
        return String.format(
            "this.<Optional<%s>>%s(%s).stream().findFirst().flatMap(Function.identity())",
            pojo.getClassName(), foldCompositionMethodName(type), getArguments());
      }
    }
  }
}
