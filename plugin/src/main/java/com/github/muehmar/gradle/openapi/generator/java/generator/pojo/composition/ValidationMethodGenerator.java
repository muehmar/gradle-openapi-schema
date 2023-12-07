package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition;

import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import java.util.function.Function;

public class ValidationMethodGenerator {
  private ValidationMethodGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> validationMethodGenerator() {
    final Function<JavaObjectPojo, Iterable<JavaObjectPojo>> getOneOfPojosMembers =
        p ->
            p.getOneOfComposition()
                .map(JavaOneOfComposition::getPojos)
                .map(NonEmptyList::toPList)
                .orElseGet(PList::empty);
    final Function<JavaObjectPojo, Iterable<JavaObjectPojo>> getAnyOfPojoMembers =
        p ->
            p.getAnyOfComposition()
                .map(JavaAnyOfComposition::getPojos)
                .map(NonEmptyList::toPList)
                .orElseGet(PList::empty);
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(isValidAgainstMethodForPojo(), getOneOfPojosMembers, newLine())
        .appendSingleBlankLine()
        .appendList(isValidAgainstMethodForPojo(), getAnyOfPojoMembers, newLine());
  }

  private static Generator<JavaObjectPojo, PojoSettings> isValidAgainstMethodForPojo() {
    return MethodGenBuilder.<JavaObjectPojo, PojoSettings>create()
        .modifiers(PRIVATE)
        .noGenericTypes()
        .returnType("boolean")
        .methodName(p -> MethodNames.Composition.isValidAgainstMethodName(p).asString())
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
