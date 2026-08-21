package ch.bluecare.commons.data;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public final class Pair<A, B> {
  private final A first;
  private final B second;

  private Pair(A first, B second) {
    this.first = first;
    this.second = second;
  }

  public static <A, B> Pair<A, B> of(A a, B b) {
    return new Pair<>(a, b);
  }

  public static <A, B> Pair<A, B> swapped(B b, A a) {
    return of(a, b);
  }

  public A first() {
    return first;
  }

  public B second() {
    return second;
  }

  public <C> Pair<C, B> mapFirst(Function<A, C> f) {
    return new Pair<>(f.apply(first), second);
  }

  public <C> Pair<A, C> mapSecond(Function<B, C> f) {
    return new Pair<>(first, f.apply(second));
  }

  public static <A, B, C> BiFunction<A, B, C> untupled(Function<Pair<A, B>, C> f) {
    return (a, b) -> f.apply(Pair.of(a, b));
  }

  public static <A, B, C> Function<Pair<A, B>, C> tupled(BiFunction<A, B, C> f) {
    return p -> f.apply(p.first, p.second);
  }

  public static <A, B> Predicate<Pair<A, B>> tupled(BiPredicate<A, B> f) {
    return p -> f.test(p.first, p.second);
  }

  public static <A, B> BiPredicate<A, B> untupled(Predicate<Pair<A, B>> f) {
    return (a, b) -> f.test(Pair.of(a, b));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Pair<?, ?> pair = (Pair<?, ?>) o;
    return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
  }

  @Override
  public int hashCode() {
    return Objects.hash(first, second);
  }

  @Override
  public String toString() {
    return "Pair{" + "first=" + first + ", second=" + second + '}';
  }
}
