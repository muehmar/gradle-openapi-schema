package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAllOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import java.util.function.Function;
import lombok.Value;

public class ConversionMethodGenerator {
  private ConversionMethodGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> conversionMethodGenerator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(asSingleDtoMethod(), ConversionMethodGenerator::getComposedPojos, newLine());
  }

  private static Iterable<JavaObjectPojo> getComposedPojos(JavaObjectPojo pojo) {
    return PList.of(pojo.getAllOfComposition().map(JavaAllOfComposition::getPojos))
        .add(pojo.getOneOfComposition().map(JavaOneOfComposition::getPojos))
        .add(pojo.getAnyOfComposition().map(JavaAnyOfComposition::getPojos))
        .flatMapOptional(Function.identity())
        .flatMap(NonEmptyList::toPList);
  }

  private static Generator<JavaObjectPojo, PojoSettings> asSingleDtoMethod() {
    return MethodGenBuilder.<JavaObjectPojo, PojoSettings>create()
        .modifiers(PRIVATE)
        .noGenericTypes()
        .returnType(pojo -> pojo.getClassName().asString())
        .methodName(pojo -> CompositionNames.asConversionMethodName(pojo).asString())
        .noArguments()
        .content(asDtoMethodContent())
        .build();
  }

  private static Generator<JavaObjectPojo, PojoSettings> asDtoMethodContent() {
    final Generator<JavaIdentifier, PojoSettings> memberGen =
        (identifier, s, w) -> w.println("%s,", identifier);
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append((p, s, w) -> w.println("return new %s(", p.getClassName()))
        .appendList(
            memberGen.indent(1),
            pojo ->
                pojo.getMembers()
                    .map(m -> new PojoAndMember(pojo, m))
                    .flatMap(PojoAndMember::getFieldNameIdentifiers))
        .append(constant(JavaAdditionalProperties.getPropertyName().asString()), 1)
        .append(w -> w.println(");"));
  }

  @Value
  private static class PojoAndMember {
    JavaObjectPojo pojo;
    JavaPojoMember member;

    private PList<JavaIdentifier> getFieldNameIdentifiers() {
      if (member.isRequiredAndNotNullable() || member.isOptionalAndNotNullable()) {
        return PList.single(member.getNameAsIdentifier());
      } else if (member.isRequiredAndNullable()) {
        return PList.of(member.getNameAsIdentifier(), member.getIsPresentFlagName());
      } else {
        return PList.of(member.getNameAsIdentifier(), member.getIsNullFlagName());
      }
    }
  }
}
