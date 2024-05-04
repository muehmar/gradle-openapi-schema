package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.maplistitem;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_FUNCTION;
import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_LIST;
import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_STREAM_COLLECTORS;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.java.JavaModifier.STATIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

public class MapListItemMethod {
  public static final String METHOD_NAME = "mapListItem";

  private MapListItemMethod() {}

  public static <A> Generator<A, PojoSettings> mapListItemMethod() {
    return MethodGenBuilder.<A, PojoSettings>create()
        .modifiers(PRIVATE, STATIC)
        .genericTypes("S", "T")
        .returnType("List<T>")
        .methodName(METHOD_NAME)
        .arguments(
            ignore ->
                PList.of(
                    new MethodGen.Argument("List<S>", "list"),
                    new MethodGen.Argument("Function<S, T>", "mapItem")))
        .doesNotThrow()
        .content(content())
        .build()
        .append(ref(JAVA_UTIL_LIST))
        .append(ref(JAVA_UTIL_FUNCTION))
        .append(ref(JAVA_UTIL_STREAM_COLLECTORS));
  }

  private static <A> Generator<A, PojoSettings> content() {
    return Generator.<A, PojoSettings>emptyGen()
        .append(constant("if (list == null) {"))
        .append(constant("return null;"), 1)
        .append(constant("}"))
        .append((constant("return list.stream().map(mapItem).collect(Collectors.toList());")));
  }
}
