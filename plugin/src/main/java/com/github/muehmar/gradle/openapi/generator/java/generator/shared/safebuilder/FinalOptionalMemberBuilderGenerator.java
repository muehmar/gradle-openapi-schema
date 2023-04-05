package com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder;

import static io.github.muehmar.codegenerator.Generator.constant;

import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

class FinalOptionalMemberBuilderGenerator {
  private FinalOptionalMemberBuilderGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> generator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(
            (p, s, w) ->
                w.println("public static final class OptBuilder%d {", optionalMemberCount(p)))
        .append(constant("private final Builder builder;"), 1)
        .appendNewLine()
        .append(
            (p, s, w) ->
                w.println("private OptBuilder%d(Builder builder) {", optionalMemberCount(p)),
            1)
        .append(constant("this.builder = builder;"), 2)
        .append(constant("}"), 1)
        .appendNewLine()
        .append((p, s, w) -> w.println("public %s build(){", p.getName()), 1)
        .append(constant("return builder.build();"), 2)
        .append(constant("}"), 1)
        .append(constant("}"));
  }

  private static int optionalMemberCount(JavaObjectPojo pojo) {
    return pojo.getMembers().filter(JavaPojoMember::isOptional).size();
  }
}
