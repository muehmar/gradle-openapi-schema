package com.github.muehmar.gradle.openapi.generator.java;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.data.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.data.MappedSchema;
import com.github.muehmar.gradle.openapi.generator.data.Name;
import com.github.muehmar.gradle.openapi.generator.data.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.data.Pojo;
import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import com.github.muehmar.gradle.openapi.generator.java.schema.JavaSchemaMapper;
import com.github.muehmar.gradle.openapi.generator.java.schema.ReferenceMapper;
import com.github.muehmar.gradle.openapi.generator.java.schema.SchemaMapperChainFactory;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.mapper.BasePojoMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.PojoMemberProcessResult;
import com.github.muehmar.gradle.openapi.generator.mapper.PojoProcessResult;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class JavaPojoMapper extends BasePojoMapper {

  private static final JavaSchemaMapper typeMapperChain = SchemaMapperChainFactory.createChain();
  private static final Map<String, String> primitivesMap = createPrimitivesMap();

  @Override
  protected PojoProcessResult fromArraysSchema(
      Name pojoName, ArraySchema schema, PojoSettings pojoSettings) {
    final PojoMemberProcessResult pojoMemberProcessResult =
        toPojoMemberFromSchema(pojoName, Name.of("value"), schema, pojoSettings, false);
    final Pojo pojo =
        new Pojo(
            pojoName,
            schema.getDescription(),
            pojoSettings.getSuffix(),
            PList.single(pojoMemberProcessResult.getPojoMember()),
            true);
    return new PojoProcessResult(pojo, PList.empty());
  }

  @Override
  protected PojoMemberProcessResult toPojoMemberFromSchema(
      Name pojoName,
      Name pojoMemberName,
      Schema<?> schema,
      PojoSettings pojoSettings,
      boolean nullable) {
    final MappedSchema<JavaType> mappedSchema =
        typeMapperChain.mapSchema(pojoName, pojoMemberName, schema, pojoSettings, typeMapperChain);

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
        new PojoMember(pojoMemberName, schema.getDescription(), classMappedJavaType, nullable);
    return new PojoMemberProcessResult(pojoMember, mappedSchema.getOpenApiPojos());
  }

  @Override
  protected ComposedPojo fromComposedSchema(
      Name name,
      String description,
      ComposedPojo.CompositionType type,
      PList<Schema<?>> schemas,
      PojoSettings pojoSettings) {

    final PList<Name> pojoNames =
        schemas.flatMapOptional(
            schema -> Optional.ofNullable(schema.get$ref()).map(ReferenceMapper::getRefName));

    final PList<Schema<?>> inlineDefinitions =
        schemas.filter(schema -> Objects.isNull(schema.get$ref()));

    final PList<OpenApiPojo> openApiPojos =
        inlineDefinitions
            .zipWithIndex()
            .map(
                p -> {
                  final Schema<?> schema = p.first();
                  final Integer index = p.second();
                  final String openApiPojoNameSuffix =
                      inlineDefinitions.size() > 1 ? "" + index : "";
                  final Name openApiPojoName =
                      name.append(JavaResolver.snakeCaseToPascalCase(type.name()))
                          .append(openApiPojoNameSuffix);
                  return new OpenApiPojo(openApiPojoName, schema);
                });

    return new ComposedPojo(
        name, description, pojoSettings.getSuffix(), type, pojoNames, openApiPojos);
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
