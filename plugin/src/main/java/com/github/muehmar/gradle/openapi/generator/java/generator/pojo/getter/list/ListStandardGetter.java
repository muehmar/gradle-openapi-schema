package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.list;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.listmapping.MemberMapWriter.fullAutoMemberMapWriter;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSettings;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedApiClassName;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;

public class ListStandardGetter {
  private ListStandardGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> listStandardGetterGenerator(
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
        .returnType(ListStandardGetter::returnType)
        .methodName(JavaPojoMember::getGetterNameWithSuffix)
        .noArguments()
        .doesNotThrow()
        .content((member, s, w) -> w.append(fullAutoMemberMapWriter(member, "return ")))
        .build();
  }

  private static String returnType(JavaPojoMember member) {
    return ParameterizedApiClassName.fromJavaType(member.getJavaType())
        .map(ParameterizedApiClassName::asStringWrappingNullableValueType)
        .orElse(
            member.getJavaType().getParameterizedClassName().asStringWrappingNullableValueType());
  }
}
