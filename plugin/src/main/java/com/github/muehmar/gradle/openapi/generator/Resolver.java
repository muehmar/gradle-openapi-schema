package com.github.muehmar.gradle.openapi.generator;

import com.github.muehmar.gradle.openapi.generator.data.Name;
import com.github.muehmar.gradle.openapi.generator.data.Type;

public interface Resolver {
  Name getterName(Name name, Type type);

  Name setterName(Name name);

  Name witherName(Name name);

  Name memberName(Name name);

  Name className(Name name);

  Name enumName(Name name);

  Name enumMemberName(Name name);
}
