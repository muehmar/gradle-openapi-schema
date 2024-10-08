package com.github.muehmar.gradle.openapi.typemappingwithconversion;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import com.github.muehmar.gradle.openapi.util.ClassFinder;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

@SnapshotTest
class GeneratedClassesTest {
  private Expect expect;

  @Test
  @SnapshotName("allGeneratedClasses")
  void findNonTestClassesInPackage_when_modelPackage_then_allClasses() {
    final List<String> classNames =
        ClassFinder.findNonTestClassesInPackage(getClass().getPackage().getName()).stream()
            .filter(className -> !className.equals("CustomStrings"))
            .filter(className -> !className.equals("CustomString"))
            .filter(className -> !className.equals("CustomList"))
            .collect(Collectors.toList());

    expect.toMatchSnapshot(String.join("\n", classNames));
  }
}
