package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.allof.AllOfBuilderGenerator.allOfBuilderGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.anyof.AnyOfBuilderGenerator.anyOfBuilderGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.oneof.OneOfBuilderGenerator.oneOfBuilderGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property.FinalOptionalMemberBuilderGenerator.finalOptionalMemberBuilderGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property.FinalRequiredMemberBuilderGenerator.finalRequiredMemberBuilderGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property.OptionalMemberBuilderGenerator.optionalMemberBuilderGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property.RequiredMemberBuilderGenerator.requiredMemberBuilderGenerator;
import static io.github.muehmar.codegenerator.Generator.constant;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaDocGenerator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.function.Function;

public class SafeBuilderGenerator implements Generator<JavaObjectPojo, PojoSettings> {
  private static final String FACTORY_JAVA_DOC =
      "Instantiates a new staged builder. Explicit properties have precedence over additional properties, i.e. an "
          + "additional property with the same name as an explicit property will be discarded if the explicit "
          + "property is set or if it gets set.";
  private final Generator<JavaObjectPojo, PojoSettings> delegate;

  public SafeBuilderGenerator(SafeBuilderVariant builderVariant) {
    this.delegate =
        JavaDocGenerator.<JavaObjectPojo, PojoSettings>ofJavaDocString(FACTORY_JAVA_DOC)
            .append(factoryMethod(builderVariant, simpleBuilderMethodName(builderVariant)))
            .appendSingleBlankLine()
            .append(JavaDocGenerator.ofJavaDocString(FACTORY_JAVA_DOC))
            .append(factoryMethod(builderVariant, pojoBuilderMethodName(builderVariant)))
            .appendSingleBlankLine()
            .append(allOfBuilderGenerator(builderVariant))
            .appendSingleBlankLine()
            .append(oneOfBuilderGenerator(builderVariant))
            .appendSingleBlankLine()
            .append(anyOfBuilderGenerator(builderVariant))
            .appendSingleBlankLine()
            .append(requiredMemberBuilderGenerator(builderVariant))
            .appendSingleBlankLine()
            .append(finalRequiredMemberBuilderGenerator(builderVariant))
            .appendSingleBlankLine()
            .append(optionalMemberBuilderGenerator(builderVariant))
            .appendSingleBlankLine()
            .append(finalOptionalMemberBuilderGenerator(builderVariant))
            .filter(Filters.isSafeBuilder());
  }

  @Override
  public Writer generate(JavaObjectPojo data, PojoSettings settings, Writer writer) {
    return delegate.generate(data, settings, writer);
  }

  private Generator<JavaObjectPojo, PojoSettings> factoryMethod(
      SafeBuilderVariant builderVariant, Function<JavaObjectPojo, String> builderName) {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(
            (pojo, s, w) ->
                w.println(
                    "public static %s %s() {",
                    BuilderStage.createStages(builderVariant, pojo).head().getName(),
                    builderName.apply(pojo)))
        .append(
            (pojo, s, w) ->
                w.println(
                    "return new %s(new Builder());",
                    BuilderStage.createStages(builderVariant, pojo).head().getName()),
            1)
        .append(constant("}"));
  }

  private static Function<JavaObjectPojo, String> simpleBuilderMethodName(
      SafeBuilderVariant builderVariant) {
    return pojo -> {
      final String prefix = builderVariant.getBuilderNamePrefix();
      if (prefix.isEmpty()) {
        return "builder";
      } else {
        return String.format("%sBuilder", prefix.toLowerCase());
      }
    };
  }

  private static Function<JavaObjectPojo, String> pojoBuilderMethodName(
      SafeBuilderVariant builderVariant) {
    return pojo -> {
      final String prefix = builderVariant.getBuilderNamePrefix();
      final JavaName className = pojo.getClassName();
      if (prefix.isEmpty()) {
        return String.format("%sBuilder", className.startLowerCase());
      } else {
        return String.format("%s%sBuilder", prefix.toLowerCase(), className);
      }
    };
  }
}
