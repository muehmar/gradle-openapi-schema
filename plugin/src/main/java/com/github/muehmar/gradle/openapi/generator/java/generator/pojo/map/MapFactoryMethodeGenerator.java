package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.map;

import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;
import static io.github.muehmar.codegenerator.java.JavaModifier.STATIC;

import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
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
                String.format(
                    "Map<String, %s> properties",
                    pojo.getAdditionalProperties().getType().getFullClassName()))
        .content(methodContent())
        .build()
        .filter(JavaObjectPojo::isSimpleMapPojo)
        .filter(pojo -> pojo.getAdditionalProperties().isAllowed());
  }

  private static Generator<JavaObjectPojo, PojoSettings> methodContent() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(
            (p, s, w) -> w.println("return new %s(new HashMap<>(properties));", p.getClassName()));
  }
}
