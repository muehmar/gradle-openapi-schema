package com.github.muehmar.gradle.openapi.generator.java;

import com.github.muehmar.gradle.openapi.generator.PojoMember;
import com.github.muehmar.gradle.openapi.generator.java.schema.JavaSchemaMapper;
import com.github.muehmar.gradle.openapi.generator.java.schema.SchemaMapperChainFactory;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.media.Schema;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class JavaPojoMember extends PojoMember {

  private static final JavaSchemaMapper typeMapperChain = SchemaMapperChainFactory.createChain();
  private static final Map<String, String> primitivesMap = createPrimitivesMap();

  private JavaPojoMember(boolean nullable, String description, JavaType javaType, String key) {
    super(nullable, description, javaType, key);
  }

  public static JavaPojoMember ofSchema(
      PojoSettings pojoSettings, Schema<?> schema, String key, boolean nullable) {

    final JavaType javaType =
        typeMapperChain
            .mapSchema(pojoSettings, schema, typeMapperChain)
            .mapPrimitiveType(name -> nullable ? name : primitivesMap.getOrDefault(name, name));

    final JavaType classMappedJavaType =
        pojoSettings.getClassTypeMappings().stream()
            .<Function<JavaType, JavaType>>map(
                mapping ->
                    t ->
                        t.replaceClass(
                            mapping.getFromClass(),
                            mapping.getToClass(),
                            Optional.ofNullable(mapping.getImports())))
            .reduce(Function::compose)
            .map(f -> f.apply(javaType))
            .orElse(javaType);

    return new JavaPojoMember(nullable, schema.getDescription(), classMappedJavaType, key);
  }

  private static Map<String, String> createPrimitivesMap() {
    final Map<String, String> formatMap = new HashMap<>();
    formatMap.put("Boolean", "boolean");
    formatMap.put("Integer", "int");
    formatMap.put("Long", "long");
    formatMap.put("Float", "float");
    formatMap.put("Double", "double");
    formatMap.put("Byte", "byte");
    return formatMap;
  }
}
