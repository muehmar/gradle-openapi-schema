package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.mapmapping;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_FUNCTION;
import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_HASH_MAP;
import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_MAP;
import static io.github.muehmar.codegenerator.Generator.constant;

import io.github.muehmar.codegenerator.Generator;

public class UnmapMapMethod {
  public static final String METHOD_NAME = "unmapMap";

  private UnmapMapMethod() {}

  public static <A, B> Generator<A, B> unmapMapMethod() {
    return Generator.<A, B>constant(
            "private static <A, B, C, D, E> Map<String, E> %s(", METHOD_NAME)
        .append(constant("A map,"), 2)
        .append(constant("Function<A, B> unwrapMap,"), 2)
        .append(constant("Function<B, Map<String, C>> unmapMapType,"), 2)
        .append(constant("Function<C, D> unwrapMapItem,"), 2)
        .append(constant("Function<D, E> unmapMapItemType) {"), 2)
        .append(constant("if (map == null) {"), 1)
        .append(constant("return null;"), 2)
        .append(constant("}"), 1)
        .appendNewLine()
        .append(constant("final B unwrappedMap = unwrapMap.apply(map);"), 1)
        .appendNewLine()
        .append(constant("if (unwrappedMap == null) {"), 1)
        .append(constant("return null;"), 2)
        .append(constant("}"), 1)
        .appendNewLine()
        .append(
            constant("final Map<String, C> unmappedMapType = unmapMapType.apply(unwrappedMap);"), 1)
        .appendNewLine()
        .append(constant("if (unmappedMapType == null) {"), 1)
        .append(constant("return null;"), 2)
        .append(constant("}"), 1)
        .appendNewLine()
        .append(constant("return unmappedMapType.entrySet().stream()"), 1)
        .append(constant(".collect("), 3)
        .append(constant("HashMap::new,"), 5)
        .append(constant("(m, entry) -> {"), 5)
        .append(
            constant(
                "final D d = entry.getValue() != null ? unwrapMapItem.apply(entry.getValue()) : null;"),
            6)
        .append(constant("final E e = d != null ? unmapMapItemType.apply(d) : null;"), 6)
        .append(constant("m.put(entry.getKey(), e);"), 6)
        .append(constant("},"), 5)
        .append(constant("HashMap::putAll);"), 5)
        .append(constant("}"))
        .append(ref(JAVA_UTIL_MAP))
        .append(ref(JAVA_UTIL_FUNCTION))
        .append(ref(JAVA_UTIL_HASH_MAP));
  }
}
