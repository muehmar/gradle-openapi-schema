package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.containertype;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.AccessorProfile.Visibility;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;

public class ContainerTristateGetter {
  private ContainerTristateGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> containerTristateGetterGenerator(
      Visibility visibility) {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(visibility.javaDocGenerator())
        .append(method(visibility));
  }

  private static Generator<JavaPojoMember, PojoSettings> method(Visibility visibility) {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(visibility.getModifiers())
        .noGenericTypes()
        .returnType(m -> String.format("Tristate<%s>", ContainerRendering.returnType(m)))
        .methodName(JavaPojoMember::getGetterNameWithSuffix)
        .noArguments()
        .doesNotThrow()
        .content((member, s, w) -> w.append(ContainerRendering.returnMappedContainer(member)))
        .build()
        .append(w -> w.ref(OpenApiUtilRefs.TRISTATE));
  }
}
