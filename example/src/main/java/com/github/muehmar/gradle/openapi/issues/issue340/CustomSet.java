package com.github.muehmar.gradle.openapi.issues.issue340;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/** Custom Set wrapper for testing type mapping with uniqueItems arrays. */
public class CustomSet<T> {
  private final Set<T> delegate;

  public CustomSet() {
    this.delegate = new HashSet<>();
  }

  public CustomSet(Collection<? extends T> collection) {
    this.delegate = new HashSet<>(collection);
  }

  public static <T> CustomSet<T> fromSet(Set<T> set) {
    return new CustomSet<>(set);
  }

  public Set<T> asSet() {
    return new HashSet<>(delegate);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CustomSet<?> customSet = (CustomSet<?>) o;
    return delegate.equals(customSet.delegate);
  }

  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  @Override
  public String toString() {
    return "CustomSet" + delegate.toString();
  }
}
