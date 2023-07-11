package com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder;

import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import io.github.muehmar.codegenerator.writer.Writer;
import io.github.muehmar.pojobuilder.annotations.BuildMethod;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import lombok.Value;

public class SingleMemberSetterGenerator {
  private SingleMemberSetterGenerator() {}

  public static <T extends Member> Generator<T, PojoSettings> singleMemberSetterGenerator(
      Setter<T> setter) {
    return singleMemberSetterGenerator(PList.single(setter));
  }

  public static <T extends Member> Generator<T, PojoSettings> singleMemberSetterGenerator(
      PList<Setter<T>> setters) {
    return setters
        .map(
            setter ->
                SingleMemberSetterGenerator.<T>builderSetter(setter.argumentFormat())
                    .append(setter::addRefs)
                    .filter(setter::includeInBuilder))
        .reduce((gen1, gen2) -> gen1.appendSingleBlankLine().append(gen2))
        .orElse(Generator.emptyGen());
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

  @PojoBuilder(builderName = "SetterBuilder")
  @Value
  public static class SetterBuilder<T extends Member> {
    IncludeInBuilder<T> includeInBuilder;
    String argumentFormat;
    AddRefs addRefs;

    @BuildMethod
    public static <T extends Member> Setter<T> buildSetter(SetterBuilder<T> setter) {
      return new Setter<T>() {
        @Override
        public boolean includeInBuilder(T member) {
          return setter.includeInBuilder.includeInBuilder(member);
        }

        @Override
        public String argumentFormat() {
          return setter.argumentFormat;
        }

        @Override
        public Writer addRefs(Writer writer) {
          return setter.addRefs.addRefs(writer);
        }
      };
    }
  }

  public interface IncludeInBuilder<T extends Member> {
    boolean includeInBuilder(T member);
  }

  public interface AddRefs {
    Writer addRefs(Writer writer);
  }
}
