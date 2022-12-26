package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import com.github.muehmar.gradle.openapi.generator.model.pojo.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;

public class ComposedPojoGenerator implements Generator<ComposedPojo, PojoSettings> {

  private final Generator<ComposedPojo, PojoSettings> delegate;

  public ComposedPojoGenerator() {
    this.delegate = Generator.emptyGen();
  }

  @Override
  public Writer generate(ComposedPojo data, PojoSettings settings, Writer writer) {
    return delegate.generate(data, settings, writer);
  }
}
