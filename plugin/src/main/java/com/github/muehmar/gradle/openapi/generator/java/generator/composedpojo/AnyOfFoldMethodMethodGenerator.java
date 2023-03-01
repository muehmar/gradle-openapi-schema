package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import static com.github.muehmar.gradle.openapi.generator.model.pojo.ComposedPojo.CompositionType.ANY_OF;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaComposedPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import lombok.Value;

public class AnyOfFoldMethodMethodGenerator {
  private AnyOfFoldMethodMethodGenerator() {}

  public static Generator<JavaComposedPojo, PojoSettings> generator() {
    return MethodGenBuilder.<JavaComposedPojo, PojoSettings>create()
        .modifiers(PUBLIC)
        .genericTypes("T")
        .returnType("List<T>")
        .methodName("fold")
        .arguments(AnyOfFoldMethodMethodGenerator::foldMethodArguments)
        .content(methodContent())
        .build()
        .append(w -> w.ref(JavaRefs.JAVA_UTIL_LIST))
        .filter(p -> p.getCompositionType().equals(ANY_OF));
  }

  private static PList<String> foldMethodArguments(JavaComposedPojo composedPojo) {
    return composedPojo
        .getJavaPojos()
        .map(JavaPojo::getName)
        .map(name -> String.format("Function<%s, T> on%s", name, name));
  }

  private static Generator<JavaComposedPojo, PojoSettings> methodContent() {
    return Generator.<JavaComposedPojo, PojoSettings>emptyGen()
        .append(constant("final List<T> result = new ArrayList<>();"))
        .appendList(singleFold(), ComposedAndMemberPojo::fromComposedPojo)
        .append(constant("return result;"));
  }

  private static Generator<ComposedAndMemberPojo, PojoSettings> singleFold() {
    return Generator.<ComposedAndMemberPojo, PojoSettings>emptyGen()
        .append(
            (p, s, w) -> w.println("if (isValidAgainst%s()) {", p.memberPojo.getName().getName()))
        .append(
            (p, s, w) ->
                w.println(
                    "result.add(on%s(as%s()));", p.memberPojo.getName(), p.memberPojo.getName()),
            1)
        .append(constant("}"));
  }

  @Value
  private static class ComposedAndMemberPojo {
    JavaComposedPojo composedPojo;
    JavaPojo memberPojo;

    private static PList<ComposedAndMemberPojo> fromComposedPojo(JavaComposedPojo composedPojo) {
      return composedPojo.getJavaPojos().map(pojo -> new ComposedAndMemberPojo(composedPojo, pojo));
    }
  }
}
