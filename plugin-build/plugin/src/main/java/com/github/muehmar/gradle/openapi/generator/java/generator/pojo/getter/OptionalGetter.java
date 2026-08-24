package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ConversionGenerationMode.NO_NULL_CHECK;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ToApiTypeConversionRenderer.toApiTypeConversion;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.AccessorProfile.Visibility;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;

public class OptionalGetter {
  private OptionalGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> optionalGetterGenerator(
      Visibility visibility) {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(visibility.javaDocGenerator())
        .append(getterMethod(visibility));
  }

  private static Generator<JavaPojoMember, PojoSettings> getterMethod(Visibility visibility) {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(visibility.getModifiers())
        .noGenericTypes()
        .returnType(member -> String.format("Optional<%s>", ReturnType.fromPojoMember(member)))
        .methodName(JavaPojoMember::getGetterNameWithSuffix)
        .noArguments()
        .doesNotThrow()
        .content(
            f -> String.format("return Optional.ofNullable(%s)%s;", f.getName(), apiMapping(f)))
        .build()
        .append(w -> w.ref(JavaRefs.JAVA_UTIL_OPTIONAL));
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
