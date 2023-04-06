package com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder;

import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaModifier.FINAL;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;
import static io.github.muehmar.codegenerator.java.JavaModifier.STATIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.ClassGenBuilder;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import lombok.Value;

class OptionalMemberBuilderGenerator {
  private OptionalMemberBuilderGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> generator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(optionalBuilder(), OptionalMember::fromObjectPojo, Generator.newLine());
  }

  private static Generator<OptionalMember, PojoSettings> optionalBuilder() {
    return ClassGenBuilder.<OptionalMember, PojoSettings>create()
        .clazz()
        .nested()
        .packageGen(Generator.emptyGen())
        .noJavaDoc()
        .noAnnotations()
        .modifiers(PUBLIC, STATIC, FINAL)
        .className(OptionalMember::builderClassName)
        .noSuperClass()
        .noInterfaces()
        .content(optionalBuilderContent())
        .build();
  }

  private static Generator<OptionalMember, PojoSettings> optionalBuilderContent() {
    final Generator<OptionalMember, PojoSettings> normalSetter = optionalBuilderSetter("%s %s");
    final Generator<OptionalMember, PojoSettings> optionalSetter =
        optionalBuilderSetter("Optional<%s> %s").append(w -> w.ref(JavaRefs.JAVA_UTIL_OPTIONAL));
    final Generator<OptionalMember, PojoSettings> tristateSetter =
        optionalBuilderSetter("Tristate<%s> %s").append(w -> w.ref(OpenApiUtilRefs.TRISTATE));

    return Generator.<OptionalMember, PojoSettings>emptyGen()
        .append(constant("private final Builder builder;"))
        .appendNewLine()
        .append((m, s, w) -> w.println("private OptBuilder%d(Builder builder) {", m.idx))
        .append(constant("this.builder = builder;"), 1)
        .append(constant("}"))
        .appendNewLine()
        .append(normalSetter)
        .appendSingleBlankLine()
        .append(optionalSetter.filter(OptionalMember::isNotNullable))
        .append(tristateSetter.filter(OptionalMember::isNullable));
  }

  private static Generator<OptionalMember, PojoSettings> optionalBuilderSetter(
      String argumentFormat) {
    final Generator<OptionalMember, PojoSettings> method =
        MethodGenBuilder.<OptionalMember, PojoSettings>create()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType(OptionalMember::nextBuilderClassName)
            .methodName(
                (m, s) -> m.member.prefixedMethodName(s.getBuilderMethodPrefix()).asString())
            .singleArgument(
                m ->
                    String.format(
                        argumentFormat,
                        m.member.getJavaType().getFullClassName().asString(),
                        m.member.getName()))
            .content(
                (m, s, w) ->
                    w.println(
                        "return new %s(builder.%s(%s));",
                        m.nextBuilderClassName(),
                        m.member.prefixedMethodName(s.getBuilderMethodPrefix()),
                        m.member.getName()))
            .build();
    return JavaDocGenerator.<PojoSettings>javaDoc()
        .<OptionalMember>contraMap(m -> m.member.getDescription())
        .append(method);
  }

  @Value
  private static class OptionalMember {
    JavaPojoMember member;
    int idx;

    private static PList<OptionalMember> fromObjectPojo(JavaObjectPojo pojo) {
      return pojo.getMembers()
          .filter(JavaPojoMember::isOptional)
          .zipWithIndex()
          .map(p -> new OptionalMember(p.first(), p.second()));
    }

    private String builderClassName() {
      return String.format("OptBuilder%d", idx);
    }

    private String nextBuilderClassName() {
      return String.format("OptBuilder%d", idx + 1);
    }

    public boolean isNullable() {
      return member.isNullable();
    }

    public boolean isNotNullable() {
      return member.isNotNullable();
    }
  }
}
