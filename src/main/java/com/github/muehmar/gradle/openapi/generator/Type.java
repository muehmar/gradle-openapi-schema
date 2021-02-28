package com.github.muehmar.gradle.openapi.generator;

import java.util.Set;

public interface Type {
  String getName();

  Set<String> getImports();
}
