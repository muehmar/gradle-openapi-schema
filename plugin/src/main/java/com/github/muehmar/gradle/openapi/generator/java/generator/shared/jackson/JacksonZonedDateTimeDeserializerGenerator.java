package com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson;

import static com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs.OPENAPI_UTIL_PACKAGE;
import static com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs.ZONED_DATE_TIME_DESERIALIZER_CLASSNAME;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.AnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.ref.JacksonRefs;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.settings.JsonSupport;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.java.MethodGen.Argument;
import io.github.muehmar.codegenerator.writer.Writer;

public class JacksonZonedDateTimeDeserializerGenerator {
  private JacksonZonedDateTimeDeserializerGenerator() {}

  public static Generator<Void, PojoSettings> zonedDateTimeDeserializer() {
    return Generator.<Void, PojoSettings>emptyGen()
        .append(packageDeclaration())
        .appendSingleBlankLine()
        .append(imports())
        .appendSingleBlankLine()
        .append(classDeclaration())
        .appendSingleBlankLine()
        .append(deserializeMethod(), 1)
        .append(classEnd());
  }

  private static Generator<Void, PojoSettings> packageDeclaration() {
    return (data, settings, writer) -> writer.println("package %s;", OPENAPI_UTIL_PACKAGE);
  }

  private static Generator<Void, PojoSettings> imports() {
    return Generator.<Void, PojoSettings>emptyGen()
        .append(JacksonRefs.generator(JacksonRefs::jsonDeserializerRef))
        .append(JacksonRefs.generator(JacksonRefs::jsonParserRef))
        .append(JacksonRefs.generator(JacksonRefs::deserializationContextRef))
        .append(
            (a, s, w) ->
                s.getJsonSupport() == JsonSupport.JACKSON_2
                    ? w.ref(JavaRefs.JAVA_IO_IOEXCEPTION)
                    : w)
        .append(w -> w.ref(JavaRefs.JAVA_TIME_ZONED_DATE_TIME))
        .append(Writer::printRefs)
        .append(w -> w.println());
  }

  private static Generator<Void, PojoSettings> classDeclaration() {
    return (data, settings, writer) -> {
      final String baseClass =
          settings.getJsonSupport() == JsonSupport.JACKSON_2
              ? "JsonDeserializer"
              : "ValueDeserializer";
      return writer.println(
          "public class %s extends %s<ZonedDateTime> {",
          ZONED_DATE_TIME_DESERIALIZER_CLASSNAME, baseClass);
    };
  }

  private static Generator<Void, PojoSettings> deserializeMethod() {
    return (data, settings, writer) -> {
      final PList<String> exceptions =
          settings.getJsonSupport() == JsonSupport.JACKSON_2
              ? PList.of("IOException")
              : PList.empty();

      final Generator<Void, PojoSettings> method =
          JavaGenerators.<Void, PojoSettings>methodGen()
              .modifiers(PUBLIC)
              .noGenericTypes()
              .returnType("ZonedDateTime")
              .methodName("deserialize")
              .arguments(deserializeMethodArguments())
              .throwsExceptions(d -> exceptions)
              .content(deserializeMethodContent())
              .build();

      return AnnotationGenerator.<Void, PojoSettings>override()
          .append(method)
          .generate(data, settings, writer);
    };
  }

  private static java.util.function.Function<Void, PList<Argument>> deserializeMethodArguments() {
    return data ->
        PList.of(new Argument("JsonParser", "p"), new Argument("DeserializationContext", "ctxt"));
  }

  private static Generator<Void, PojoSettings> deserializeMethodContent() {
    return Generator.constant("return ZonedDateTime.parse(p.getText().trim());");
  }

  private static Generator<Void, PojoSettings> classEnd() {
    return Generator.constant("}");
  }
}
