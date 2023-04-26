package com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder;

import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaModifier.FINAL;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;
import static io.github.muehmar.codegenerator.java.JavaModifier.STATIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.ClassGenBuilder;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import io.github.muehmar.codegenerator.writer.Writer;

class SingleMemberBuilderGenerator {
  private SingleMemberBuilderGenerator() {}

  static <T extends Member> Generator<T, PojoSettings> generator(PList<Setter<T>> setters) {
    return ClassGenBuilder.<T, PojoSettings>create()
        .clazz()
        .nested()
        .packageGen(Generator.emptyGen())
        .noJavaDoc()
        .noAnnotations()
        .modifiers(PUBLIC, STATIC, FINAL)
        .className(T::builderClassName)
        .noSuperClass()
        .noInterfaces()
        .content(builderContent(setters))
        .build();
  }

  private static <T extends Member> PList<Generator<T, PojoSettings>> builderContent(
      PList<Setter<T>> setters) {
    final PList<Generator<T, PojoSettings>> setterGenerators =
        setters.map(
            setter ->
                SingleMemberBuilderGenerator.<T>builderSetter(setter.argumentFormat())
                    .append(setter::addRefs)
                    .appendSingleBlankLine()
                    .filter(setter::includeInBuilder));

    final Generator<T, PojoSettings> memberAndConstructorGenerator =
        Generator.<T, PojoSettings>emptyGen()
            .append(constant("private final Builder builder;"))
            .appendNewLine()
            .append((m, s, w) -> w.println("private %s(Builder builder) {", m.builderClassName()))
            .append(constant("this.builder = builder;"), 1)
            .append(constant("}"))
            .appendSingleBlankLine();
    return setterGenerators.cons(memberAndConstructorGenerator);
  }

  private static <T extends Member> Generator<T, PojoSettings> builderSetter(
      String argumentFormat) {
    final Generator<T, PojoSettings> method =
        MethodGenBuilder.<T, PojoSettings>create()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType(T::nextBuilderClassName)
            .methodName(
                (m, s) -> m.getMember().prefixedMethodName(s.getBuilderMethodPrefix()).asString())
            .singleArgument(
                m ->
                    String.format(
                        argumentFormat,
                        m.getMember().getJavaType().getFullClassName().asString(),
                        m.getMember().getNameAsIdentifier()))
            .content(
                (m, s, w) ->
                    w.println(
                        "return new %s(builder.%s(%s));",
                        m.nextBuilderClassName(),
                        m.getMember().prefixedMethodName(s.getBuilderMethodPrefix()),
                        m.getMember().getNameAsIdentifier()))
            .build();
    return JavaDocGenerator.<PojoSettings>javaDoc()
        .<T>contraMap(m -> m.getMember().getDescription())
        .append(method);
  }

  public interface Member {
    String builderClassName();

    String nextBuilderClassName();

    JavaPojoMember getMember();
  }

  public interface Setter<T extends Member> {
    boolean includeInBuilder(T member);

    String argumentFormat();

    Writer addRefs(Writer writer);
  }
}
