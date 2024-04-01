package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.wither;

import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TechnicalPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaDocGenerator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import lombok.Value;

public class WitherGenerator {
  private WitherGenerator() {}

  public static Generator<WitherContent, PojoSettings> witherGenerator() {
    return Generator.<WitherContent, PojoSettings>emptyGen()
        .appendList(method(), WitherMethod::fromWitherContent, newLine());
  }

  private static Generator<WitherMethod, PojoSettings> method() {
    final MethodGen<WitherMethod, PojoSettings> method =
        MethodGenBuilder.<WitherMethod, PojoSettings>create()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType(WitherMethod::className)
            .methodName(WitherMethod::witherName)
            .arguments(WitherMethod::argument)
            .doesNotThrow()
            .content(methodContent())
            .build();
    return JavaDocGenerator.<PojoSettings>javaDoc()
        .contraMap(WitherMethod::javaDocString)
        .append(method)
        .append((wm, s, w) -> wm.addRefs(w));
  }

  private static Generator<WitherMethod, PojoSettings> methodContent() {
    return Generator.<WitherMethod, PojoSettings>emptyGen()
        .append((wm, s, w) -> w.println("return %s;", wm.constructorCall()));
  }

  @Value
  @PojoBuilder(includeOuterClassName = false)
  public static class WitherContent {
    JavaName className;
    PList<JavaPojoMember> membersForWithers;
    PList<TechnicalPojoMember> technicalPojoMembers;
  }
}
