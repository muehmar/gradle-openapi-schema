package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ConversionGenerationMode.NO_NULL_CHECK;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ToApiTypeConversionRenderer.toApiTypeConversion;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSettings;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;

public class OptionalGetter {
  private OptionalGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> optionalGetterGenerator(
      GetterGeneratorSettings generatorSettings) {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(generatorSettings.javaDocGenerator())
        .append(jsonIgnore())
        .append(getterMethod(generatorSettings));
  }

  private static Generator<JavaPojoMember, PojoSettings> getterMethod(
      GetterGeneratorSettings generatorSettings) {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(generatorSettings.modifiersWithDefault(PUBLIC))
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
