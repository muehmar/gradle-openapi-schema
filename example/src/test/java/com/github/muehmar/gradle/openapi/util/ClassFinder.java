package com.github.muehmar.gradle.openapi.util;

import java.util.List;
import java.util.stream.Collectors;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

public class ClassFinder {
  private ClassFinder() {}

  public static List<String> findNonTestClassesInPackage(String packageName) {
    final Reflections reflections =
        new Reflections(packageName, Scanners.SubTypes.filterResultsBy(s -> true));
    return reflections.getSubTypesOf(Object.class).stream()
        .filter(clazz -> clazz.getEnclosingClass() == null)
        .map(Class::getName)
        .map(name -> name.replace(packageName + ".", ""))
        .filter(name -> !name.endsWith("Test"))
        .sorted()
        .collect(Collectors.toList());
  }
}
