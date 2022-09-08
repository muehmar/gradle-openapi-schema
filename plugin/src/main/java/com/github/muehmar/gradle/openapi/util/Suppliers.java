package com.github.muehmar.gradle.openapi.util;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class Suppliers {
  private Suppliers() {}

  /** Not thread safe cached {@link Supplier}. */
  public static <T> Supplier<T> cached(Supplier<T> supplier) {
    final AtomicReference<T> ref = new AtomicReference<>();
    return () -> {
      final T currentValue = ref.get();
      if (currentValue != null) {
        return currentValue;
      } else {
        final T newValue = supplier.get();
        ref.set(newValue);
        return newValue;
      }
    };
  }
}
