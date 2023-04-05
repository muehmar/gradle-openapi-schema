package com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder;

import static io.github.muehmar.codegenerator.Generator.constant;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;

public class SafeBuilderGenerator implements Generator<JavaObjectPojo, PojoSettings> {
  private final Generator<JavaObjectPojo, PojoSettings> delegate;

  public SafeBuilderGenerator() {
    this.delegate =
        this.<JavaObjectPojo>factoryMethod()
            .appendNewLine()
            .append(RequiredMemberBuilderGenerator.generator())
            .appendSingleBlankLine()
            .append(FinalRequiredMemberBuilderGenerator.generator())
            .appendSingleBlankLine()
            .append(OptionalMemberBuilderGenerator.generator())
            .filter(Filters.isSafeBuilder());
  }

  @Override
  public Writer generate(JavaObjectPojo data, PojoSettings settings, Writer writer) {
    return delegate.generate(data, settings, writer);
  }

  private <A> Generator<A, PojoSettings> factoryMethod() {
    return Generator.<A, PojoSettings>constant("public static Builder0 newBuilder() {")
        .append(constant("return new Builder0(new Builder());"), 1)
        .append(constant("}"));
  }
}
