package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ToApiTypeConversionRenderer.toApiTypeConversion;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIgnore;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.AccessorProfile.Visibility;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ConversionGenerationMode;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.writer.Writer;

public class StandardGetter {
  private StandardGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> standardGetterGenerator(
      Visibility visibility) {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(visibility.javaDocGenerator())
        .append(jsonIgnore())
        .append(getterMethod(visibility));
  }

  private static Generator<JavaPojoMember, PojoSettings> getterMethod(Visibility visibility) {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(visibility.getModifiers())
        .noGenericTypes()
        .returnType(member -> member.getJavaType().getWriteableParameterizedClassName().asString())
        .methodName(JavaPojoMember::getGetterNameWithSuffix)
        .noArguments()
        .doesNotThrow()
        .content(getterContent())
        .build()
        .append(RefsGenerator.fieldRefs());
  }

  private static Generator<JavaPojoMember, PojoSettings> getterContent() {
    return (member, settings, writer) -> {
      final String value =
          member
              .getJavaType()
              .getApiType()
              .map(
                  apiType ->
                      toApiTypeConversion(
                          apiType, member.getName().asString(), ConversionGenerationMode.NULL_SAFE))
              .map(Writer::asString)
              .orElse(member.getName().asString());
      return writer.println("return %s;", value);
    };
  }
}
