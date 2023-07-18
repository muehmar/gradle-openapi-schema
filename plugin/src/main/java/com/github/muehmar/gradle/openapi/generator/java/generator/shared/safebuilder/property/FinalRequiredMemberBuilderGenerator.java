package com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.property;

import static io.github.muehmar.codegenerator.Generator.constant;

import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

public class FinalRequiredMemberBuilderGenerator {
  private FinalRequiredMemberBuilderGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> generator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append((p, s, w) -> w.println("public static final class %s {", builderName(p)))
        .append(constant("private final Builder builder;"), 1)
        .appendNewLine()
        .append((p, s, w) -> w.println("private %s(Builder builder) {", builderName(p)), 1)
        .append(constant("this.builder = builder;"), 2)
        .append(constant("}"), 1)
        .appendNewLine()
        .append(builderMethods(), 1)
        .append(constant("}"));
  }

  public static Generator<JavaObjectPojo, PojoSettings> builderMethods() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append((p, s, w) -> w.println("public %s andAllOptionals(){", nextBuilderName(p)))
        .append(constant("return new OptBuilder0(builder);"), 1)
        .append(constant("}"))
        .appendNewLine()
        .append(constant("public Builder andOptionals(){"))
        .append(constant("return builder;"), 1)
        .append(constant("}"))
        .appendNewLine()
        .append((p, s, w) -> w.println("public %s build(){", p.getClassName()))
        .append(constant("return builder.build();"), 1)
        .append(constant("}"));
  }

  private static String builderName(JavaObjectPojo pojo) {
    return RequiredPropertyBuilderName.from(
            pojo, pojo.getMembers().filter(JavaPojoMember::isRequired).size())
        .currentName();
  }

  private static String nextBuilderName(JavaObjectPojo pojo) {
    return OptionalPropertyBuilderName.initial(pojo).currentName();
  }
}
