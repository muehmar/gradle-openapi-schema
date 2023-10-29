package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.map;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;
import static io.github.muehmar.codegenerator.java.JavaModifier.STATIC;

import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen.Argument;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

public class MapFactoryMethodeGenerator {
  private MapFactoryMethodeGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> mapFactoryMethodeGenerator() {
    return MethodGenBuilder.<JavaObjectPojo, PojoSettings>create()
        .modifiers(PUBLIC, STATIC)
        .noGenericTypes()
        .returnType(pojo -> pojo.getClassName().asString())
        .methodName("fromProperties")
        .singleArgument(
            pojo ->
                new Argument(
                    String.format(
                        "Map<String, %s>",
                        pojo.getAdditionalProperties().getType().getFullClassName()),
                    "properties"))
        .content(methodContent())
        .build()
        .append(ref(JavaRefs.JAVA_UTIL_MAP))
        .filter(JavaObjectPojo::isSimpleMapPojo)
        .filter(pojo -> pojo.getAdditionalProperties().isAllowed());
  }

  private static Generator<JavaObjectPojo, PojoSettings> methodContent() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(
            (p, s, w) -> w.println("return new %s(new HashMap<>(properties));", p.getClassName()))
        .append(ref(JavaRefs.JAVA_UTIL_HASH_MAP));
  }
}
