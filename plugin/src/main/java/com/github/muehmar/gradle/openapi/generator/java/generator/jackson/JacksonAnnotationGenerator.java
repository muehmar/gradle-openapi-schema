package com.github.muehmar.gradle.openapi.generator.java.generator.jackson;

import static com.github.muehmar.gradle.openapi.generator.java.generator.Filters.isJacksonJson;

import com.github.muehmar.gradle.openapi.generator.java.JacksonRefs;
import com.github.muehmar.gradle.openapi.generator.java.JavaResolver;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

public class JacksonAnnotationGenerator {
  private JacksonAnnotationGenerator() {}

  public static Generator<PojoMember, PojoSettings> jsonProperty() {
    return Generator.<PojoMember, PojoSettings>emptyGen()
        .append((f, s, w) -> w.println("@JsonProperty(\"%s\")", f.memberName(new JavaResolver())))
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
}
