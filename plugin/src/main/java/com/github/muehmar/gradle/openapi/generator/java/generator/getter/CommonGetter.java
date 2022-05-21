package com.github.muehmar.gradle.openapi.generator.java.generator.getter;

import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PRIVATE;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.Resolver;
import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.JavaResolver;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGenBuilder;

public class CommonGetter {
  private static final Resolver RESOLVER = new JavaResolver();

  private CommonGetter() {}

  public static Generator<PojoMember, PojoSettings> wrapNullableInOptionalGetterMethod() {
    return MethodGenBuilder.<PojoMember, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(f -> String.format("Optional<%s>", f.getTypeName(RESOLVER).asString()))
        .methodName(f -> f.getterName(RESOLVER).asString())
        .noArguments()
        .content(f -> String.format("return Optional.ofNullable(%s);", f.memberName(RESOLVER)))
        .build()
        .append(w -> w.ref(JavaRefs.JAVA_UTIL_OPTIONAL));
  }

  public static Generator<PojoMember, PojoSettings> nullableGetterMethod() {
    return MethodGenBuilder.<PojoMember, PojoSettings>create()
        .modifiers(PRIVATE)
        .noGenericTypes()
        .returnType(f -> f.getTypeName(RESOLVER).asString())
        .methodName(f -> f.getterName(RESOLVER).asString() + "Nullable")
        .noArguments()
        .content(f -> String.format("return %s;", f.memberName(RESOLVER)))
        .build();
  }
}