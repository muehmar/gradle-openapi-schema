package com.github.muehmar.gradle.openapi.snapshot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import au.com.origin.snapshots.Snapshot;
import au.com.origin.snapshots.reporters.SnapshotReporter;

public class JUnitSnapshotReporter implements SnapshotReporter {
  @Override
  public boolean supportsFormat(String s) {
    return true;
  }

  @Override
  public void report(Snapshot snapshot, Snapshot snapshot1) {
    assertEquals(snapshot.getBody(), snapshot1.getBody());
  }
}
