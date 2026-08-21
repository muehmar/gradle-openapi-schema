package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.wither;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.map.MapAssignmentWriterBuilder.fullMapAssignmentWriterBuilder;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.map.MapAssignmentWriter;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.WriteableParameterizedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaMapType;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

class MapWitherMethod extends WitherMethod {
  private final JavaPojoMember pojoMember;
  private final JavaMapType javaMapType;
  private final WitherMapType witherMapType;

  public MapWitherMethod(
      WitherGenerator.WitherContent witherContent,
      JavaPojoMember pojoMember,
      JavaMapType javaMapType,
      WitherMapType witherMapType) {
    super(witherContent, pojoMember);
    this.pojoMember = pojoMember;
    this.javaMapType = javaMapType;
    this.witherMapType = witherMapType;
  }

  public static PList<WitherMethod> fromContentAndMember(
      WitherGenerator.WitherContent witherContent, JavaPojoMember member) {
    return member
        .getJavaType()
        .onMapType()
        .map(
            javaMapType ->
                PList.of(
                    new MapWitherMethod(witherContent, member, javaMapType, WitherMapType.STANDARD),
                    new MapWitherMethod(
                        witherContent, member, javaMapType, WitherMapType.OVERLOADED)))
        .orElse(PList.empty())
        .map(m -> m);
  }

  @Override
  boolean shouldBeUsed() {
    if (pojoMember.isRequiredAndNotNullable() && witherMapType == WitherMapType.OVERLOADED) {
      return false;
    }
    return true;
  }

  @Override
  String argumentType(WriteableParameterizedClassName parameterizedClassName) {
    final Function<WriteableParameterizedClassName, String> renderClassName =
        WriteableParameterizedClassName::asString;

    if (witherMapType == WitherMapType.STANDARD) {
      return renderClassName.apply(parameterizedClassName);
    } else {
      final String formatString;
      if (pojoMember.isRequiredAndNullable() || pojoMember.isOptionalAndNotNullable()) {
        formatString = "Optional<%s>";
      } else {
        formatString = "Tristate<%s>";
      }
      return String.format(formatString, renderClassName.apply(parameterizedClassName));
    }
  }

  @Override
  boolean isOverloadedWither() {
    return witherMapType.equals(WitherMapType.OVERLOADED);
  }

  @Override
  Map<JavaName, StringOrWriter> propertyNameReplacementForConstructorCall() {
    final Writer assignmentWriter =
        fullMapAssignmentWriterBuilder()
            .member(pojoMember)
            .expressionOnly()
            .unwrapMap(witherMapType.unwrapMapFunction(pojoMember))
            .unmapMapType(javaMapType)
            .unwrapMapItemNotNecessary()
            .unmapMapItemType(javaMapType)
            .build();
    final HashMap<JavaName, StringOrWriter> map = new HashMap<>();
    map.put(pojoMember.getName(), StringOrWriter.ofWriter(assignmentWriter));
    return map;
  }

  @Override
  Writer addRefs(Writer writer) {
    final boolean optionalRef =
        (pojoMember.isRequiredAndNullable() || pojoMember.isOptionalAndNotNullable())
            && witherMapType == WitherMapType.OVERLOADED;

    final boolean tristateMap =
        pojoMember.isOptionalAndNullable() && witherMapType == WitherMapType.OVERLOADED;

    return writer
        .refs(optionalRef ? singletonList(JavaRefs.JAVA_UTIL_OPTIONAL) : emptyList())
        .refs(tristateMap ? singletonList(OpenApiUtilRefs.TRISTATE) : emptyList());
  }

  @Override
  String witherName() {
    return super.witherName();
  }

  public enum WitherMapType {
    STANDARD {
      @Override
      MapAssignmentWriter.UnwrapMapFunction unwrapMapFunction(JavaPojoMember member) {
        return MapAssignmentWriter.UnwrapMapFunction.IDENTITY;
      }
    },
    OVERLOADED {
      @Override
      MapAssignmentWriter.UnwrapMapFunction unwrapMapFunction(JavaPojoMember member) {
        if (member.isRequiredAndNullable() || member.isOptionalAndNotNullable()) {
          return MapAssignmentWriter.UnwrapMapFunction.UNWRAP_OPTIONAL;
        } else {
          return MapAssignmentWriter.UnwrapMapFunction.UNWRAP_TRISTATE;
        }
      }
    };

    abstract MapAssignmentWriter.UnwrapMapFunction unwrapMapFunction(JavaPojoMember member);
  }
}
