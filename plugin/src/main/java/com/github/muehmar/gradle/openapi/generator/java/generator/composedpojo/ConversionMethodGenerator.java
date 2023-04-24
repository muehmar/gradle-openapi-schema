package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import java.util.Optional;
import lombok.Value;

public class ConversionMethodGenerator {
  private ConversionMethodGenerator() {}

  public static Generator<JavaPojo, PojoSettings> asDtoMethod() {
    return MethodGenBuilder.<JavaPojo, PojoSettings>create()
        .modifiers(PRIVATE)
        .noGenericTypes()
        .returnType(pojo -> pojo.getClassName().asString())
        .methodName(pojo -> CompositionNames.asConversionMethodName(pojo).asString())
        .noArguments()
        .content(asDtoMethodContent())
        .build();
  }

  private static Generator<JavaPojo, PojoSettings> asDtoMethodContent() {
    final Generator<ConstructorArgument, PojoSettings> memberGen =
        (a, s, w) -> w.println("%s%s", a.getArgumentName(), a.getCommaOrNothing());
    return Generator.<JavaPojo, PojoSettings>emptyGen()
        .append((p, s, w) -> w.println("return new %s(", p.getClassName()))
        .appendList(
            memberGen.indent(1),
            pojo ->
                pojo.getMembersOrEmpty()
                    .map(m -> new PojoAndMember(pojo, m))
                    .flatMap(PojoAndMember::asConstructorArguments))
        .append(w -> w.println(");"));
  }

  @Value
  private static class PojoAndMember {
    JavaPojo pojo;
    JavaPojoMember member;

    private boolean isLast() {
      return pojo.getMembersOrEmpty().reverse().headOption().equals(Optional.of(member));
    }

    private String commaOrNothing() {
      return isLast() ? "" : ",";
    }

    private PList<ConstructorArgument> asConstructorArguments() {
      if (member.isRequiredAndNotNullable() || member.isOptionalAndNotNullable()) {
        return PList.single(
            new ConstructorArgument(member.getJavaName().asIdentifier(), commaOrNothing()));
      } else if (member.isRequiredAndNullable()) {
        return PList.of(
            new ConstructorArgument(member.getJavaName().asIdentifier(), ","),
            new ConstructorArgument(member.getIsPresentFlagName(), commaOrNothing()));
      } else {
        return PList.of(
            new ConstructorArgument(member.getJavaName().asIdentifier(), ","),
            new ConstructorArgument(member.getIsNullFlagName(), commaOrNothing()));
      }
    }
  }

  @Value
  private static class ConstructorArgument {
    JavaIdentifier argumentName;
    String commaOrNothing;
  }
}
