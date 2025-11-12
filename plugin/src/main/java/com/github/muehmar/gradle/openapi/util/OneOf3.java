package com.github.muehmar.gradle.openapi.util;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/** Container class to hold the three code instances and exactly one is present. */
@EqualsAndHashCode
@ToString
public class OneOf3<T, R, S> {
  private final T first;
  private final R second;
  private final S third;

  private OneOf3(T first, R second, S third) {
    this.first = first;
    this.second = second;
    this.third = third;
  }

  public static <T, R, S> OneOf3<T, R, S> ofFirst(T first) {
    return new OneOf3<>(first, null, null);
  }

  public static <T, R, S> OneOf3<T, R, S> ofSecond(R second) {
    return new OneOf3<>(null, second, null);
  }

  public static <T, R, S> OneOf3<T, R, S> ofThird(S third) {
    return new OneOf3<>(null, null, third);
  }

  public <X> X fold(
      java.util.function.Function<T, X> onFirst,
      java.util.function.Function<R, X> onSecond,
      java.util.function.Function<S, X> onThird) {
    if (first != null) {
      return onFirst.apply(first);
    } else if (second != null) {
      return onSecond.apply(second);
    } else {
      return onThird.apply(third);
    }
  }
}
