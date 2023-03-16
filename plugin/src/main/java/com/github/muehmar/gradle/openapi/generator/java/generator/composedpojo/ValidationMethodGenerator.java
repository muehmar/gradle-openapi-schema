package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import java.util.Optional;
import lombok.Value;

public class ValidationMethodGenerator {
  private ValidationMethodGenerator() {}

  public static Generator<JavaPojo, PojoSettings> isValidAgainstMethod() {
    return MethodGenBuilder.<JavaPojo, PojoSettings>create()
        .modifiers(PRIVATE)
        .noGenericTypes()
        .returnType("boolean")
        .methodName(p -> CompositionNames.isValidAgainstMethodName(p).asString())
        .noArguments()
        .content(isValidAgainstMethodContent())
        .build()
        .filter(Filters.isValidationEnabled());
  }

  private static Generator<JavaPojo, PojoSettings> isValidAgainstMethodContent() {
    final Generator<JavaPojo, PojoSettings> nonEmptyPojo =
        Generator.<JavaPojo, PojoSettings>ofWriterFunction(w -> w.println("return"))
            .appendList(
                javaPojoMemberGen(),
                pojo -> pojo.getMembersOrEmpty().map(member -> new JavaPojoAndMember(pojo, member)))
            .filter(pojo -> pojo.getMembersOrEmpty().nonEmpty());
    final Generator<JavaPojo, PojoSettings> emptyPojo =
        Generator.<JavaPojo, PojoSettings>ofWriterFunction(w -> w.println("return true;"))
            .filter(pojo -> pojo.getMembersOrEmpty().isEmpty());
    return nonEmptyPojo.append(emptyPojo);
  }

  private static Generator<JavaPojoAndMember, PojoSettings> javaPojoMemberGen() {
    final Generator<JavaPojoAndMember, PojoSettings> requiredNotNullableGen =
        (pm, s, w) -> w.println("%s != null%s", pm.member.getName(), pm.ampersandOrSemicolon());
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
    JavaPojo pojo;
    JavaPojoMember member;

    boolean isRequired() {
      return member.isRequired();
    }

    boolean isRequiredAndNotNullable() {
      return member.isRequiredAndNotNullable();
    }

    public boolean isRequiredAndNullable() {
      return member.isRequiredAndNullable();
    }

    boolean isLast() {
      return pojo.getMembersOrEmpty()
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
