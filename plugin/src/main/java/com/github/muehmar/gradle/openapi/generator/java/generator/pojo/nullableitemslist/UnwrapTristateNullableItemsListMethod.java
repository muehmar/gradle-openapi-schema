package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.nullableitemslist;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_LIST;
import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_OPTIONAL;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.java.JavaModifier.STATIC;

import com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

public class UnwrapTristateNullableItemsListMethod {
  public static final String METHOD_NAME = "unwrapTristateNullableItemsList";

  private UnwrapTristateNullableItemsListMethod() {}

  public static <A> Generator<A, PojoSettings> unwrapTristateNullableItemsListMethod() {
    return MethodGenBuilder.<A, PojoSettings>create()
        .modifiers(PRIVATE, STATIC)
        .singleGenericType(ignore -> "T")
        .returnType("Tristate<List<T>>")
        .methodName(METHOD_NAME)
        .singleArgument(ignore -> new MethodGen.Argument("Tristate<List<Optional<T>>>", "list"))
        .doesNotThrow()
        .content(
            constant("return list.map(l -> %s(l));", UnwrapNullableItemsListMethod.METHOD_NAME))
        .build()
        .append(ref(JAVA_UTIL_OPTIONAL))
        .append(ref(JAVA_UTIL_LIST))
        .append(ref(OpenApiUtilRefs.TRISTATE));
  }
}
