package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.containertype;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.AccessorProfile.Visibility;
import com.github.muehmar.gradle.openapi.generator.java.model.member.FlagFieldNameScope;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;

public class ContainerOptionalGetter {
  private ContainerOptionalGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> containerOptionalGetterGenerator(
      Visibility visibility) {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(visibility.javaDocGenerator())
        .append(method(visibility));
  }

  private static Generator<JavaPojoMember, PojoSettings> method(Visibility visibility) {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(visibility.getModifiers())
        .noGenericTypes()
        .returnType(m -> String.format("Optional<%s>", ContainerRendering.returnType(m)))
        .methodName(JavaPojoMember::getGetterNameWithSuffix)
        .noArguments()
        .doesNotThrow()
        .content(
            (member, s, w) ->
                // No companion flag is read here, hence the scope is unused.
                w.append(
                    ContainerRendering.returnMappedContainer(member, FlagFieldNameScope.empty())))
        .build()
        .append(w -> w.ref(JavaRefs.JAVA_UTIL_OPTIONAL));
  }
}
