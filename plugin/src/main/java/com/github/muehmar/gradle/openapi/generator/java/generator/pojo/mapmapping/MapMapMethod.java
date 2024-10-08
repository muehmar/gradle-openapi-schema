package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.mapmapping;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_FUNCTION;
import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_HASH_MAP;
import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_MAP;
import static io.github.muehmar.codegenerator.Generator.constant;

import io.github.muehmar.codegenerator.Generator;

public class MapMapMethod {
  public static final String METHOD_NAME = "mapMap";

  private MapMapMethod() {}

  public static <A, B> Generator<A, B> mapMapMethod() {
    return Generator.<A, B>constant("private static <A, B, C, D, E> E %s(", METHOD_NAME)
        .append(constant("Map<String, A> map,"), 2)
        .append(constant("Function<A, B> mapMapItemType,"), 2)
        .append(constant("Function<B, C> wrapMapItemType,"), 2)
        .append(constant("Function<Map<String, C>, D> mapMapType,"), 2)
        .append(constant("Function<D, E> wrapMapType) {"), 2)
        .append(constant("if (map == null) {"), 1)
        .append(constant("return wrapMapType.apply(null);"), 2)
        .append(constant("}"), 1)
        .appendNewLine()
        .append(constant("final Map<String, C> mappedMapType ="), 1)
        .append(constant("map.entrySet().stream()"), 3)
        .append(constant(".collect("), 5)
        .append(constant("HashMap::new,"), 7)
        .append(constant("(m, entry) -> {"), 7)
        .append(
            constant(
                "final B b = entry.getValue() != null ? mapMapItemType.apply(entry.getValue()) : null;"),
            8)
        .append(constant("final C c = wrapMapItemType.apply(b);"), 8)
        .append(constant("m.put(entry.getKey(), c);"), 8)
        .append(constant("},"), 7)
        .append(constant("HashMap::putAll);"), 7)
        .appendNewLine()
        .append(constant("return wrapMapType.apply(mapMapType.apply(mappedMapType));"), 1)
        .append(constant("}"))
        .append(ref(JAVA_UTIL_FUNCTION))
        .append(ref(JAVA_UTIL_MAP))
        .append(ref(JAVA_UTIL_HASH_MAP));
  }
}
