package com.github.muehmar.gradle.openapi.generator.java.generator;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.writer.Writer;

public class RefsGenerator {
  private RefsGenerator() {}

  public static Generator<PojoMember, PojoSettings> fieldRefs() {
    return (f, s, w) -> addRefs(w, f.getType().getImports());
  }

  public static <A, B> Generator<A, B> optionalRef() {
    return Generator.ofWriterFunction(w -> w.ref(JavaRefs.OPTIONAL));
  }

  private static Writer addRefs(Writer writer, PList<String> imports) {
    return imports.foldLeft(writer, Writer::ref);
  }
}
