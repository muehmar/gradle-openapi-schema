package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.list;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.listmapping.MemberMapWriterBuilder.fullMemberMapWriterBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;
import static io.github.muehmar.codegenerator.java.MethodGen.Argument.argument;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSettings;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.writer.Writer;

public class ListOptionalOrGetter {
  private ListOptionalOrGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> listOptionalOrGetterGenerator(
      GetterGeneratorSettings generatorSettings) {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(generatorSettings.javaDocGenerator())
        .append(jsonIgnore())
        .append(method());
  }

  private static Generator<JavaPojoMember, PojoSettings> method() {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(ListReturnType::fromPojoMember)
        .methodName(f -> String.format("%sOr", f.getGetterName()))
        .singleArgument(f -> argument(ListReturnType.fromPojoMember(f), "defaultValue"))
        .doesNotThrow()
        .content(methodContent())
        .build()
        .append(w -> w.ref(JavaRefs.JAVA_UTIL_OPTIONAL));
  }

  private static Generator<JavaPojoMember, PojoSettings> methodContent() {
    return (member, settings, writer) -> {
      final Writer memberMapWriter =
          fullMemberMapWriterBuilder()
              .member(member)
              .prefix(": ")
              .autoMapListItemType()
              .autoWrapListItem()
              .autoMapListType()
              .wrapListNotNecessary()
              .trailingSemicolon()
              .build();
      return writer
          .println("return this.%s == null", member.getName())
          .tab(2)
          .println("? defaultValue")
          .append(2, memberMapWriter);
    };
  }
}
