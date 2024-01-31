package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant.FULL;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant.STANDARD;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.allof.AllOfBuilderGenerator.allOfBuilderGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.anyof.AnyOfBuilderGenerator.anyOfBuilderGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.oneof.OneOfBuilderGenerator.oneOfBuilderGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property.FinalOptionalMemberBuilderGenerator.finalOptionalMemberBuilderGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property.FinalRequiredMemberBuilderGenerator.finalRequiredMemberBuilderGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property.OptionalMemberBuilderGenerator.optionalMemberBuilderGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property.RequiredMemberBuilderGenerator.requiredMemberBuilderGenerator;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.ClassGen.Declaration.NESTED;
import static io.github.muehmar.codegenerator.java.JavaModifier.FINAL;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;
import static io.github.muehmar.codegenerator.java.JavaModifier.STATIC;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaDocGenerator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.function.Function;

public class SafeBuilderGenerator implements Generator<JavaObjectPojo, PojoSettings> {
  private static final String FACTORY_JAVA_DOC =
      "Instantiates a new staged builder. Explicit properties have precedence over additional properties, i.e. an "
          + "additional property with the same name as an explicit property will be discarded.";
  private final Generator<JavaObjectPojo, PojoSettings> delegate;

  private static final String BUILDER_STAGES_CLASS_NAME = "BuilderStages";

  public SafeBuilderGenerator() {
    this.delegate =
        Generator.<JavaObjectPojo, PojoSettings>emptyGen()
            .append(factoryMethods(FULL))
            .appendSingleBlankLine()
            .append(factoryMethods(STANDARD))
            .appendSingleBlankLine()
            .append(builderStagesClass())
            .filter(Filters.isSafeBuilder());
  }

  private static Generator<JavaObjectPojo, PojoSettings> factoryMethods(
      SafeBuilderVariant builderVariant) {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(JavaDocGenerator.ofJavaDocString(FACTORY_JAVA_DOC))
        .append(factoryMethod(builderVariant, simpleBuilderMethodName(builderVariant)))
        .appendSingleBlankLine()
        .append(JavaDocGenerator.ofJavaDocString(FACTORY_JAVA_DOC))
        .append(factoryMethod(builderVariant, pojoBuilderMethodName(builderVariant)));
  }

  private static Generator<JavaObjectPojo, PojoSettings> builderStagesClass() {
    return JavaGenerators.<JavaObjectPojo, PojoSettings>classGen()
        .clazz()
        .declaration(NESTED)
        .packageGen(Generator.emptyGen())
        .noJavaDoc()
        .noAnnotations()
        .modifiers(PUBLIC, STATIC, FINAL)
        .className(BUILDER_STAGES_CLASS_NAME)
        .noSuperClass()
        .noInterfaces()
        .content(
            privateBuilderStagesClassConstructor()
                .appendSingleBlankLine()
                .append(singleBuilderVariantContent(FULL))
                .appendSingleBlankLine()
                .append(singleBuilderVariantContent(STANDARD)))
        .build();
  }

  private static Generator<JavaObjectPojo, PojoSettings> privateBuilderStagesClassConstructor() {
    return Generator.constant("private %s() {}", BUILDER_STAGES_CLASS_NAME);
  }

  private static Generator<JavaObjectPojo, PojoSettings> singleBuilderVariantContent(
      SafeBuilderVariant builderVariant) {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
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
        .append(finalOptionalMemberBuilderGenerator(builderVariant));
  }

  @Override
  public Writer generate(JavaObjectPojo data, PojoSettings settings, Writer writer) {
    return delegate.generate(data, settings, writer);
  }

  private static Generator<JavaObjectPojo, PojoSettings> factoryMethod(
      SafeBuilderVariant builderVariant, Function<JavaObjectPojo, String> builderName) {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(
            (pojo, s, w) ->
                w.println(
                    "public static %s.%s %s() {",
                    BUILDER_STAGES_CLASS_NAME,
                    BuilderStage.createStages(builderVariant, pojo).head().getName(),
                    builderName.apply(pojo)))
        .append(
            (pojo, s, w) ->
                w.println(
                    "return new %s.%s(new Builder());",
                    BUILDER_STAGES_CLASS_NAME,
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
