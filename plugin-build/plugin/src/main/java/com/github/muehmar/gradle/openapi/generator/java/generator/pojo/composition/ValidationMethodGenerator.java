package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition;

import static com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames.Composition.isValidAgainstMethodName;
import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;

import com.github.muehmar.gradle.openapi.generator.java.model.composition.DiscriminatableJavaComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

public class ValidationMethodGenerator {
  private ValidationMethodGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> validationMethodGenerator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(
            isValidAgainstMethodForPojo(),
            pojo ->
                pojo.getDiscriminatableCompositions()
                    .flatMap(DiscriminatableJavaComposition::getPojos),
            newLine());
  }

  private static Generator<JavaObjectPojo, PojoSettings> isValidAgainstMethodForPojo() {
    return MethodGenBuilder.<JavaObjectPojo, PojoSettings>create()
        .modifiers(PRIVATE)
        .noGenericTypes()
        .returnType("boolean")
        .methodName(p -> isValidAgainstMethodName(p).asString())
        .noArguments()
        .doesNotThrow()
        .content(isValidAgainstMethodContent())
        .build();
  }

  private static Generator<JavaObjectPojo, PojoSettings> isValidAgainstMethodContent() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(
            (p, s, w) ->
                w.println(
                    "return %s().isValid();", MethodNames.Composition.asConversionMethodName(p)));
  }
}
