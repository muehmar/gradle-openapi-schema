package ch.bluecare.commons.data;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.IntFunction;

/** A {@link PList} that has at least one element. */
public final class NonEmptyList<A> implements Iterable<A>, IntFunction<A> {

  private final A head;
  private final PList<A> tail;
  private final PList<A> all;

  public NonEmptyList(A head, PList<A> tail) {
    this.head = head;
    this.tail = tail;
    this.all = tail.cons(head);
  }

  @SuppressWarnings("unchecked")
  public static <A> NonEmptyList<A> of(A first, A... other) {
    return new NonEmptyList<>(first, PList.fromArray(other));
  }

  /** Creates a single-element list. */
  public static <A> NonEmptyList<A> single(A el) {
    return new NonEmptyList<>(el, PList.nil());
  }

  /**
   * Create a non-empty list from a given iterable. Use the various constructors on {@link PList} to
   * create iterables from other values.
   */
  public static <A> Optional<NonEmptyList<A>> fromIter(Iterable<A> iter) {
    PList<A> list = PList.fromIter(iter);
    return list.nonEmpty()
        ? Optional.of(new NonEmptyList<>(list.head(), list.tail()))
        : Optional.empty();
  }

  /** Return the head of this list. */
  public A head() {
    return head;
  }

  /** Return the tail of this list. */
  public PList<A> tail() {
    return tail;
  }

  /** Appends an iterable to this list. */
  public NonEmptyList<A> concat(Iterable<A> next) {
    return new NonEmptyList<>(head, tail.concat(PList.fromIter(next)));
  }

  /** Prepend the given element to this list. */
  public NonEmptyList<A> cons(A el) {
    return new NonEmptyList<>(el, toPList());
  }

  /**
   * Create a new list by adding {@code element} to the tail of this list, i. e. the head of the
   * list remains the same. Use this method carefully as it is expensive, use {@link
   * PList#cons(Object)} if possible.
   */
  public NonEmptyList<A> add(A element) {
    return new NonEmptyList<>(head, tail.add(element));
  }

  /** Map the given function across this list. */
  public <B> NonEmptyList<B> map(Function<A, B> f) {
    return new NonEmptyList<>(f.apply(head), tail.map(f));
  }

  /** Apply the function to every element and flatten the results. */
  public <B> NonEmptyList<B> flatMap(Function<A, NonEmptyList<B>> f) {
    PList<B> pl = toPList().flatMap(e -> f.apply(e).toPList());
    return new NonEmptyList<>(pl.head(), pl.tail());
  }

  /** Reduces this list to a single value using the merge function {@code f}. */
  public A reduce(BinaryOperator<A> f) {
    return tail.foldLeft(head, f);
  }

  /** Reverses this list. */
  public NonEmptyList<A> reverse() {
    PList<A> pl = toPList().reverse();
    return new NonEmptyList<>(pl.head(), pl.tail());
  }

  /** Sorts this list according to the given comparator. */
  public NonEmptyList<A> sort(Comparator<A> comparator) {
    PList<A> pl = toPList().sort(comparator);
    return new NonEmptyList<>(pl.head(), pl.tail());
  }

  public int size() {
    return tail.size() + 1;
  }

  /** Convert this to a {@link PList} which will be non-empty. */
  public PList<A> toPList() {
    return all;
  }

  public NonEmptyList<Pair<A, Integer>> zipWithIndex() {
    PList<Pair<A, Integer>> pl = toPList().zipWithIndex();
    return new NonEmptyList<>(pl.head(), pl.tail());
  }

  @Override
  public A apply(int value) {
    return value == 0 ? head : tail.apply(value - 1);
  }

  @Override
  public Iterator<A> iterator() {
    return toPList().iterator();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NonEmptyList<?> that = (NonEmptyList<?>) o;
    return Objects.equals(head, that.head) && Objects.equals(tail, that.tail);
  }

  @Override
  public int hashCode() {
    return Objects.hash(head, tail);
  }

  @Override
  public String toString() {
    return toPList().toString();
  }
}
