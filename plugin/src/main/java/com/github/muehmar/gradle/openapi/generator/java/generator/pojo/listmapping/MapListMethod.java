package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.listmapping;

import static io.github.muehmar.codegenerator.Generator.constant;

import io.github.muehmar.codegenerator.Generator;

public class MapListMethod {
  public static final String METHOD_NAME = "mapList";

  private MapListMethod() {}

  public static <A, B> Generator<A, B> mapListMethod() {
    return Generator.<A, B>constant("private static <A, B, C, D, E> E mapList(")
        .append(constant("A list,"), 2)
        .append(constant("Function<A, B> mapListItemType,"), 2)
        .append(constant("Function<B, C> wrapListItem,"), 2)
        .append(constant("Function<List<C>, D> mapListType,"), 2)
        .append(constant("Function<D, E> wrapListType) {"), 2)
        .append(constant("if (list == null) {"), 1)
        .append(constant("return wrapListType.apply(null);"), 2)
        .append(constant("}"), 1)
        .appendNewLine()
        .append(constant("final List<C> mappedListType ="), 1)
        .append(constant("list.stream()"), 3)
        .append(constant(".map(i -> i != null ? mapListItemType.apply(i) : null)"), 5)
        .append(constant(".map(wrapListItem)"), 5)
        .append(constant(".collect(Collectors.toList());"), 5)
        .appendNewLine()
        .append(constant("return wrapListType.apply(mapListType.apply(mappedListType));"), 1)
        .append(constant("}"));
  }
}
