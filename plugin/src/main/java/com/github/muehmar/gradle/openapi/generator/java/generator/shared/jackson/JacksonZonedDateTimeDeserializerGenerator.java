package com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson;

import static com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs.*;

public class JacksonZonedDateTimeDeserializerGenerator {
  private JacksonZonedDateTimeDeserializerGenerator() {}

  public static io.github.muehmar.codegenerator.Generator<Void, Void> zonedDateTimeDeserializer() {
    return io.github.muehmar.codegenerator.Generator.ofWriterFunction(
        w ->
            w.println("package %s;", OPENAPI_UTIL_PACKAGE)
                .println()
                .println("import com.fasterxml.jackson.core.JsonParser;")
                .println("import com.fasterxml.jackson.databind.DeserializationContext;")
                .println("import com.fasterxml.jackson.databind.JsonDeserializer;")
                .println("import java.io.IOException;")
                .println("import java.time.ZonedDateTime;")
                .println()
                .println(
                    "public class %s extends JsonDeserializer<ZonedDateTime> {",
                    ZONED_DATE_TIME_DESERIALIZER_CLASSNAME)
                .println()
                .tab(1)
                .println("@Override")
                .tab(1)
                .println(
                    "public ZonedDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {")
                .tab(2)
                .println("return ZonedDateTime.parse(p.getText().trim());")
                .tab(1)
                .println("}")
                .println("}"));
  }
}
