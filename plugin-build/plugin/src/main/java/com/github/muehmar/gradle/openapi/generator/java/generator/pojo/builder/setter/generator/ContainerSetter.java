package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.setter.generator;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.list.ListAssigmentWriterBuilder.fullListAssigmentWriterBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.map.MapAssignmentWriterBuilder.fullMapAssignmentWriterBuilder;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaDocGenerator.javaDoc;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberAndNameScope;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.definition.SetterGeneratorSetting;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.definition.SetterGeneratorSettings;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.setter.SetterModifier;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.list.ListAssigmentWriter;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.map.MapAssignmentWriter;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.WriteableParameterizedClassName;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.function.Function;

public class ContainerSetter {
  private ContainerSetter() {}

  public static Generator<MemberAndNameScope, PojoSettings> containerSetterGenerator(
      SetterGeneratorSetting... settings) {
    return containerSetterGenerator(new SetterGeneratorSettings(PList.of(settings)));
  }

  public static Generator<MemberAndNameScope, PojoSettings> containerSetterGenerator(
      SetterGeneratorSettings settings) {
    return Generator.<MemberAndNameScope, PojoSettings>emptyGen()
        .append(javaDoc(), mas -> mas.getMember().getDescription())
        .append(setterMethod(settings));
  }

  private static Generator<MemberAndNameScope, PojoSettings> setterMethod(
      SetterGeneratorSettings settings) {
    return JavaGenerators.<MemberAndNameScope, PojoSettings>methodGen()
        .modifiers(SetterModifier.scopedModifiers())
        .noGenericTypes()
        .returnType("Builder")
        .methodName((mas, s) -> methodName(mas.getMember(), s, settings))
        .singleArgument(
            mas ->
                new MethodGen.Argument(
                    argumentType(mas.getMember(), settings), mas.getMember().getName().asString()))
        .doesNotThrow()
        .content(methodContent(settings))
        .build()
        .append(RefsGenerator.fieldRefs(), MemberAndNameScope::getMember)
        .append(settings.wrappingRefs(), MemberAndNameScope::getMember);
  }

  private static JavaName methodName(
      JavaPojoMember m, PojoSettings pojoSettings, SetterGeneratorSettings settings) {
    final String suffix = settings.isNullableContainerValue() ? "_" : "";
    return m.prefixedMethodName(pojoSettings.getBuilderMethodPrefix()).append(suffix);
  }

  private static String argumentType(JavaPojoMember member, SetterGeneratorSettings settings) {
    final Function<WriteableParameterizedClassName, String> asStringFunction =
        settings.isNullableContainerValue()
            ? WriteableParameterizedClassName::asStringWrappingNullableValueType
            : WriteableParameterizedClassName::asString;
    final WriteableParameterizedClassName writeableParameterizedClassName =
        member.getJavaType().getWriteableParameterizedClassName();

    return String.format(
        settings.typeFormat(), asStringFunction.apply(writeableParameterizedClassName));
  }

  private static Generator<MemberAndNameScope, PojoSettings> methodContent(
      SetterGeneratorSettings settings) {
    return Generator.<MemberAndNameScope, PojoSettings>emptyGen()
        .append((mas, s, w) -> w.append(methodWriter(mas.getMember(), settings)))
        .append(settings.flagAssigment())
        .append(constant("return this;"));
  }

  private static Writer methodWriter(JavaPojoMember member, SetterGeneratorSettings settings) {
    if (member.getJavaType().isArrayType()) {
      final ListAssigmentWriter.UnwrapListFunction unwrapListFunction;
      if (settings.isTristateSetter()) {
        unwrapListFunction = ListAssigmentWriter.UnwrapListFunction.UNWRAP_TRISTATE;
      } else if (settings.isOptionalSetter()) {
        unwrapListFunction = ListAssigmentWriter.UnwrapListFunction.UNWRAP_OPTIONAL;
      } else {
        unwrapListFunction = ListAssigmentWriter.UnwrapListFunction.IDENTITY;
      }

      final ListAssigmentWriter.UnwrapListItemFunction unwrapListItemFunction =
          settings.isNullableContainerValue()
              ? ListAssigmentWriter.UnwrapListItemFunction.UNWRAP_OPTIONAL
              : ListAssigmentWriter.UnwrapListItemFunction.IDENTITY;

      return fullListAssigmentWriterBuilder()
          .member(member)
          .fieldAssigment()
          .unwrapList(unwrapListFunction)
          .autoUnmapListType(member)
          .unwrapListItem(unwrapListItemFunction)
          .autoUnmapListItemType(member)
          .build();
    } else {
      final MapAssignmentWriter.UnwrapMapFunction unwrapMapFunction;
      if (settings.isTristateSetter()) {
        unwrapMapFunction = MapAssignmentWriter.UnwrapMapFunction.UNWRAP_TRISTATE;
      } else if (settings.isOptionalSetter()) {
        unwrapMapFunction = MapAssignmentWriter.UnwrapMapFunction.UNWRAP_OPTIONAL;
      } else {
        unwrapMapFunction = MapAssignmentWriter.UnwrapMapFunction.IDENTITY;
      }

      final MapAssignmentWriter.UnwrapMapItemFunction unwrapMapItemFunction =
          settings.isNullableContainerValue()
              ? MapAssignmentWriter.UnwrapMapItemFunction.UNWRAP_OPTIONAL
              : MapAssignmentWriter.UnwrapMapItemFunction.IDENTITY;

      return fullMapAssignmentWriterBuilder()
          .member(member)
          .fieldAssigment()
          .unwrapMap(unwrapMapFunction)
          .autoUnmapMapType(member)
          .unwrapMapItem(unwrapMapItemFunction)
          .autoUnmapMapItemType(member)
          .build();
    }
  }
}
