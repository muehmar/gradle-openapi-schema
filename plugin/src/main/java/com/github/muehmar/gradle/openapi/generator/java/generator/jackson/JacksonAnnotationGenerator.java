package com.github.muehmar.gradle.openapi.generator.java.generator.jackson;

import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import com.github.muehmar.gradle.openapi.generator.java.JacksonRefs;
import com.github.muehmar.gradle.openapi.generator.java.JavaResolver;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.Generator;

public class JacksonAnnotationGenerator {
  private JacksonAnnotationGenerator() {}

  public static Generator<PojoMember, PojoSettings> jsonProperty() {
    return Generator.<PojoMember, PojoSettings>emptyGen()
        .append((f, s, w) -> w.println("@JsonProperty(\"%s\")", f.memberName(new JavaResolver())))
        .append(w -> w.ref(JacksonRefs.JSON_PROPERTY))
        .filter((ignore, settings) -> settings.isJacksonJson());
  }

  public static <A> Generator<A, PojoSettings> jsonIgnore() {
    return Generator.<A, PojoSettings>emptyGen()
        .append(w -> w.println("@JsonIgnore"))
        .append(w -> w.ref(JacksonRefs.JSON_IGNORE))
        .filter((ignore, settings) -> settings.isJacksonJson());
  }
}
