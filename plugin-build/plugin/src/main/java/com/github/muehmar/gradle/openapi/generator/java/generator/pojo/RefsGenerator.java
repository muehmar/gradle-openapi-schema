package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;

public class RefsGenerator {
  private RefsGenerator() {}

  public static <A, B> Generator<A, B> ref(String ref) {
    return (f, s, w) -> w.ref(ref);
  }

  public static <A, B> Generator<A, B> ref(Optional<String> ref) {
    return (f, s, w) -> ref.map(w::ref).orElse(w);
  }

  public static <B> Generator<JavaPojoMember, B> fieldRefs() {
    return (f, s, w) -> addRefs(w, f.getJavaType().getImportsAsString());
  }

  public static <B> Generator<QualifiedClassName, B> classNameRef() {
    return (className, s, w) -> w.ref(className.asString());
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
