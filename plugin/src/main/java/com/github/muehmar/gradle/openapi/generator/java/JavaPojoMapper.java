package com.github.muehmar.gradle.openapi.generator.java;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.BasePojoMapper;
import com.github.muehmar.gradle.openapi.generator.data.MappedSchema;
import com.github.muehmar.gradle.openapi.generator.data.Pojo;
import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import com.github.muehmar.gradle.openapi.generator.java.schema.JavaSchemaMapper;
import com.github.muehmar.gradle.openapi.generator.java.schema.SchemaMapperChainFactory;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class JavaPojoMapper extends BasePojoMapper {

  private static final JavaSchemaMapper typeMapperChain = SchemaMapperChainFactory.createChain();
  private static final Map<String, String> primitivesMap = createPrimitivesMap();

  @Override
  protected PojoProcessResult createArrayPojo(
      String key, ArraySchema schema, PojoSettings pojoSettings) {
    final PojoMemberProcessResult pojoMemberProcessResult =
        pojoMemberFromSchema(key, "value", schema, pojoSettings, false);
    final Pojo pojo =
        new Pojo(
            key,
            schema.getDescription(),
            pojoSettings.getSuffix(),
            PList.single(pojoMemberProcessResult.getPojoMember()),
            true);
    return new PojoProcessResult(pojo, PList.empty());
  }

  @Override
  protected PojoMemberProcessResult pojoMemberFromSchema(
      String pojoKey, String key, Schema<?> schema, PojoSettings pojoSettings, boolean nullable) {
    final MappedSchema<JavaType> mappedSchema =
        typeMapperChain.mapSchema(pojoKey, key, schema, pojoSettings, typeMapperChain);

    final JavaType javaType =
        mappedSchema
            .getType()
            .mapPrimitiveType(name -> nullable ? name : primitivesMap.getOrDefault(name, name));

    final JavaType classMappedJavaType =
        pojoSettings
            .getClassTypeMappings()
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

    final PojoMember pojoMember =
        new PojoMember(key, schema.getDescription(), classMappedJavaType, nullable);
    return new PojoMemberProcessResult(pojoMember, mappedSchema.getOpenApiPojos());
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
