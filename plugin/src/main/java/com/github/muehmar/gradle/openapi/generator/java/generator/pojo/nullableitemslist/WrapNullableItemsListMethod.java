package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.nullableitemslist;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_LIST;
import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_STREAM_COLLECTORS;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.java.JavaModifier.STATIC;

import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

public class WrapNullableItemsListMethod {
  public static final String METHOD_NAME = "wrapNullableItemsList";

  private WrapNullableItemsListMethod() {}

  public static <A> Generator<A, PojoSettings> wrapNullableItemsListMethod() {
    return MethodGenBuilder.<A, PojoSettings>create()
        .modifiers(PRIVATE, STATIC)
        .singleGenericType(ignore -> "T")
        .returnType("List<Optional<T>>")
        .methodName(METHOD_NAME)
        .singleArgument(ignore -> new MethodGen.Argument("List<T>", "list"))
        .doesNotThrow()
        .content(content())
        .build()
        .append(ref(JAVA_UTIL_OPTIONAL))
        .append(ref(JAVA_UTIL_LIST))
        .append(ref(JAVA_UTIL_STREAM_COLLECTORS));
  }

  private static <A> Generator<A, PojoSettings> content() {
    return Generator.<A, PojoSettings>emptyGen()
        .append(constant("if (list == null) {"))
        .append(constant("return null;"), 1)
        .append(constant("}"))
        .append(
            constant(
                "return list.stream().map(Optional::ofNullable).collect(Collectors.toList());"));
  }
}
