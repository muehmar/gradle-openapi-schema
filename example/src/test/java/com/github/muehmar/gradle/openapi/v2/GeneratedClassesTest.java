package com.github.muehmar.gradle.openapi.v2;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import com.github.muehmar.gradle.openapi.util.ClassFinder;
import java.util.List;
import org.junit.jupiter.api.Test;

@SnapshotTest
class GeneratedClassesTest {
  private Expect expect;

  @Test
  @SnapshotName("allGeneratedClasses")
  void findNonTestClassesInPackage_when_modelPackage_then_allClasses() {
    final List<String> classNames =
        ClassFinder.findNonTestClassesInPackage(getClass().getPackage().getName());

    expect.toMatchSnapshot(String.join("\n", classNames));
  }
}
