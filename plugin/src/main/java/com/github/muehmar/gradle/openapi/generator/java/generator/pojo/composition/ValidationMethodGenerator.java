package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;
import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;

import ch.bluecare.commons.data.NonEmptyList;
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
        .methodName(p -> CompositionNames.isValidAgainstMethodName(p).asString())
        .noArguments()
        .content(isValidAgainstMethodContent())
        .build()
        .filter(Filters.isValidationEnabled());
  }

  private static Generator<JavaObjectPojo, PojoSettings> isValidAgainstMethodContent() {
    return isValidAgainstMethodContentForPojoWithRequiredMember()
        .append(isValidAgainstMethodContentForPojoWithoutRequiredMember());
  }

  private static Generator<JavaObjectPojo, PojoSettings>
      isValidAgainstMethodContentForPojoWithRequiredMember() {
    return Generator.<JavaObjectPojo, PojoSettings>ofWriterFunction(w -> w.println("return"))
        .appendList(
            singlePojoMemberCondition(),
            pojo -> pojo.getAllMembers().map(member -> new JavaPojoAndMember(pojo, member)))
        .filter(ValidationMethodGenerator::hasRequiredInAllMembers);
  }

  private static Generator<JavaObjectPojo, PojoSettings>
      isValidAgainstMethodContentForPojoWithoutRequiredMember() {
    return Generator.<JavaObjectPojo, PojoSettings>ofWriterFunction(w -> w.println("return true;"))
        .filter(ValidationMethodGenerator::hasNoRequiredInAllMembers);
  }

  private static Generator<JavaPojoAndMember, PojoSettings> singlePojoMemberCondition() {
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

  private static boolean hasRequiredInAllMembers(JavaObjectPojo pojo) {
    return pojo.getAllMembers().exists(JavaPojoMember::isRequired);
  }

  private static boolean hasNoRequiredInAllMembers(JavaObjectPojo pojo) {
    return not(hasRequiredInAllMembers(pojo));
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
      return pojo.getAllMembers()
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
