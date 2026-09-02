package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.containertype;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.listmapping.ListMemberMappingWriterBuilder.fullListMemberMappingWriterBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.mapmapping.MapMemberMappingWriterBuilder.fullMapMemberMappingWriterBuilder;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;
import static io.github.muehmar.codegenerator.java.MethodGen.Argument.argument;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.AccessorProfile.Visibility;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.writer.Writer;

public class ContainerOptionalOrGetter {
  private ContainerOptionalOrGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> containerOptionalOrGetterGenerator() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(Visibility.PUBLIC.javaDocGenerator())
        .append(method());
  }

  private static Generator<JavaPojoMember, PojoSettings> method() {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(ContainerRendering::returnType)
        .methodName(f -> String.format("%sOr", f.getGetterName()))
        .singleArgument(f -> argument(ContainerRendering.returnType(f), "defaultValue"))
        .doesNotThrow()
        .content(methodContent())
        .build()
        .append(w -> w.ref(JavaRefs.JAVA_UTIL_OPTIONAL));
  }

  private static Generator<JavaPojoMember, PojoSettings> methodContent() {
    return (member, settings, writer) -> {
      final Writer memberMapWriter = methodWriter(member);
      return writer
          .println("return this.%s == null", member.getName())
          .tab(2)
          .println("? defaultValue")
          .append(2, memberMapWriter);
    };
  }

  private static Writer methodWriter(JavaPojoMember member) {
    if (member.getJavaType().isArrayType()) {
      return fullListMemberMappingWriterBuilder()
          .member(member)
          .prefix(": ")
          .autoMapListItemType()
          .autoWrapListItem()
          .autoMapListType()
          .wrapListNotNecessary()
          .trailingSemicolon()
          .build();
    } else {
      return fullMapMemberMappingWriterBuilder()
          .member(member)
          .prefix(": ")
          .autoMapMapItemType()
          .autoWrapMapItem()
          .autoMapMapType()
          .wrapMapNotNecessary()
          .trailingSemicolon()
          .build();
    }
  }
}
