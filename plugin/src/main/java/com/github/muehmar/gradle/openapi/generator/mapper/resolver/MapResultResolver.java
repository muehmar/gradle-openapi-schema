package com.github.muehmar.gradle.openapi.generator.mapper.resolver;

import com.github.muehmar.gradle.openapi.generator.mapper.MapResult;
import com.github.muehmar.gradle.openapi.generator.mapper.UnresolvedMapResult;

public interface MapResultResolver {
  MapResult resolve(UnresolvedMapResult unresolvedMapResult);
}
