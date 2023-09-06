package com.github.muehmar.gradle.openapi.snapshot;

import au.com.origin.snapshots.junit5.SnapshotExtension;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

/** Convenient annotation to activate snapshot testing while enabling IntelliJ to provide the diffs.*/
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({SnapshotExtension.class, IntellijDiffSnapshotTestExtension.class})
public @interface SnapshotTest {}
