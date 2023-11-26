package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property;

import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.Generator.newLine;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.BuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

public class FinalRequiredMemberBuilderGenerator {
  private FinalRequiredMemberBuilderGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> finalRequiredMemberBuilderGenerator(
      SafeBuilderVariant builderVariant) {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(
            finalRequiredMemberBuilderGenerator(),
            pojo ->
                BuilderStage.createStages(builderVariant, pojo)
                    .toPList()
                    .flatMapOptional(BuilderStage::asLastRequiredPropertyBuilderStage),
            newLine());
  }

  public static Generator<LastRequiredPropertyBuilderStage, PojoSettings>
      finalRequiredMemberBuilderGenerator() {
    return Generator.<LastRequiredPropertyBuilderStage, PojoSettings>emptyGen()
        .append((stage, s, w) -> w.println("public static final class %s {", stage.getName()))
        .append(constant("private final Builder builder;"), 1)
        .appendNewLine()
        .append((stage, s, w) -> w.println("private %s(Builder builder) {", stage.getName()), 1)
        .append(constant("this.builder = builder;"), 2)
        .append(constant("}"), 1)
        .appendNewLine()
        .append(builderMethodsForLastRequiredPropertyBuilderStage(), 1)
        .append(constant("}"))
        .filter(stage -> stage.getBuilderVariant().equals(SafeBuilderVariant.STANDARD));
  }

  public static Generator<LastRequiredPropertyBuilderStage, PojoSettings>
      builderMethodsForLastRequiredPropertyBuilderStage() {
    return Generator.<LastRequiredPropertyBuilderStage, PojoSettings>emptyGen()
        .append(
            (stage, s, w) ->
                w.println("public %s andAllOptionals(){", stage.getNextStage().getName()))
        .append(
            (stage, s, w) -> w.println("return new %s(builder);", stage.getNextStage().getName()),
            1)
        .append(constant("}"))
        .appendNewLine()
        .append(constant("public Builder andOptionals(){"))
        .append(constant("return builder;"), 1)
        .append(constant("}"))
        .appendNewLine()
        .append(
            (stage, s, w) -> w.println("public %s build(){", stage.getParentPojo().getClassName()))
        .append(constant("return builder.build();"), 1)
        .append(constant("}"));
  }
}
