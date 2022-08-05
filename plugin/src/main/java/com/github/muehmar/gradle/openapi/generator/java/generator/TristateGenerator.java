package com.github.muehmar.gradle.openapi.generator.java.generator;

import static com.github.muehmar.gradle.openapi.generator.java.generator.AnnotationGenerator.override;
import static com.github.muehmar.gradle.openapi.generator.java.generator.JavaDocGenerator.javaDoc;
import static com.github.muehmar.gradle.openapi.generator.java.generator.JavaDocGenerator.ofJavaDocString;
import static io.github.muehmar.codegenerator.java.JavaModifier.FINAL;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.OpenApiUtilRefs;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.writer.Writer;

public class TristateGenerator {
  private static final String ON_VALUE_JAVA_DOC =
      "Registers a {@link java.util.function.Function} which is applied on the value of the property if it was present and non-null.";
  private static final String ON_NULL_JAVA_DOC =
      "Registers a {@link java.util.function.Supplier} which is called in case the property was null.";
  private static final String ON_ABSENT_JAVA_DOC =
      "Registers a {@link java.util.function.Supplier} which is called in case the property was absent.";
  private static final String MAP_METHOD_JAVA_DOC =
      "Returns a Tristate class whose value is mapped with the given function.";

  private TristateGenerator() {}

  public static <A, B> Generator<A, B> tristateClass() {
    return JavaGenerators.<A, B>classGen()
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
        .append(ofJavaDocString(MAP_METHOD_JAVA_DOC))
        .append(mapMethod())
        .appendNewLine()
        .append(ofNullableAndNullFlagFactoryMethod())
        .appendNewLine()
        .append(ofValueFactoryMethod())
        .appendNewLine()
        .append(ofAbsentFactoryMethod())
        .appendNewLine()
        .append(ofNullFactoryMethod())
        .appendNewLine()
        .append(ofJavaDocString(ON_VALUE_JAVA_DOC))
        .append(onValueMethod())
        .appendNewLine()
        .append(override())
        .append(equalsMethod())
        .appendNewLine()
        .append(override())
        .append(hashCodeMethod())
        .appendNewLine()
        .append(override())
        .append(toStringMethod())
        .appendNewLine()
        .append(onNullInterface())
        .appendNewLine()
        .append(onAbsentInterface());
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

  private static <A, B> Generator<A, B> mapMethod() {
    return (a, b, writer) ->
        writer
            .println("public <R> Tristate<R> map(Function<T, R> f) {")
            .tab(1)
            .println("return new Tristate<>(value.map(f), isNull);")
            .println("}");
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

  private static <B, A> Generator<A, B> ofValueFactoryMethod() {
    return (a, b, writer) ->
        writer
            .println("public static <T> Tristate<T> ofValue(T value) {")
            .tab(1)
            .println("return new Tristate<>(Optional.of(value), false);")
            .println("}");
  }

  private static <B, A> Generator<A, B> ofNullFactoryMethod() {
    return (a, b, writer) ->
        writer
            .println("public static <T> Tristate<T> ofNull() {")
            .tab(1)
            .println("return new Tristate<>(Optional.empty(), true);")
            .println("}");
  }

  private static <B, A> Generator<A, B> ofAbsentFactoryMethod() {
    return (a, b, writer) ->
        writer
            .println("public static <T> Tristate<T> ofAbsent() {")
            .tab(1)
            .println("return new Tristate<>(Optional.empty(), false);")
            .println("}");
  }

  private static <B, A> Generator<A, B> onValueMethod() {
    return (a, b, writer) ->
        writer
            .println("public <R> OnNull<R> onValue(Function<T, R> onValue) {")
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

  private static <B, A> Generator<A, B> onNullInterface() {
    return (a, b, writer) ->
        writer
            .println("public interface OnNull<R> {")
            .append(1, javaDoc().generate(ON_NULL_JAVA_DOC, (Void) null, Writer.createDefault()))
            .tab(1)
            .println("OnAbsent<R> onNull(Supplier<R> onNull);")
            .println("}")
            .ref(JavaRefs.JAVA_UTIL_SUPPLIER);
  }

  private static <B, A> Generator<A, B> onAbsentInterface() {
    return (a, b, writer) ->
        writer
            .println("public interface OnAbsent<R> {")
            .append(1, javaDoc().generate(ON_ABSENT_JAVA_DOC, (Void) null, Writer.createDefault()))
            .tab(1)
            .println("R onAbsent(Supplier<R> onAbsent);")
            .println("}")
            .ref(JavaRefs.JAVA_UTIL_SUPPLIER);
  }
}
