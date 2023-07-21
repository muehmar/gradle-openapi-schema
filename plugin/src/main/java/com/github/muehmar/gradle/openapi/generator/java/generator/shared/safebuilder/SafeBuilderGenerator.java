package com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.allof.AllOfBuilderGenerator.allOfBuilderGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.anyof.AnyOfBuilderGenerator.anyOfBuilderGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.oneof.OneOfBuilderGenerator.oneOfBuilderGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.property.FinalOptionalMemberBuilderGenerator.finalOptionalMemberBuilderGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.property.FinalRequiredMemberBuilderGenerator.finalRequiredMemberBuilderGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.property.OptionalMemberBuilderGenerator.optionalMemberBuilderGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.property.RequiredMemberBuilderGenerator.requiredMemberBuilderGenerator;
import static io.github.muehmar.codegenerator.Generator.constant;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.name.BuilderName;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.property.OptionalPropertyBuilderName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;

public class SafeBuilderGenerator implements Generator<JavaObjectPojo, PojoSettings> {
  private final Generator<JavaObjectPojo, PojoSettings> delegate;

  public SafeBuilderGenerator() {
    this.delegate =
        factoryMethod()
            .appendSingleBlankLine()
            .append(allOfBuilderGenerator())
            .appendSingleBlankLine()
            .append(oneOfBuilderGenerator())
            .appendSingleBlankLine()
            .append(anyOfBuilderGenerator())
            .appendSingleBlankLine()
            .append(requiredMemberBuilderGenerator())
            .appendSingleBlankLine()
            .append(finalRequiredMemberBuilderGenerator())
            .appendSingleBlankLine()
            .append(optionalMemberBuilderGenerator())
            .appendSingleBlankLine()
            .append(finalOptionalMemberBuilderGenerator())
            .filter(Filters.isSafeBuilder());
  }

  @Override
  public Writer generate(JavaObjectPojo data, PojoSettings settings, Writer writer) {
    return delegate.generate(data, settings, writer);
  }

  private Generator<JavaObjectPojo, PojoSettings> factoryMethod() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(
            (pojo, s, w) ->
                w.println(
                    "public static %s newBuilder() {",
                    createInitialBuilderName(pojo).currentName()))
        .append(
            (pojo, s, w) ->
                w.println(
                    "return new %s(new Builder());", createInitialBuilderName(pojo).currentName()),
            1)
        .append(constant("}"));
  }

  private static BuilderName createInitialBuilderName(JavaObjectPojo pojo) {
    if (pojo.isSimpleMapPojo()) {
      return OptionalPropertyBuilderName.initial(pojo);
    } else {
      return BuilderName.initial(pojo);
    }
  }
}
