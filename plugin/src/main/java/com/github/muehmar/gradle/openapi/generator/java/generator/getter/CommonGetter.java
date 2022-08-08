package com.github.muehmar.gradle.openapi.generator.java.generator.getter;

import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.Resolver;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.JavaResolver;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import java.util.function.BiFunction;

public class CommonGetter {
  private static final Resolver RESOLVER = new JavaResolver();

  private CommonGetter() {}

  public static Generator<PojoMember, PojoSettings> wrapNullableInOptionalGetterMethod() {
    return JavaGenerators.<PojoMember, PojoSettings>methodGen()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(f -> String.format("Optional<%s>", f.getTypeName(RESOLVER).asString()))
        .methodName(getterName())
        .noArguments()
        .content(f -> String.format("return Optional.ofNullable(%s);", f.memberName(RESOLVER)))
        .build()
        .append(w -> w.ref(JavaRefs.JAVA_UTIL_OPTIONAL));
  }

  public static Generator<PojoMember, PojoSettings> wrapNullableInOptionalGetterOrMethod() {
    return JavaGenerators.<PojoMember, PojoSettings>methodGen()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(f -> f.getTypeName(RESOLVER).asString())
        .methodName(f -> String.format("%sOr", f.getterName(RESOLVER)))
        .singleArgument(f -> String.format("%s defaultValue", f.getTypeName(RESOLVER).asString()))
        .content(
            f ->
                String.format(
                    "return %s == null ? defaultValue : %s;",
                    f.memberName(RESOLVER), f.memberName(RESOLVER)))
        .build()
        .append(w -> w.ref(JavaRefs.JAVA_UTIL_OPTIONAL));
  }

  public static BiFunction<PojoMember, PojoSettings, String> getterName() {
    return (field, settings) -> field.getterName(RESOLVER) + settings.suffixForField(field);
  }

  public static Generator<PojoMember, PojoSettings> rawGetterMethod() {
    return JavaGenerators.<PojoMember, PojoSettings>methodGen()
        .modifiers((f, s) -> s.getRawGetter().getModifier().asJavaModifiers())
        .noGenericTypes()
        .returnType(f -> f.getTypeName(RESOLVER).asString())
        .methodName((f, s) -> f.getterName(RESOLVER) + s.getRawGetter().getSuffix())
        .noArguments()
        .content(f -> String.format("return %s;", f.memberName(RESOLVER)))
        .build();
  }
}
