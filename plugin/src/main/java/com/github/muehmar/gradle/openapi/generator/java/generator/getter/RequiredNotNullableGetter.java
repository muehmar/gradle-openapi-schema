package com.github.muehmar.gradle.openapi.generator.java.generator.getter;

import static com.github.muehmar.gradle.openapi.generator.java.GeneratorUtil.noSettingsGen;
import static com.github.muehmar.gradle.openapi.generator.java.generator.JavaDocGenerator.javaDoc;
import static com.github.muehmar.gradle.openapi.generator.java.generator.ValidationGenerator.validationAnnotations;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.Resolver;
import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import com.github.muehmar.gradle.openapi.generator.java.JavaResolver;
import com.github.muehmar.gradle.openapi.generator.java.generator.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;

public class RequiredNotNullableGetter {
  private static final Resolver RESOLVER = new JavaResolver();

  private RequiredNotNullableGetter() {}

  public static Generator<PojoMember, PojoSettings> getter() {
    return Generator.<PojoMember, PojoSettings>emptyGen()
        .append(noSettingsGen(javaDoc()), PojoMember::getDescription)
        .append(validationAnnotations())
        .append(getterMethod());
  }

  public static Generator<PojoMember, PojoSettings> getterMethod() {
    return JavaGenerators.<PojoMember, PojoSettings>methodGen()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(f -> f.getTypeName(RESOLVER).asString())
        .methodName((f, settings) -> f.getterName(RESOLVER) + settings.suffixForField(f))
        .noArguments()
        .content(f -> String.format("return %s;", f.memberName(RESOLVER)))
        .build()
        .append(RefsGenerator.fieldRefs());
  }
}
