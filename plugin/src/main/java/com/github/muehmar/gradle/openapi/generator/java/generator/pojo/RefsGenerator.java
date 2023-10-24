package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;

public class RefsGenerator {
  private RefsGenerator() {}

  public static <A, B> Generator<A, B> ref(String ref) {
    return (f, s, w) -> w.ref(ref);
  }

  public static Generator<JavaPojoMember, PojoSettings> fieldRefs() {
    return (f, s, w) -> addRefs(w, f.getJavaType().getImportsAsString());
  }

  public static <A, B> Generator<A, B> optionalRef() {
    return Generator.ofWriterFunction(w -> w.ref(JavaRefs.JAVA_UTIL_OPTIONAL));
  }

  public static <B> Generator<JavaType, B> javaTypeRefs() {
    return (type, s, w) -> addRefs(w, type.getImportsAsString());
  }

  private static Writer addRefs(Writer writer, PList<String> imports) {
    return imports.foldLeft(writer, Writer::ref);
  }
}
