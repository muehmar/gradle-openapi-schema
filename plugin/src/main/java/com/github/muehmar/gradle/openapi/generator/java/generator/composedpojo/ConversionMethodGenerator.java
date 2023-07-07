package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaAnyType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import java.util.function.Function;
import lombok.Value;

public class ConversionMethodGenerator {
  private ConversionMethodGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> conversionMethodGenerator() {
    final Function<JavaObjectPojo, Iterable<JavaObjectPojo>> getComposePojoMembers =
        p ->
            p.getAnyOfComposition()
                .map(JavaAnyOfComposition::getPojos)
                .map(NonEmptyList::toPList)
                .orElseGet(PList::empty)
                .concat(
                    p.getOneOfComposition()
                        .map(JavaOneOfComposition::getPojos)
                        .map(NonEmptyList::toPList)
                        .orElseGet(PList::empty));
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(asSingleDtoMethod(), getComposePojoMembers, Generator.newLine());
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
        .append(additionalPropertiesArgument().indent(1), JavaObjectPojo::getAdditionalProperties)
        .append(
            additionalPropertiesArgumentWithCasting().indent(1),
            JavaObjectPojo::getAdditionalProperties)
        .append(w -> w.println(");"));
  }

  private static Generator<JavaAdditionalProperties, PojoSettings>
      additionalPropertiesArgumentWithCasting() {
    return Generator.<JavaAdditionalProperties, PojoSettings>emptyGen()
        .append(
            (props, s, w) ->
                w.println("%s.entrySet().stream()", JavaAdditionalProperties.getPropertyName()))
        .append(
            (props, s, w) ->
                w.println(
                    ".filter(e -> e.getValue() instanceof %s)", props.getType().getFullClassName()),
            1)
        .append(
            (props, s, w) ->
                w.println(
                    ".collect(Collectors.toMap(Map.Entry::getKey, e -> (%s)e.getValue()))",
                    props.getType().getFullClassName()),
            1)
        .filter(props -> not(props.getType().equals(JavaAnyType.create())))
        .append(RefsGenerator.ref(JavaRefs.JAVA_UTIL_MAP))
        .append(RefsGenerator.ref(JavaRefs.JAVA_UTIL_STREAM_COLLECTORS));
  }

  private static Generator<JavaAdditionalProperties, PojoSettings> additionalPropertiesArgument() {
    return Generator.<JavaAdditionalProperties, PojoSettings>emptyGen()
        .append((props, s, w) -> w.println("%s", JavaAdditionalProperties.getPropertyName()))
        .filter(props -> props.getType().equals(JavaAnyType.create()));
  }

  @Value
  private static class ComposedPojo {
    JavaObjectPojo composedPojo;
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
