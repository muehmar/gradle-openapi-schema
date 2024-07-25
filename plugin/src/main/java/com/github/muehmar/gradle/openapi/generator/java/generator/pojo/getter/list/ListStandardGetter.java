package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.list;

import static com.github.muehmar.gradle.openapi.generator.java.GeneratorUtil.noSettingsGen;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.CommonGetter.getterName;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.listmapping.MemberMapWriter.fullAutoMemberMapWriter;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static io.github.muehmar.codegenerator.java.JavaDocGenerator.javaDoc;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSettings;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;

public class ListStandardGetter {
  private ListStandardGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> listStandardGetterGenerator(
      GetterGeneratorSettings generatorSettings) {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(noSettingsGen(javaDoc()), JavaPojoMember::getDescription)
        .append(jsonIgnore())
        .append(method());
  }

  private static Generator<JavaPojoMember, PojoSettings> method() {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(
            member ->
                member
                    .getJavaType()
                    .getParameterizedClassName()
                    .asStringWrappingNullableValueType())
        .methodName(getterName())
        .noArguments()
        .doesNotThrow()
        .content((member, s, w) -> w.append(fullAutoMemberMapWriter(member, "return ")))
        .build();
  }
}
