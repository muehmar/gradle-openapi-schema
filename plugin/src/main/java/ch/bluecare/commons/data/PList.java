package ch.bluecare.commons.data;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A persistent singly linked list.
 *
 * @param <E>
 */
public abstract class PList<E> implements Iterable<E>, IntFunction<E> {
  private PList() {}

  // ---- constructors

  /**
   * Return the empty list.
   *
   * <p>It is the same list for all {@code A}.
   */
  @SuppressWarnings("unchecked")
  public static <A> PList<A> nil() {
    return (PList<A>) Nil.INSTANCE;
  }

  /**
   * Returns the empty list.
   *
   * <p>It is the same list for all {@code A}.
   */
  public static <A> PList<A> empty() {
    return nil();
  }

  /** Create a “cons cell”, using the given head and tail. */
  public static <A> PList<A> cell(A head, PList<A> tail) {
    return new Cons<>(head, tail, tail.size() + 1);
  }

  /** Create a new single element list containing the given element. */
  public static <A> PList<A> single(A element) {
    return cell(element, nil());
  }

  /**
   * Return either the empty list if {@code element} is {@code null}, or a singly element list with
   * that element.
   *
   * <p>Note that {@link #single(Object)} would create a single-element list where the element is
   * {@code null}.
   */
  public static <A> PList<A> fromNullable(A element) {
    return element == null ? nil() : single(element);
  }

  /**
   * Generate a list by using the output of the given function. The function is called until it
   * returns {@link Optional#empty()}.
   */
  public static <A, B> PList<B> generate(A init, Function<A, Optional<Pair<A, B>>> next) {
    Optional<Pair<A, B>> el = next.apply(init);
    PList<B> list = nil();
    while (el.isPresent()) {
      list = list.cons(el.get().second());
      el = next.apply(el.get().first());
    }
    return list;
  }

  /** Create a list from the given elements in the given order. */
  @SafeVarargs
  public static <A> PList<A> of(A... elements) {
    return fromArray(elements);
  }

  /** Create a list by calling the given supplier {@code length} times. */
  public static <A> PList<A> fill(Supplier<A> supplier, int length) {
    return generate(
        0, n -> n < length ? Optional.of(Pair.of(n + 1, supplier.get())) : Optional.empty());
  }

  /** Creates a list of integers, from {@code starte} (inclusive) to {@code end} (exclusive). */
  public static PList<Integer> range(int start, int end) {
    return generate(end - 1, n -> n >= start ? Optional.of(Pair.of(n - 1, n)) : Optional.empty());
  }

  /**
   * Create a list from an iterable, by traversing the iterable and cons-ing each element. The
   * resulting list reflects the same order as the iterator of the given iterable emitted during
   * traversal.
   */
  public static <A> PList<A> fromIter(Iterable<A> iter) {
    if (iter instanceof PList) {
      return (PList<A>) iter;
    } else {
      return iter == null ? nil() : collect(iter.iterator());
    }
  }

  /** Create a single element or the empty list from a given Optional. */
  public static <A> PList<A> fromOptional(Optional<A> opt) {
    return opt.map(PList::single).orElseGet(PList::nil);
  }

  /**
   * Create a list from the given array. It returns a list in same order as the given array. @param
   * values.
   */
  public static <A> PList<A> fromArray(A[] values) {
    if (values == null) {
      return nil();
    } else {
      return generate(
              0, n -> n < values.length ? Optional.of(Pair.of(n + 1, values[n])) : Optional.empty())
          .reverse();
    }
  }

  /** Traverse the iterator to create a new list. */
  public static <A> PList<A> collect(Iterator<A> iter) {
    if (iter == null) {
      return nil();
    } else {
      return generate(
              iter.hasNext(),
              b -> b ? Optional.of(Pair.swapped(iter.next(), iter.hasNext())) : Optional.empty())
          .reverse();
    }
  }

  /**
   * A collector that can be used with {@link Stream#collect(Collector)} to collect a stream into a
   * {@link PList}.
   */
  public static <T> Collector<T, ?, PList<T>> collector() {
    return Collectors.reducing(PList.nil(), PList::single, PList::concat);
  }

  // ~~~~~ abstract definitions

  /** Return the size of this list. The size is cached and doesn't require to traverse the list. */
  public abstract int size();

  /** Returns the tail of this list, that is without first element. */
  public abstract PList<E> tail();

  /**
   * Return the first element (the head) of this list. An exception is thrown if this is the empty
   * list.
   */
  public abstract E head();

  /** Check whether this is the empty list. */
  public abstract boolean isEmpty();

  // ---- implementation

  public E apply(int index) {
    AtomicInteger idx = new AtomicInteger(index);
    Optional<E> el =
        find(
            e -> {
              if (idx.get() == 0) {
                return true;
              } else {
                idx.set(idx.get() - 1);
                return false;
              }
            });
    return el.orElseThrow(() -> new IndexOutOfBoundsException("index: " + index));
  }

  public Function<Integer, E> asFunction() {
    return index -> {
      Objects.requireNonNull(index, "index must not be null");
      return apply(index);
    };
  }

  /** Concatenates the given list onto {@code this} list. */
  public PList<E> concat(PList<E> next) {
    return foldRight(next, PList::cons);
  }

  /**
   * Create a new list by adding {@code element} to the beginning of this list. That is, {@code
   * element} becomes the head and {@code this} becomes the tail of the new list.
   */
  public final PList<E> cons(E element) {
    return cell(element, this);
  }

  /**
   * Create a new list by adding {@code element} to the tail of this list, i. e. the head of the
   * list remains the same. Use this method carefully as it is expensive, use {@link
   * PList#cons(Object)} if possible.
   */
  public PList<E> add(E element) {
    return concat(PList.single(element));
  }

  /** Check whether the given element is a member of this list. */
  public boolean contains(E element, BiPredicate<E, E> eq) {
    return indexOf(element, eq) != -1;
  }

  /**
   * Drop the first {@code count} elements of this list. If {@code count} is greater than the size
   * of this list, the empty list is returned.
   */
  public final PList<E> drop(int count) {
    if (count < 0) {
      throw new IllegalArgumentException("count must be positive");
    }
    PList<E> result = this;
    for (int i = 0; i < count; i++) {
      if (result.nonEmpty()) {
        result = result.tail();
      }
    }
    return result;
  }

  /** Return a new list with the last {@code count} elements removed. */
  public final PList<E> dropRight(int count) {
    int len = size();
    return count >= len ? nil() : take(len - count);
  }

  /** Check whether any element in this list holds the predicate. */
  public boolean exists(Predicate<E> predicate) {
    return find(predicate).isPresent();
  }

  /** Return a new list with only those elements of {@code this} that holds the given predicate. */
  public PList<E> filter(Predicate<E> filter) {
    return flatMap(e -> filter.test(e) ? Collections.singletonList(e) : Collections.emptyList());
  }

  private PList<E> filterWith(BiPredicate<E, E> filter, Predicate<E> last) {
    return foldLeft(
            Pair.of(drop(1), PList.<E>nil()),
            (p, e) -> {
              Predicate<E> pred =
                  p.first()
                      .headOption()
                      .map(next -> (Predicate<E>) x -> filter.test(x, next))
                      .orElse(last);
              return pred.test(e)
                  ? Pair.of(p.first().drop(1), p.second().cons(e))
                  : Pair.of(p.first().drop(1), p.second());
            })
        .second();
  }

  /**
   * Filter this list by peeking the next element. The bi-predicate receives the current element as
   * first argument and the next element as second argument.
   */
  public PList<E> filterWithNext(BiPredicate<E, E> filter, Predicate<E> last) {
    return filterWith(filter, last).reverse();
  }

  /**
   * Filter this list by peeking the previous element. The bi-predicate receives the current element
   * as the second argument and the previous as first.
   */
  public PList<E> filterWithPrev(Predicate<E> first, BiPredicate<E, E> filter) {
    return reverse().filterWith((a, b) -> filter.test(b, a), first);
  }

  /**
   * Remove duplicate elements. The returned list is sorted.
   *
   * <p>A {@link Comparator} is used for determining whether two elements are a duplicate, because
   * the list is first sorted, and then subsequent elements are compared, using the same comparator.
   * If you can use {@code equals/hashCode} to compare elements, the {@link #distinct(Function)}
   * version is faster.
   */
  public PList<E> distinct(Comparator<E> comparator) {
    return sort(comparator)
        .filterWithNext((e1, e2) -> comparator.compare(e1, e2) != 0, ign -> true);
  }

  /**
   * Remove duplicate elements retaining the order. Duplicate elements are identified by using
   * {@link Object#equals(Object)} on the computed key of an element with {@code getKey}. The first
   * occurrence of an element is kept.
   *
   * <p>If you want to specify a custom function to determine a duplicate, use {@link
   * #distinct(Comparator)} instead.
   */
  public <T> PList<E> distinct(Function<E, T> getKey) {
    final Set<T> seen = new HashSet<>();
    Predicate<E> distinctFilter = e -> seen.add(getKey.apply(e));
    return filter(distinctFilter);
  }

  /**
   * Retain only elements that exists more than once. The returned list is sorted and each duplicate
   * is appears once.
   *
   * <p>A {@link Comparator} is used for determining whether two elements are a duplicate, because
   * the list is first sorted, and then subsequent elements are compared, using the same comparator.
   */
  public PList<E> duplicates(Comparator<E> comparator) {
    return sort(comparator)
        .filterWithNext((e1, e2) -> comparator.compare(e1, e2) == 0, ign -> false)
        .filterWithNext((e1, e2) -> comparator.compare(e1, e2) != 0, ign -> true);
  }

  /** Find the first element that holds the given predicate. */
  public Optional<E> find(Predicate<E> predicate) {
    PList<E> current = this;
    while (current.nonEmpty()) {
      if (predicate.test(current.head())) {
        return Optional.of(current.head());
      }
      current = current.tail();
    }
    return Optional.empty();
  }

  /** Apply {@code f} to each element in the list and concatenate the results. */
  public <B> PList<B> flatMap(Function<E, Iterable<B>> f) {
    PList<B> result = nil();
    PList<E> current = this;
    while (current.nonEmpty()) {
      Iterable<B> bs = f.apply(current.head());
      for (B b : bs) {
        result = result.cons(b);
      }
      current = current.tail();
    }
    return result.reverse();
  }

  /** Apply {@code f} to each element in the list and concatenate the non-empty results. */
  public <B> PList<B> flatMapOptional(Function<E, Optional<B>> f) {
    return flatMap(e -> PList.fromOptional(f.apply(e)));
  }

  /** Check whether the given predicate holds for all elements in this list. */
  public boolean forall(Predicate<E> predicate) {
    return !find(predicate.negate()).isPresent();
  }

  /**
   * Folds the list from the left.
   *
   * <p>The function f is applied to the {@code init} element and the first element of this list.
   * Then {@code f} is applied to the result and the second element in this list, and so on.
   *
   * <p>If this list is empty, the init element is returned and {@code f} is not invoked.
   *
   * <p>The list is alwayes completely traversed.
   *
   * @param init the initial element
   * @param f the function merging each element to the result of {@code f}
   */
  public <B> B foldLeft(B init, BiFunction<B, E, B> f) {
    B result = init;
    PList<E> list = this;
    while (list.nonEmpty()) {
      result = f.apply(result, list.head());
      list = list.tail();
    }
    return result;
  }

  /**
   * Same as {@link #foldLeft(Object, BiFunction)} but going from the right (the end of the list).
   *
   * <p>Note that this version is not recursive and therefore is stack-safe but lacks the
   * early-return feature.
   *
   * @param init the initial element of the fold
   * @param f the function merging the result of {@code f} with each element
   */
  public <B> B foldRight(B init, BiFunction<B, E, B> f) {
    return reverse().foldLeft(init, f);
  }

  /** Return a map of lists that are keyed by {@code f}. */
  public <K> Map<K, NonEmptyList<E>> groupBy(Function<E, K> f) {
    Map<K, NonEmptyList<E>> result = new HashMap<>();
    forEach(
        e -> {
          K key = f.apply(e);
          NonEmptyList<E> g = result.get(key);
          if (g == null) {
            result.put(key, NonEmptyList.single(e));
          } else {
            result.put(key, g.cons(e));
          }
        });
    return result;
  }

  /**
   * Returns the head of this list or {@link Optional#empty()} if this is the empty list.
   *
   * <p>Note: If the element exists but is {@code null}, then {@link Optional#empty()} is returned
   * as well!
   */
  public final Optional<E> headOption() {
    return isEmpty() ? Optional.empty() : Optional.ofNullable(head());
  }

  /** Returns the first index of the given element or -1 if not found. */
  public int indexOf(E element, BiPredicate<E, E> eq) {
    AtomicInteger index = new AtomicInteger(0);
    Optional<E> found =
        this.find(
            el -> {
              boolean r = eq.test(element, el);
              if (!r) {
                index.incrementAndGet();
              }
              return r;
            });
    return found.isPresent() ? index.get() : -1;
  }

  /** Return all indexes of the given element. */
  public PList<Integer> indexesOf(E element, BiPredicate<E, E> eq) {
    return zipWithIndex().filter(p -> eq.test(p.first(), element)).map(Pair::second);
  }

  @Override
  public Iterator<E> iterator() {
    return new Iterator<E>() {
      private PList<E> current = PList.this;

      @Override
      public boolean hasNext() {
        return current != PList.nil();
      }

      @Override
      public E next() {
        E next = current.head();
        current = current.tail();
        return next;
      }
    };
  }

  /**
   * Apply {@code f} to each element in this list, returning a new list preserving this structure.
   */
  public <B> PList<B> map(Function<E, B> f) {
    return flatMap(e -> Collections.singleton(f.apply(e)));
  }

  /** Return the maximum element according to the given comparator. */
  public Optional<E> max(Comparator<E> comparator) {
    return reduce((e1, e2) -> comparator.compare(e1, e2) < 0 ? e2 : e1);
  }

  /** Return the minimum element according to the given comparator. */
  public Optional<E> min(Comparator<E> comparator) {
    return reduce((e1, e2) -> comparator.compare(e1, e2) < 0 ? e1 : e2);
  }

  /**
   * Create a string where {@code sep} appears between elements. Elements are added using their
   * {@code toString()} method.
   */
  public String mkString(String sep) {
    StringBuilder buffer = new StringBuilder();
    forEach(
        el -> {
          if (buffer.length() != 0) {
            buffer.append(sep);
          }
          buffer.append(el);
        });
    return buffer.toString();
  }

  /** Check whether the list is not empty. */
  public final boolean nonEmpty() {
    return !isEmpty();
  }

  /** Reduces this list to a single value using the merge function {@code f}. */
  public Optional<E> reduce(BinaryOperator<E> f) {
    return isEmpty() ? Optional.empty() : Optional.of(tail().foldLeft(head(), f));
  }

  /** Reverse the order of this list. */
  public PList<E> reverse() {
    return foldLeft(nil(), PList::cons);
  }

  /** Take the first {@code count} elements from this list. */
  public final PList<E> take(int count) {
    int len = size();
    if (count >= len) {
      return this;
    } else {
      PList<E> result = nil();
      PList<E> current = this;
      for (int i = 0; i < count; i++) {
        result = result.cons(current.head());
        current = current.tail();
      }
      return result.reverse();
    }
  }

  /** Return a new list with only the last {@code count} elements of this list. */
  public final PList<E> takeRight(int count) {
    int len = size();
    return count >= len ? this : drop(len - count);
  }

  /** Return a new sorted version of this list. */
  public PList<E> sort(Comparator<E> comparator) {
    List<E> jl = toArrayList();
    jl.sort(comparator);
    return fromIter(jl);
  }

  /** Create a new mutable {@link ArrayList} from this persistent list. */
  public List<E> toArrayList() {
    List<E> result = new ArrayList<>();
    this.forEach(result::add);
    return result;
  }

  /** Return a new array containing the elements of this list. */
  @SuppressWarnings("unchecked")
  public E[] toArray(Class<E> type) {
    E[] array = (E[]) Array.newInstance(type, size());
    AtomicInteger index = new AtomicInteger(0);
    forEach(el -> array[index.getAndIncrement()] = el);
    return array;
  }

  /** Create a new primitive array of the given list. */
  public static byte[] toByteArray(PList<Byte> list) {
    byte[] result = new byte[list.size()];
    AtomicInteger index = new AtomicInteger(0);
    list.forEach(el -> result[index.getAndIncrement()] = el);
    return result;
  }

  /** Create a new primitive array of the given list. */
  public static int[] toIntArray(PList<Integer> list) {
    int[] result = new int[list.size()];
    AtomicInteger index = new AtomicInteger(0);
    list.forEach(el -> result[index.getAndIncrement()] = el);
    return result;
  }

  /** Create a new primitive array of the given list. */
  public static long[] toLongArray(PList<Integer> list) {
    long[] result = new long[list.size()];
    AtomicInteger index = new AtomicInteger(0);
    list.forEach(el -> result[index.getAndIncrement()] = el);
    return result;
  }

  /** Create a new primitive array of the given list. */
  public static float[] toFloatArray(PList<Integer> list) {
    float[] result = new float[list.size()];
    AtomicInteger index = new AtomicInteger(0);
    list.forEach(el -> result[index.getAndIncrement()] = el);
    return result;
  }

  /** Create a new primitive array of the given list. */
  public static double[] toDoubleArray(PList<Integer> list) {
    double[] result = new double[list.size()];
    AtomicInteger index = new AtomicInteger(0);
    list.forEach(el -> result[index.getAndIncrement()] = el);
    return result;
  }

  /** Create a mutable {@link HashSet} from this persistent list. */
  public Set<E> toHashSet() {
    Set<E> set = new HashSet<>();
    this.forEach(set::add);
    return set;
  }

  /** Create a {@link Stream} from this list. */
  public Stream<E> toStream() {
    return StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(iterator(), Spliterator.IMMUTABLE), false);
  }

  public <B> Iterable<Pair<E, B>> zipLazy(Iterable<B> other) {
    return () ->
        new Iterator<Pair<E, B>>() {
          private final Iterator<E> thisIter = PList.this.iterator();
          private final Iterator<B> otherIter = other.iterator();

          @Override
          public boolean hasNext() {
            return thisIter.hasNext() && otherIter.hasNext();
          }

          @Override
          public Pair<E, B> next() {
            return Pair.of(thisIter.next(), otherIter.next());
          }
        };
  }

  public <A> PList<Pair<E, A>> zip(Iterable<A> other) {
    return fromIter(zipLazy(other));
  }

  public PList<Pair<E, Integer>> zipWithIndex() {
    AtomicInteger index = new AtomicInteger(size() - 1);
    return foldRight(nil(), (r, el) -> r.cons(Pair.of(el, index.getAndDecrement())));
  }

  // -------------------------------------------------
  public static final class Cons<A> extends PList<A> {

    private final A head;
    private final PList<A> tail;
    private final int size;

    private Cons(A head, PList<A> tail, int size) {
      this.head = head;
      this.tail = tail;
      this.size = size;
    }

    @Override
    public int size() {
      return size;
    }

    @Override
    public PList<A> tail() {
      return tail;
    }

    @Override
    public A head() {
      return head;
    }

    @Override
    public boolean isEmpty() {
      return false;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      if (!super.equals(o)) {
        return false;
      }
      Cons<?> cons = (Cons<?>) o;
      return Objects.equals(head, cons.head) && Objects.equals(tail, cons.tail);
    }

    @Override
    public int hashCode() {
      return Objects.hash(super.hashCode(), head, tail);
    }
  }

  public static final class Nil extends PList<Object> {
    private static final PList<Object> INSTANCE = new Nil();

    private Nil() {}

    @Override
    public int size() {
      return 0;
    }

    @Override
    public PList<Object> tail() {
      throw new NoSuchElementException("tail of empty list");
    }

    @Override
    public Object head() {
      throw new NoSuchElementException("head of empty list");
    }

    @Override
    public boolean isEmpty() {
      return true;
    }

    @Override
    public PList<Object> reverse() {
      return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <B> PList<B> map(Function<Object, B> f) {
      return (PList<B>) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <B> PList<B> flatMap(Function<Object, Iterable<B>> f) {
      return (PList<B>) this;
    }

    @Override
    public PList<Object> concat(PList<Object> next) {
      return next;
    }

    @Override
    public boolean equals(Object obj) {
      return super.equals(obj) || (obj instanceof Nil);
    }

    // this makes not much sense, but sonar…
    @Override
    public int hashCode() {
      return super.hashCode();
    }
  }

  @Override
  public String toString() {
    return "[" + foldLeft("", (r, e) -> r.isEmpty() ? e + "" : r + ", " + e) + "]";
  }

  @Override
  public int hashCode() {
    return foldLeft(0, (n, e) -> n + 31 * e.hashCode());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || obj.getClass() != getClass()) {
      return false;
    }
    PList<?> other = (PList<?>) obj;
    if (other.size() != this.size()) {
      return false;
    }
    for (Pair<E, ?> p : this.zipLazy(other)) {
      if (!Objects.equals(p.first(), p.second())) {
        return false;
      }
    }
    return true;
  }
}
