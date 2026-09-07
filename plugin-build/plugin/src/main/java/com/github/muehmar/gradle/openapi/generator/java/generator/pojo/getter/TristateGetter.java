package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ConversionGenerationMode.NO_NULL_CHECK;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ToApiTypeConversionRenderer.toApiTypeConversion;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberAndNameScope;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.AccessorProfile.Visibility;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;

public class TristateGetter {
  private TristateGetter() {}

  public static Generator<MemberAndNameScope, PojoSettings> tristateGetterGenerator(
      Visibility visibility) {
    return Generator.<MemberAndNameScope, PojoSettings>emptyGen()
        .append(visibility.javaDocGenerator(), MemberAndNameScope::getMember)
        .append(getterMethod(visibility));
  }

  private static Generator<MemberAndNameScope, PojoSettings> getterMethod(Visibility visibility) {
    return JavaGenerators.<MemberAndNameScope, PojoSettings>methodGen()
        .modifiers(visibility.getModifiers())
        .noGenericTypes()
        .returnType(
            mas -> String.format("Tristate<%s>", ReturnType.fromPojoMember(mas.getMember())))
        .methodName((mas, s) -> mas.getMember().getGetterNameWithSuffix(s))
        .noArguments()
        .doesNotThrow()
        .content(
            mas ->
                String.format(
                    "return Tristate.ofNullableAndNullFlag(%s, %s)%s;",
                    mas.getMember().getName(),
                    mas.getIsNullFlagName(),
                    apiMapping(mas.getMember())))
        .build()
        .append(w -> w.ref(OpenApiUtilRefs.TRISTATE));
  }

  private static String apiMapping(JavaPojoMember member) {
    return member
        .getJavaType()
        .getApiType()
        .map(apiType -> toApiTypeConversion(apiType, "value", NO_NULL_CHECK))
        .map(writer -> String.format(".map(value -> %s)", writer.asString()))
        .orElse("");
  }
}
