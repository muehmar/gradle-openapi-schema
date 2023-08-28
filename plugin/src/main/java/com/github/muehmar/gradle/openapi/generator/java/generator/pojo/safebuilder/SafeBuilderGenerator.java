package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder;

import static io.github.muehmar.codegenerator.Generator.constant;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.allof.AllOfBuilderGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.anyof.AnyOfBuilderGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.name.BuilderName;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.oneof.OneOfBuilderGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property.FinalOptionalMemberBuilderGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property.FinalRequiredMemberBuilderGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property.OptionalMemberBuilderGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property.OptionalPropertyBuilderName;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property.RequiredMemberBuilderGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.function.Function;

public class SafeBuilderGenerator implements Generator<JavaObjectPojo, PojoSettings> {
  private final Generator<JavaObjectPojo, PojoSettings> delegate;

  public SafeBuilderGenerator() {
    this.delegate =
        factoryMethod(pojo -> "builder")
            .appendSingleBlankLine()
            .append(factoryMethod(pojo -> pojo.getClassName().startLowercase() + "Builder"))
            .appendSingleBlankLine()
            .append(AllOfBuilderGenerator.allOfBuilderGenerator())
            .appendSingleBlankLine()
            .append(OneOfBuilderGenerator.oneOfBuilderGenerator())
            .appendSingleBlankLine()
            .append(AnyOfBuilderGenerator.anyOfBuilderGenerator())
            .appendSingleBlankLine()
            .append(RequiredMemberBuilderGenerator.requiredMemberBuilderGenerator())
            .appendSingleBlankLine()
            .append(FinalRequiredMemberBuilderGenerator.finalRequiredMemberBuilderGenerator())
            .appendSingleBlankLine()
            .append(OptionalMemberBuilderGenerator.optionalMemberBuilderGenerator())
            .appendSingleBlankLine()
            .append(FinalOptionalMemberBuilderGenerator.finalOptionalMemberBuilderGenerator())
            .filter(Filters.isSafeBuilder());
  }

  @Override
  public Writer generate(JavaObjectPojo data, PojoSettings settings, Writer writer) {
    return delegate.generate(data, settings, writer);
  }

  private Generator<JavaObjectPojo, PojoSettings> factoryMethod(
      Function<JavaObjectPojo, String> builderName) {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(
            (pojo, s, w) ->
                w.println(
                    "public static %s %s() {",
                    createInitialBuilderName(pojo).currentName(), builderName.apply(pojo)))
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
