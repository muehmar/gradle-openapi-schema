package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.listmapping;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_FUNCTION;
import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_LIST;
import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_STREAM_COLLECTORS;
import static io.github.muehmar.codegenerator.Generator.constant;

import io.github.muehmar.codegenerator.Generator;

public class UnmapListMethod {
  public static final String METHOD_NAME = "unmapList";

  private UnmapListMethod() {}

  public static <A, B> Generator<A, B> unmapListMethod() {
    return Generator.<A, B>constant("private static <A, B, C, D, E> List<E> unmapList(")
        .append(constant("A list,"), 2)
        .append(constant("Function<A, B> unwrapList,"), 2)
        .append(constant("Function<B, List<C>> unmapListType,"), 2)
        .append(constant("Function<C, D> unwrapListItem,"), 2)
        .append(constant("Function<D, E> unmapListItemType) {"), 2)
        .append(constant("if (list == null) {"), 1)
        .append(constant("return null;"), 2)
        .append(constant("}"), 1)
        .appendNewLine()
        .append(constant("final B unwrappedList = unwrapList.apply(list);"), 1)
        .appendNewLine()
        .append(constant("if (unwrappedList == null) {"), 1)
        .append(constant("return null;"), 2)
        .append(constant("}"), 1)
        .appendNewLine()
        .append(constant("final List<C> unmappedListType = unmapListType.apply(unwrappedList);"), 1)
        .appendNewLine()
        .append(constant("if (unmappedListType == null) {"), 1)
        .append(constant("return null;"), 2)
        .append(constant("}"), 1)
        .appendNewLine()
        .append(constant("return unmappedListType.stream()"), 1)
        .append(constant(".map(i -> i != null ? unwrapListItem.apply(i) : null)"), 3)
        .append(constant(".map(i -> i != null ? unmapListItemType.apply(i) : null)"), 3)
        .append(constant(".collect(Collectors.toList());"), 3)
        .append(constant("}"))
        .append(ref(JAVA_UTIL_LIST))
        .append(ref(JAVA_UTIL_FUNCTION))
        .append(ref(JAVA_UTIL_STREAM_COLLECTORS));
  }
}
