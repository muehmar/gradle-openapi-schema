package com.github.muehmar.gradle.openapi.util;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** A generic container that can hold either one of two types or both. */
@EqualsAndHashCode
@ToString
public class OneOrBoth<T, S> {
  private final T first;
  private final S second;

  private OneOrBoth(T first, S second) {
    this.first = first;
    this.second = second;
  }

  public static <T, S> OneOrBoth<T, S> ofFirst(T first) {
    return new OneOrBoth<>(first, null);
  }

  public static <T, S> OneOrBoth<T, S> ofSecond(S second) {
    return new OneOrBoth<>(null, second);
  }

  public static <T, S> OneOrBoth<T, S> ofBoth(T first, S second) {
    return new OneOrBoth<>(first, second);
  }

  public static <T, S> OneOrBoth<T, S> ofBothOptional(T first, Optional<S> second) {
    return new OneOrBoth<>(first, second.orElse(null));
  }

  public static <T, S> OneOrBoth<T, S> ofBothOptional(Optional<T> first, S second) {
    return new OneOrBoth<>(first.orElse(null), second);
  }

  public <R> R fold(Function<T, R> onFirst, Function<S, R> onSecond, BiFunction<T, S, R> onBoth) {
    if (first != null && second != null) {
      return onBoth.apply(first, second);
    } else if (first != null) {
      return onFirst.apply(first);
    } else {
      return onSecond.apply(second);
    }
  }
}
