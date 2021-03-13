package com.github.muehmar.gradle.openapi.util;

import ch.bluecare.commons.data.PList;
import java.util.Optional;
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
}
