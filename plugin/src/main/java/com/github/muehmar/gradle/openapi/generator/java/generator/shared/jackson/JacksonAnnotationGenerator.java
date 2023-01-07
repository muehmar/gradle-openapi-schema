package com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.Filters.isJacksonJson;

import com.github.muehmar.gradle.openapi.generator.java.JacksonRefs;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import java.util.Optional;

public class JacksonAnnotationGenerator {
  private JacksonAnnotationGenerator() {}

  public static Generator<JavaPojoMember, PojoSettings> jsonProperty() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append((f, s, w) -> w.println("@JsonProperty(\"%s\")", f.getName()))
        .append(w -> w.ref(JacksonRefs.JSON_PROPERTY))
        .filter(isJacksonJson());
  }

  public static <A> Generator<A, PojoSettings> jsonIgnore() {
    return Generator.<A, PojoSettings>emptyGen()
        .append(w -> w.println("@JsonIgnore"))
        .append(w -> w.ref(JacksonRefs.JSON_IGNORE))
        .filter(isJacksonJson());
  }

  public static <A> Generator<A, PojoSettings> jsonIncludeNonNull() {
    return Generator.<A, PojoSettings>emptyGen()
        .append(w -> w.println("@JsonInclude(JsonInclude.Include.NON_NULL)"))
        .append(w -> w.ref(JacksonRefs.JSON_INCLUDE))
        .filter(isJacksonJson());
  }

  public static <A> Generator<A, PojoSettings> jsonValue() {
    return Generator.<A, PojoSettings>emptyGen()
        .append(w -> w.println("@JsonValue"))
        .append(w -> w.ref(JacksonRefs.JSON_VALUE))
        .filter(isJacksonJson());
  }

  public static <A> Generator<A, PojoSettings> jsonCreator() {
    return Generator.<A, PojoSettings>emptyGen()
        .append(w -> w.println("@JsonCreator"))
        .append(w -> w.ref(JacksonRefs.JSON_CREATOR))
        .filter(isJacksonJson());
  }

  private static <A> Generator<A, PojoSettings> jsonPojoBuilder(Optional<String> prefix) {
    final String prefixString =
        prefix.map(p -> String.format("(withPrefix = \"%s\")", p)).orElse("");
    return Generator.<A, PojoSettings>emptyGen()
        .append(w -> w.println("@JsonPOJOBuilder%s", prefixString))
        .append(w -> w.ref(JacksonRefs.JSON_POJO_BUILDER))
        .filter(isJacksonJson());
  }

  public static <A> Generator<A, PojoSettings> jsonPojoBuilderWithPrefix(String prefix) {
    return jsonPojoBuilder(Optional.of(prefix));
  }

  public static <T extends JavaPojo> Generator<T, PojoSettings> jsonDeserialize() {
    return Generator.<T, PojoSettings>emptyGen()
        .append(
            (pojo, settings, writer) ->
                writer.println(
                    "@JsonDeserialize(builder = %s.Builder.class)", pojo.getName().asString()))
        .append(w -> w.ref(JacksonRefs.JSON_DESERIALIZE))
        .filter(isJacksonJson());
  }
}
