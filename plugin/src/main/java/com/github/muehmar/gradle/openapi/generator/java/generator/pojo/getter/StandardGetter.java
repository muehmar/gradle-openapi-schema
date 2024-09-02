package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaTypeGenerators.deepAnnotatedParameterizedClassName;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ToApiTypeConversion.toApiTypeConversion;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSettings;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ConversionGenerationMode;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.java.JavaModifier;
import io.github.muehmar.codegenerator.java.JavaModifiers;
import io.github.muehmar.codegenerator.writer.Writer;

public class StandardGetter {
  private StandardGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> standardGetterGenerator(
      GetterGeneratorSettings generatorSettings) {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(generatorSettings.javaDocGenerator())
        .append(generatorSettings.validationAnnotationGenerator())
        .append(generatorSettings.jsonIgnoreGenerator())
        .append(generatorSettings.jsonPropertyGenerator())
        .append(getterMethod(generatorSettings));
  }

  private static Generator<JavaPojoMember, PojoSettings> getterMethod(
      GetterGeneratorSettings generatorSettings) {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(modifiers(generatorSettings))
        .noGenericTypes()
        .returnType(getterMethodReturnType(generatorSettings))
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

  private static JavaModifiers modifiers(GetterGeneratorSettings generatorSettings) {
    if (generatorSettings.isPackagePrivate()) {
      return JavaModifiers.empty();
    } else {
      return JavaModifiers.of(JavaModifier.PUBLIC);
    }
  }

  private static Generator<JavaPojoMember, PojoSettings> getterMethodReturnType(
      GetterGeneratorSettings generatorSettings) {
    final Generator<JavaPojoMember, PojoSettings> deepAnnotatedReturnType =
        deepAnnotatedParameterizedClassName()
            .contraMap(ValidationAnnotationGenerator.PropertyType::fromMember)
            .filter(generatorSettings.validationFilter());

    final Generator<JavaPojoMember, PojoSettings> standardReturnType =
        Generator.<JavaPojoMember, PojoSettings>emptyGen()
            .append(
                (m, s, w) ->
                    w.println(
                        "%s", m.getJavaType().getWriteableParameterizedClassName().asString()))
            .filter(generatorSettings.<JavaPojoMember>validationFilter().negate());

    return deepAnnotatedReturnType.append(standardReturnType);
  }
}
