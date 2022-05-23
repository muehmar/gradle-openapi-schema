package com.github.muehmar.gradle.openapi.generator.java.generator;

import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.FINAL;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.OpenApiUtilRefs;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.impl.gen.ClassGenBuilder;

public class TristateGenerator {
  private TristateGenerator() {}

  public static <A, B> Generator<A, B> tristateClass() {
    return ClassGenBuilder.<A, B>create()
        .clazz()
        .topLevel()
        .packageGen(
            (a, b, writer) -> writer.println("package %s;", OpenApiUtilRefs.OPENAPI_UTIL_PACKAGE))
        .modifiers(PUBLIC, FINAL)
        .className("Tristate<T>")
        .noSuperClass()
        .noInterfaces()
        .content(tristateClassContent())
        .build();
  }

  private static <B, A> Generator<A, B> tristateClassContent() {
    return Generator.<A, B>emptyGen()
        .append(fields())
        .appendNewLine()
        .append(constructor())
        .appendNewLine()
        .append(ofNullableAndNullFlagFactoryMethod())
        .appendNewLine()
        .append(onValueMethod())
        .appendNewLine()
        .append(AnnotationGenerator.override())
        .append(equalsMethod())
        .appendNewLine()
        .append(AnnotationGenerator.override())
        .append(hashCodeMethod())
        .appendNewLine()
        .append(AnnotationGenerator.override())
        .append(toStringMethod())
        .appendNewLine()
        .append(onValueInterface())
        .appendNewLine()
        .append(onNullInterface());
  }

  private static <B, A> Generator<A, B> fields() {
    return (a, b, writer) ->
        writer
            .println("private final Optional<T> value;")
            .println("private final boolean isNull;")
            .ref(JavaRefs.JAVA_UTIL_OPTIONAL);
  }

  private static <B, A> Generator<A, B> constructor() {
    return (a, b, writer) ->
        writer
            .println("private Tristate(Optional<T> value, boolean isNull) {")
            .tab(1)
            .println("this.value = value;")
            .tab(1)
            .println("this.isNull = isNull;")
            .println("}")
            .ref(JavaRefs.JAVA_UTIL_OPTIONAL);
  }

  private static <B, A> Generator<A, B> ofNullableAndNullFlagFactoryMethod() {
    return (a, b, writer) ->
        writer
            .println(
                "public static <T> Tristate<T> ofNullableAndNullFlag(T nullableValue, boolean isNull) {")
            .tab(1)
            .println("return new Tristate<>(Optional.ofNullable(nullableValue), isNull);")
            .println("}");
  }

  private static <B, A> Generator<A, B> onValueMethod() {
    return (a, b, writer) ->
        writer
            .println("public <R> OnValue<R> onValue(Function<T, R> onValue) {")
            .tab(1)
            .println("return onNull ->")
            .tab(2)
            .println(
                "onAbsent -> value.map(onValue).orElseGet(() -> isNull ? onNull.get() : onAbsent.get());")
            .println("}")
            .ref(JavaRefs.JAVA_UTIL_FUNCTION);
  }

  private static <B, A> Generator<A, B> equalsMethod() {
    return (a, b, writer) ->
        writer
            .println("public boolean equals(Object o) {")
            .tab(1)
            .println("if (this == o) return true;")
            .tab(1)
            .println("if (o == null || getClass() != o.getClass()) return false;")
            .tab(1)
            .println("Tristate<?> tristate = (Tristate<?>) o;")
            .tab(1)
            .println("return isNull == tristate.isNull && Objects.equals(value, tristate.value);")
            .println("}")
            .ref(JavaRefs.JAVA_UTIL_OBJECTS);
  }

  private static <B, A> Generator<A, B> hashCodeMethod() {
    return (a, b, writer) ->
        writer
            .println("public int hashCode() {")
            .tab(1)
            .println("return Objects.hash(value, isNull);")
            .println("}")
            .ref(JavaRefs.JAVA_UTIL_OBJECTS);
  }

  private static <B, A> Generator<A, B> toStringMethod() {
    return (a, b, writer) ->
        writer
            .println("public String toString() {")
            .tab(1)
            .println("return \"Tristate{\" + \"value=\" + value + \", isNull=\" + isNull + '}';")
            .println("}");
  }

  private static <B, A> Generator<A, B> onValueInterface() {
    return (a, b, writer) ->
        writer
            .println("public interface OnValue<R> {")
            .tab(1)
            .println("OnNull<R> onNull(Supplier<R> onNull);")
            .println("}")
            .ref(JavaRefs.JAVA_UTIL_SUPPLIER);
  }

  private static <B, A> Generator<A, B> onNullInterface() {
    return (a, b, writer) ->
        writer
            .println("public interface OnNull<R> {")
            .tab(1)
            .println("R onAbsent(Supplier<R> onAbsent);")
            .println("}")
            .ref(JavaRefs.JAVA_UTIL_SUPPLIER);
  }
}
