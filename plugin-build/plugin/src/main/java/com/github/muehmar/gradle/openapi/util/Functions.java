package com.github.muehmar.gradle.openapi.util;

import ch.bluecare.commons.data.Pair;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class Functions {
  private Functions() {}

  public static <T> Function<Pair<T, Integer>, T> first(UnaryOperator<T> allExceptFirst) {
    return pair -> {
      if (pair.second() == 0) {
        return allExceptFirst.apply(pair.first());
      } else {
        return pair.first();
      }
    };
  }

  public static <T> Function<Pair<T, Integer>, T> allExceptFirst(UnaryOperator<T> allExceptFirst) {
    return pair -> {
      if (pair.second() == 0) {
        return pair.first();
      } else {
        return allExceptFirst.apply(pair.first());
      }
    };
  }

  public static <A, B> Function<Pair<A, Integer>, B> firstAndTail(
      Function<A, B> first, Function<A, B> tail) {
    return pair -> {
      if (pair.second() == 0) {
        return first.apply(pair.first());
      } else {
        return tail.apply(pair.first());
      }
    };
  }
}
