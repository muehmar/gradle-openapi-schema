package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaAnyType;
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
        .appendOptional(
            additionalPropertiesArgument().indent(1),
            pojo -> WrappedPojo.fromPojo(pojo).getAdditionalProperties())
        .appendOptional(
            additionalPropertiesArgumentWithCasting().indent(1),
            pojo -> WrappedPojo.fromPojo(pojo).getAdditionalProperties())
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
  private static class PojoAndMember {
    JavaPojo pojo;
    JavaPojoMember member;

    private String commaOrNothing() {
      return WrappedPojo.fromPojo(pojo).getAdditionalProperties().isPresent() ? "," : "";
    }

    private PList<ConstructorArgument> asConstructorArguments() {
      if (member.isRequiredAndNotNullable() || member.isOptionalAndNotNullable()) {
        return PList.single(
            new ConstructorArgument(member.getNameAsIdentifier(), commaOrNothing()));
      } else if (member.isRequiredAndNullable()) {
        return PList.of(
            new ConstructorArgument(member.getNameAsIdentifier(), ","),
            new ConstructorArgument(member.getIsPresentFlagName(), commaOrNothing()));
      } else {
        return PList.of(
            new ConstructorArgument(member.getNameAsIdentifier(), ","),
            new ConstructorArgument(member.getIsNullFlagName(), commaOrNothing()));
      }
    }
  }

  @Value
  private static class WrappedPojo {
    JavaPojo pojo;

    public static WrappedPojo fromPojo(JavaPojo pojo) {
      return new WrappedPojo(pojo);
    }

    Optional<JavaAdditionalProperties> getAdditionalProperties() {
      return pojo.fold(
          javaArrayPojo -> Optional.empty(),
          enumPojo -> Optional.empty(),
          objectPojo -> Optional.of(objectPojo.getAdditionalProperties()),
          composedPojo -> Optional.of(JavaAdditionalProperties.anyTypeAllowed()));
    }
  }

  @Value
  private static class ConstructorArgument {
    JavaIdentifier argumentName;
    String commaOrNothing;
  }
}
