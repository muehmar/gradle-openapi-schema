package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters.isJacksonJson;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIncludeNonNull;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonProperty;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSettings;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;

public class TristateJsonGetter {
  private TristateJsonGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> tristateJsonGetterGenerator(
      GetterGeneratorSettings generatorSettings) {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(jsonProperty())
        .append(jsonIncludeNonNull())
        .append(getterMethod())
        .filter(isJacksonJson());
  }

  private static Generator<JavaPojoMember, PojoSettings> getterMethod() {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(PRIVATE)
        .noGenericTypes()
        .returnType("Object")
        .methodName(f -> String.format("%sJackson", f.getGetterName()))
        .noArguments()
        .doesNotThrow()
        .content(
            f ->
                String.format(
                    "return %s ? new JacksonNullContainer<>(%s) : %s;",
                    f.getIsNullFlagName(), f.getName(), f.getName()))
        .build()
        .append(RefsGenerator.fieldRefs())
        .append(w -> w.ref(OpenApiUtilRefs.JACKSON_NULL_CONTAINER));
  }
}
