package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import java.util.Optional;
import java.util.function.Function;
import lombok.Value;

public class ValidationMethodGenerator {
  private ValidationMethodGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> isValidAgainstMethods() {
    final Function<JavaObjectPojo, Iterable<JavaObjectPojo>> getOneOfPojosMembers =
        p -> p.getOneOfComposition().map(JavaOneOfComposition::getPojos).orElseGet(PList::empty);
    final Function<JavaObjectPojo, Iterable<JavaObjectPojo>> getAnyOfPojoMembers =
        p -> p.getAnyOfComposition().map(JavaAnyOfComposition::getPojos).orElseGet(PList::empty);
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
        .methodName(p -> CompositionNames.isValidAgainstMethodName(p).asString())
        .noArguments()
        .content(isValidAgainstMethodContent())
        .build()
        .filter(Filters.isValidationEnabled());
  }

  private static Generator<JavaObjectPojo, PojoSettings> isValidAgainstMethodContent() {
    final Generator<JavaObjectPojo, PojoSettings> nonEmptyPojo =
        Generator.<JavaObjectPojo, PojoSettings>ofWriterFunction(w -> w.println("return"))
            .appendList(
                javaPojoMemberGen(),
                pojo -> pojo.getMembers().map(member -> new JavaPojoAndMember(pojo, member)))
            .filter(pojo -> pojo.getMembers().nonEmpty());
    final Generator<JavaObjectPojo, PojoSettings> emptyPojo =
        Generator.<JavaObjectPojo, PojoSettings>ofWriterFunction(w -> w.println("return true;"))
            .filter(pojo -> pojo.getMembers().isEmpty());
    return nonEmptyPojo.append(emptyPojo);
  }

  private static Generator<JavaPojoAndMember, PojoSettings> javaPojoMemberGen() {
    final Generator<JavaPojoAndMember, PojoSettings> requiredNotNullableGen =
        (pm, s, w) ->
            w.println("%s != null%s", pm.member.getNameAsIdentifier(), pm.ampersandOrSemicolon());
    final Generator<JavaPojoAndMember, PojoSettings> requiredNullableGen =
        (pm, s, w) ->
            w.println("%s%s", pm.member.getIsPresentFlagName(), pm.ampersandOrSemicolon());
    return Generator.<JavaPojoAndMember, PojoSettings>emptyGen()
        .appendConditionally(JavaPojoAndMember::isRequiredAndNotNullable, requiredNotNullableGen)
        .appendConditionally(JavaPojoAndMember::isRequiredAndNullable, requiredNullableGen)
        .indent(1);
  }

  @Value
  private static class JavaPojoAndMember {
    JavaObjectPojo pojo;
    JavaPojoMember member;

    boolean isRequiredAndNotNullable() {
      return member.isRequiredAndNotNullable();
    }

    public boolean isRequiredAndNullable() {
      return member.isRequiredAndNullable();
    }

    boolean isLast() {
      return pojo.getMembers()
          .filter(JavaPojoMember::isRequired)
          .reverse()
          .headOption()
          .equals(Optional.of(member));
    }

    String ampersandOrSemicolon() {
      return isLast() ? ";" : " &&";
    }
  }
}
