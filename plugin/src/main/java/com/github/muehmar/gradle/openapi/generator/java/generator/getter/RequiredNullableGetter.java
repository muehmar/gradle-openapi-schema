package com.github.muehmar.gradle.openapi.generator.java.generator.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static com.github.muehmar.gradle.openapi.generator.java.generator.jackson.JacksonAnnotationGenerator.jsonProperty;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PRIVATE;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.Resolver;
import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.JavaResolver;
import com.github.muehmar.gradle.openapi.generator.java.generator.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGen;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import java.util.function.BiPredicate;

public class RequiredNullableGetter {

  private static final Resolver RESOLVER = new JavaResolver();

  private RequiredNullableGetter() {}

  public static Generator<PojoMember, PojoSettings> getter() {
    final BiPredicate<PojoMember, PojoSettings> isJacksonJson =
        (f, settings) -> settings.isJacksonJson();

    return Generator.<PojoMember, PojoSettings>emptyGen()
        .appendConditionally(isJacksonJson, jsonIgnore())
        .append(publicGetter())
        .appendConditionally(isJacksonJson, Generator.ofWriterFunction(Writer::println))
        .appendConditionally(isJacksonJson, jsonProperty())
        .appendConditionally(isJacksonJson, jacksonGetterMethod())
        .append(RefsGenerator.fieldRefs());
  }

  private static Generator<PojoMember, PojoSettings> publicGetter() {
    return MethodGen.<PojoMember, PojoSettings>modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(f -> String.format("Optional<%s>", f.getTypeName(RESOLVER).asString()))
        .methodName(f -> f.getterName(RESOLVER).asString())
        .noArguments()
        .content(f -> String.format("return Optional.ofNullable(%s);", f.memberName(RESOLVER)))
        .append(w -> w.ref(JavaRefs.JAVA_UTIL_OPTIONAL));
  }

  private static Generator<PojoMember, PojoSettings> jacksonGetterMethod() {
    return MethodGen.<PojoMember, PojoSettings>modifiers(PRIVATE)
        .noGenericTypes()
        .returnType(f -> f.getTypeName(RESOLVER).asString())
        .methodName(f -> f.getterName(RESOLVER).asString() + "Jackson")
        .noArguments()
        .content(f -> String.format("return %s;", f.memberName(RESOLVER)));
  }
}
