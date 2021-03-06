package com.github.muehmar.gradle.openapi.util;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Optionals {
  private Optionals() {}

  @SafeVarargs
  public static <T> Optional<T> or(Optional<T>... optionals) {
    return or(
        Arrays.stream(optionals)
            .<Supplier<Optional<T>>>map(optional -> () -> optional)
            .collect(Collectors.toList()));
  }

  @SafeVarargs
  public static <T> Optional<T> or(Supplier<Optional<T>>... suppliers) {
    return or(Arrays.asList(suppliers));
  }

  public static <T> Optional<T> or(List<Supplier<Optional<T>>> suppliers) {
    for (Supplier<Optional<T>> supplier : suppliers) {
      final Optional<T> optional = supplier.get();
      if (optional.isPresent()) {
        return optional;
      }
    }
    return Optional.empty();
  }
}
