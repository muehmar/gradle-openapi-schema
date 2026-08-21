package com.github.muehmar.gradle.openapi.typemappingwithconversion;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CustomList<T> {
  private final List<T> list;

  public CustomList(List<T> list) {
    this.list = list;
  }

  public static <T> CustomList<T> fromList(List<T> list) {
    return new CustomList<>(list);
  }

  public static <T> CustomList<T> emptyCustomList() {
    return customList();
  }

  @SafeVarargs
  public static <T> CustomList<T> customList(T... items) {
    return new CustomList<>(Arrays.asList(items));
  }

  public List<T> asList() {
    return list;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final CustomList<?> that = (CustomList<?>) o;
    return Objects.equals(list, that.list);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(list);
  }

  @Override
  public String toString() {
    return "CustomList{" + "list=" + list + '}';
  }
}
