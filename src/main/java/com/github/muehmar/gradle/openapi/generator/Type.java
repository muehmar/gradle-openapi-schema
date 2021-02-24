package com.github.muehmar.gradle.openapi.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.UnaryOperator;

public class Type {
  private final String name;
  private final List<String> imports;

  public Type(String name, List<String> imports) {
    this.name = name;
    this.imports = imports;
  }

  public Type(String name, String singleImport) {
    this(name, new ArrayList<>(Collections.singletonList(singleImport)));
  }

  public Type(String name) {
    this(name, new ArrayList<>());
  }

  public Type map(UnaryOperator<String> mapName, UnaryOperator<List<String>> mapImports) {
    return new Type(mapName.apply(name), mapImports.apply(imports));
  }

  public Type map(UnaryOperator<String> mapName) {
    return new Type(mapName.apply(name), imports);
  }

  public String getName() {
    return name;
  }

  public List<String> getImports() {
    return imports;
  }
}
