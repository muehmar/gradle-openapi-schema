package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.StagedBuilderVariant.FULL;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.StagedBuilderVariant.STANDARD;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.factory.FactoryMethods.factoryMethods;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.property.FinalOptionalMemberBuilderGenerator.finalOptionalMemberBuilderGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.property.FinalRequiredMemberBuilderGenerator.finalRequiredMemberBuilderGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.property.OptionalMemberBuilderGenerator.optionalMemberBuilderGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.property.RequiredMemberBuilderGenerator.requiredMemberBuilderGenerator;
import static io.github.muehmar.codegenerator.java.ClassGen.Declaration.NESTED;
import static io.github.muehmar.codegenerator.java.JavaModifier.FINAL;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;
import static io.github.muehmar.codegenerator.java.JavaModifier.STATIC;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.allof.AllOfBuilderGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.anyof.AnyOfBuilderGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.oneof.OneOfBuilderGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.writer.Writer;

public class StagedBuilderGenerator implements Generator<JavaObjectPojo, PojoSettings> {
  private final Generator<JavaObjectPojo, PojoSettings> delegate;

  public static final String BUILDER_STAGES_CLASS_NAME = "BuilderStages";

  public StagedBuilderGenerator() {
    this.delegate =
        Generator.<JavaObjectPojo, PojoSettings>emptyGen()
            .append(factoryMethods())
            .appendSingleBlankLine()
            .append(builderStagesClass())
            .filter(Filters.isStagedBuilder());
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
      StagedBuilderVariant builderVariant) {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(AllOfBuilderGenerator.allOfBuilderGenerator(builderVariant))
        .appendSingleBlankLine()
        .append(OneOfBuilderGenerator.oneOfBuilderGenerator(builderVariant))
        .appendSingleBlankLine()
        .append(AnyOfBuilderGenerator.anyOfBuilderGenerator(builderVariant))
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
}
