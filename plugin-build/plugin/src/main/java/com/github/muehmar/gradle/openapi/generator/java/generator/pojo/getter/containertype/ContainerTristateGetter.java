package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.containertype;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberAndNameScope;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.AccessorProfile.Visibility;
import com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;

public class ContainerTristateGetter {
  private ContainerTristateGetter() {}

  public static Generator<MemberAndNameScope, PojoSettings> containerTristateGetterGenerator(
      Visibility visibility) {
    return Generator.<MemberAndNameScope, PojoSettings>emptyGen()
        .append(visibility.javaDocGenerator(), MemberAndNameScope::getMember)
        .append(method(visibility));
  }

  private static Generator<MemberAndNameScope, PojoSettings> method(Visibility visibility) {
    return JavaGenerators.<MemberAndNameScope, PojoSettings>methodGen()
        .modifiers(visibility.getModifiers())
        .noGenericTypes()
        .returnType(
            mas -> String.format("Tristate<%s>", ContainerRendering.returnType(mas.getMember())))
        .methodName((mas, s) -> mas.getMember().getGetterNameWithSuffix(s))
        .noArguments()
        .doesNotThrow()
        .content(
            (mas, s, w) ->
                w.append(
                    ContainerRendering.returnMappedContainer(
                        mas.getMember(), mas.getFlagFieldNameScope())))
        .build()
        .append(w -> w.ref(OpenApiUtilRefs.TRISTATE));
  }
}
