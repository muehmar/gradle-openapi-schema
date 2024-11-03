package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.list.ListAssigmentWriterBuilder.fullListAssigmentWriterBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.map.MapAssignmentWriterBuilder.fullMapAssignmentWriterBuilder;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaDocGenerator.javaDoc;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.FlagAssignments;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.writer.Writer;

public class ContainerStandardSetter {
  private ContainerStandardSetter() {}

  public static Generator<JavaPojoMember, PojoSettings> containerStandardSetterGenerator() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(javaDoc(), JavaPojoMember::getDescription)
        .append(jsonIgnore())
        .append(setterMethod());
  }

  private static Generator<JavaPojoMember, PojoSettings> setterMethod() {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(SetterModifier.modifiers())
        .noGenericTypes()
        .returnType("Builder")
        .methodName((m, s) -> m.prefixedMethodName(s.getBuilderMethodPrefix()))
        .singleArgument(
            member ->
                new MethodGen.Argument(
                    member.getJavaType().getWriteableParameterizedClassName().asString(),
                    member.getName().asString()))
        .doesNotThrow()
        .content(methodContent())
        .build()
        .append(RefsGenerator.fieldRefs());
  }

  private static Generator<JavaPojoMember, PojoSettings> methodContent() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append((m, s, w) -> w.append(methodWriter(m)))
        .append(FlagAssignments.forStandardMemberSetter())
        .append(constant("return this;"));
  }

  private static Writer methodWriter(JavaPojoMember member) {
    if (member.getJavaType().isArrayType()) {
      return fullListAssigmentWriterBuilder()
          .member(member)
          .fieldAssigment()
          .unwrapListNotNecessary()
          .autoUnmapListType(member)
          .unwrapListItemNotNecessary()
          .autoUnmapListItemType(member)
          .build();
    } else {
      return fullMapAssignmentWriterBuilder()
          .member(member)
          .fieldAssigment()
          .unwrapMapNotNecessary()
          .autoUnmapMapType(member)
          .unwrapMapItemNotNecessary()
          .autoUnmapMapItemType(member)
          .build();
    }
  }
}
