package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.setter.container;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.list.ListAssigmentWriterBuilder.fullListAssigmentWriterBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.map.MapAssignmentWriterBuilder.fullMapAssignmentWriterBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_OPTIONAL;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaDocGenerator.javaDoc;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.setter.FlagAssignments;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.setter.SetterModifier;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.writer.Writer;

public class ContainerNullableValueOptionalSetter {
  private ContainerNullableValueOptionalSetter() {}

  public static Generator<JavaPojoMember, PojoSettings>
      containerNullableValueOptionalSetterGenerator() {
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
        .methodName((m, s) -> m.prefixedMethodName(s.getBuilderMethodPrefix()).append("_"))
        .singleArgument(
            member -> new MethodGen.Argument(argumentType(member), member.getName().asString()))
        .doesNotThrow()
        .content(methodContent())
        .build()
        .append(RefsGenerator.fieldRefs())
        .append(ref(JAVA_UTIL_OPTIONAL));
  }

  private static String argumentType(JavaPojoMember member) {
    return String.format(
        "Optional<%s>",
        member
            .getJavaType()
            .getWriteableParameterizedClassName()
            .asStringWrappingNullableValueType());
  }

  private static Generator<JavaPojoMember, PojoSettings> methodContent() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append((m, s, w) -> w.append(methodWriter(m)))
        .append(FlagAssignments.forWrappedMemberSetter())
        .append(constant("return this;"));
  }

  private static Writer methodWriter(JavaPojoMember member) {
    if (member.getJavaType().isArrayType()) {
      return fullListAssigmentWriterBuilder()
          .member(member)
          .fieldAssigment()
          .unwrapOptionalList()
          .autoUnmapListType(member)
          .unwrapOptionalListItem()
          .autoUnmapListItemType(member)
          .build();
    } else {
      return fullMapAssignmentWriterBuilder()
          .member(member)
          .fieldAssigment()
          .unwrapOptionalMap()
          .autoUnmapMapType(member)
          .unwrapOptionalMapItem()
          .autoUnmapMapItemType(member)
          .build();
    }
  }
}
