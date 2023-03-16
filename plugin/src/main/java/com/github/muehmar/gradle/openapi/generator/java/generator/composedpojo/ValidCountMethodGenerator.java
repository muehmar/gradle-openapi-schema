package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaComposedPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import java.util.Optional;
import lombok.Value;

public class ValidCountMethodGenerator {
  private ValidCountMethodGenerator() {}

  public static Generator<JavaComposedPojo, PojoSettings> validCountMethod() {
    return MethodGenBuilder.<JavaComposedPojo, PojoSettings>create()
        .modifiers(PRIVATE)
        .noGenericTypes()
        .returnType("int")
        .methodName("getValidCount")
        .noArguments()
        .content(validCountMethodContent())
        .build();
  }

  private static Generator<JavaComposedPojo, PojoSettings> validCountMethodContent() {
    final Generator<ComposedMemberPojo, PojoSettings> composedMemberPojoGen =
        (mp, s, w) -> w.println(mp.validCountLine());
    return Generator.<JavaComposedPojo, PojoSettings>ofWriterFunction(w -> w.println("return"))
        .appendList(composedMemberPojoGen.indent(1), ComposedMemberPojo::fromComposedPojo);
  }

  @Value
  private static class ComposedMemberPojo {
    JavaComposedPojo composedPojo;
    JavaPojo memberPojo;

    public static PList<ComposedMemberPojo> fromComposedPojo(JavaComposedPojo composedPojo) {
      return composedPojo
          .getJavaPojos()
          .map(memberPojo -> new ComposedMemberPojo(composedPojo, memberPojo));
    }

    private boolean isLast() {
      return composedPojo.getJavaPojos().reverse().headOption().equals(Optional.of(memberPojo));
    }

    private String validCountLine() {
      return String.format(
          "(%s() ? 1 : 0)%s",
          CompositionNames.isValidAgainstMethodName(memberPojo), isLast() ? ";" : " +");
    }
  }
}
