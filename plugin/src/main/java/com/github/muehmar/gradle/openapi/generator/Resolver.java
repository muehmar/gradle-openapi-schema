package com.github.muehmar.gradle.openapi.generator;

public interface Resolver {
  String getterName(String key, Type type);

  String setterName(String key);

  String witherName(String key);

  String memberName(String key);

  String className(String key);

  String enumName(String key);
}
