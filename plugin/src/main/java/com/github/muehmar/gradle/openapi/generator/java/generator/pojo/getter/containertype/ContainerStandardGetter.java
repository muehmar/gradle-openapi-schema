package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.containertype;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.listmapping.ListMemberMappingWriter.fullAutoListMemberMappingWriter;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.mapmapping.MapMemberMappingWriter.fullAutoMapMemberMappingWriter;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSettings;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.writer.Writer;

public class ContainerStandardGetter {
  private ContainerStandardGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> containerStandardGetterGenerator(
      GetterGeneratorSettings generatorSettings) {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(generatorSettings.javaDocGenerator())
        .append(jsonIgnore())
        .append(method(generatorSettings));
  }

  private static Generator<JavaPojoMember, PojoSettings> method(
      GetterGeneratorSettings generatorSettings) {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(generatorSettings.modifiersWithDefault(PUBLIC))
        .noGenericTypes()
        .returnType(ContainerStandardGetter::getListReturnType)
        .methodName(JavaPojoMember::getGetterNameWithSuffix)
        .noArguments()
        .doesNotThrow()
        .content((member, s, w) -> w.append(methodWriter(member)))
        .build();
  }

  private static Object getListReturnType(JavaPojoMember member) {
    if (member.getJavaType().isArrayType()) {
      return ListReturnType.fromPojoMember(member);
    } else {
      return MapReturnType.fromPojoMember(member);
    }
  }

  private static Writer methodWriter(JavaPojoMember member) {
    if (member.getJavaType().isArrayType()) {
      return fullAutoListMemberMappingWriter(member, "return ");
    } else {
      return fullAutoMapMemberMappingWriter(member, "return ");
    }
  }
}
