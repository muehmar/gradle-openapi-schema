package com.github.muehmar.gradle.openapi.generator;

import com.github.muehmar.gradle.openapi.generator.data.Type;

public interface Resolver {
  String getterName(String key, Type type);

  String setterName(String key);

  String witherName(String key);

  String memberName(String key);

  String className(String key);

  String enumName(String key);
}
