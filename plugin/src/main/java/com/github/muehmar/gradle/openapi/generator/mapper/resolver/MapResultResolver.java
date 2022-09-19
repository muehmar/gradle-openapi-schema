package com.github.muehmar.gradle.openapi.generator.mapper.resolver;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.MapResult;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;

public interface MapResultResolver {
  PList<Pojo> resolve(MapResult mapResult);
}
