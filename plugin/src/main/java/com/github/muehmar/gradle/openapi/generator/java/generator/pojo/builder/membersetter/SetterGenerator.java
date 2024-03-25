package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter;

import static io.github.muehmar.codegenerator.java.MethodGen.Argument.argument;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaDocGenerator;
import io.github.muehmar.codegenerator.java.JavaModifiers;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import io.github.muehmar.codegenerator.writer.Writer;

public class SetterGenerator {
  private SetterGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> setterGenerator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(memberSetterMethods(), JavaObjectPojo::getAllMembers);
  }

  static Generator<JavaPojoMember, PojoSettings> memberSetterMethods() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .appendList(memberSetterMethod(), MemberSetter::fromMember);
  }

  private static Generator<MemberSetter, PojoSettings> memberSetterMethod() {
    final MethodGen<MemberSetter, PojoSettings> method =
        MethodGenBuilder.<MemberSetter, PojoSettings>create()
            .modifiers(
                (memberSetter, settings) -> JavaModifiers.of(memberSetter.modifier(settings)))
            .noGenericTypes()
            .returnType("Builder")
            .methodName(
                (memberSetter, settings) ->
                    memberSetter
                        .getMember()
                        .prefixedMethodName(settings.getBuilderMethodPrefix())
                        .asString())
            .singleArgument(
                memberSetter ->
                    argument(memberSetter.argumentType(), memberSetter.getMember().getName()))
            .doesNotThrow()
            .content(
                ((memberSetter, settings, writer) -> {
                  final Writer valueAssigmentWriter =
                      writer.println(
                          "this.%s = %s;",
                          memberSetter.getMember().getName(), memberSetter.memberValue());
                  final Writer flagAssigmentWriter =
                      memberSetter
                          .flagAssignment()
                          .map(valueAssigmentWriter::println)
                          .orElse(valueAssigmentWriter)
                          .println("return this;");
                  return memberSetter.addRefs(flagAssigmentWriter);
                }))
            .build();
    return Generator.<MemberSetter, PojoSettings>emptyGen()
        .append(
            JavaDocGenerator.javaDoc(), memberSetter -> memberSetter.getMember().getDescription())
        .append((m, s, w) -> m.annotationGenerator().generate(m, s, w))
        .append(method)
        .appendSingleBlankLine()
        .filter(MemberSetter::shouldBeUsed);
  }
}
