package com.github.muehmar.gradle.openapi.util;

import ch.bluecare.commons.data.PList;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class Optionals {
  private Optionals() {}

  @SafeVarargs
  public static <T> Optional<T> or(Optional<T>... optionals) {
    return or(PList.fromArray(optionals).map(optional -> () -> optional));
  }

  @SafeVarargs
  public static <T> Optional<T> or(Supplier<Optional<T>>... suppliers) {
    return or(PList.fromArray(suppliers));
  }

  public static <T> Optional<T> or(PList<Supplier<Optional<T>>> suppliers) {
    for (Supplier<Optional<T>> supplier : suppliers) {
      final Optional<T> optional = supplier.get();
      if (optional.isPresent()) {
        return optional;
      }
    }
    return Optional.empty();
  }

  public static <A, B, T> Optional<T> combine(
      Optional<A> a,
      Optional<B> b,
      Function<A, T> mapA,
      Function<B, T> mapB,
      BiFunction<A, B, T> combine) {
    return a.flatMap(a1 -> b.map(b1 -> combine.apply(a1, b1)))
        .map(Optional::of)
        .orElseGet(() -> a.map(mapA).map(Optional::of).orElseGet(() -> b.map(mapB)));
  }
}
