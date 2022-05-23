package com.github.muehmar.gradle.openapi.generator.java.generator;

import static com.github.muehmar.gradle.openapi.generator.java.generator.data.VoidData.noData;
import static com.github.muehmar.gradle.openapi.generator.java.generator.data.VoidData.noSettings;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class TristateGeneratorTest {
  @Test
  void tristateClass_when_called_then_correctOutput() {
    final Generator<Void, Void> gen = TristateGenerator.tristateClass();
    final Writer writer = gen.generate(noData(), noSettings(), Writer.createDefault());
    assertEquals(
        "package com.github.muehmar.openapi.util;\n"
            + "\n"
            + "import java.util.Objects;\n"
            + "import java.util.Optional;\n"
            + "import java.util.function.Function;\n"
            + "import java.util.function.Supplier;\n"
            + "\n"
            + "public final class Tristate<T> {\n"
            + "  private final Optional<T> value;\n"
            + "  private final boolean isNull;\n"
            + "\n"
            + "  private Tristate(Optional<T> value, boolean isNull) {\n"
            + "    this.value = value;\n"
            + "    this.isNull = isNull;\n"
            + "  }\n"
            + "\n"
            + "  public static <T> Tristate<T> ofNullableAndNullFlag(T nullableValue, boolean isNull) {\n"
            + "    return new Tristate<>(Optional.ofNullable(nullableValue), isNull);\n"
            + "  }\n"
            + "\n"
            + "  public <R> OnValue<R> onValue(Function<T, R> onValue) {\n"
            + "    return onNull ->\n"
            + "      onAbsent -> value.map(onValue).orElseGet(() -> isNull ? onNull.get() : onAbsent.get());\n"
            + "  }\n"
            + "\n"
            + "  @Override\n"
            + "  public boolean equals(Object o) {\n"
            + "    if (this == o) return true;\n"
            + "    if (o == null || getClass() != o.getClass()) return false;\n"
            + "    Tristate<?> tristate = (Tristate<?>) o;\n"
            + "    return isNull == tristate.isNull && Objects.equals(value, tristate.value);\n"
            + "  }\n"
            + "\n"
            + "  @Override\n"
            + "  public int hashCode() {\n"
            + "    return Objects.hash(value, isNull);\n"
            + "  }\n"
            + "\n"
            + "  @Override\n"
            + "  public String toString() {\n"
            + "    return \"Tristate{\" + \"value=\" + value + \", isNull=\" + isNull + '}';\n"
            + "  }\n"
            + "\n"
            + "  public interface OnValue<R> {\n"
            + "    OnNull<R> onNull(Supplier<R> onNull);\n"
            + "  }\n"
            + "\n"
            + "  public interface OnNull<R> {\n"
            + "    R onAbsent(Supplier<R> onAbsent);\n"
            + "  }\n"
            + "}",
        writer.asString());
  }
}
