package com.github.muehmar.gradle.openapi.generator.java;

import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.schema.JavaSchemaMapper;
import com.github.muehmar.gradle.openapi.generator.java.schema.ReferenceMapper;
import com.github.muehmar.gradle.openapi.generator.java.schema.SchemaMapperChainFactory;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.mapper.BasePojoMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.PojoMemberProcessResult;
import com.github.muehmar.gradle.openapi.generator.mapper.PojoProcessResult;
import com.github.muehmar.gradle.openapi.generator.model.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.MappedSchema;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

// TODO: No specific mappers, one mapper should map to the generic pojo/pojomember and type
public class JavaPojoMapper extends BasePojoMapper {

  private static final JavaSchemaMapper typeMapperChain = SchemaMapperChainFactory.createChain();

  @Override
  protected PojoProcessResult fromArraysSchema(
      Name pojoName, ArraySchema schema, PojoSettings pojoSettings) {
    final PojoMemberProcessResult pojoMemberProcessResult =
        toPojoMemberFromSchema(
            pojoName, Name.of("value"), schema, pojoSettings, Necessity.REQUIRED, NOT_NULLABLE);
    final Pojo pojo =
        Pojo.ofArray(
            pojoName,
            schema.getDescription(),
            pojoSettings.getSuffix(),
            pojoMemberProcessResult.getPojoMember());
    return new PojoProcessResult(pojo, PList.empty());
  }

  @Override
  protected PojoMemberProcessResult toPojoMemberFromSchema(
      Name pojoName,
      Name pojoMemberName,
      Schema<?> schema,
      PojoSettings pojoSettings,
      Necessity necessity,
      Nullability nullability) {
    final MappedSchema<JavaType> mappedSchema =
        typeMapperChain.mapSchema(pojoName, pojoMemberName, schema, pojoSettings, typeMapperChain);

    final JavaType javaType = mappedSchema.getType();

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
        new PojoMember(
            pojoMemberName, schema.getDescription(), classMappedJavaType, necessity, nullability);
    return new PojoMemberProcessResult(pojoMember, mappedSchema.getOpenApiPojos());
  }

  @Override
  protected ComposedPojo fromComposedSchema(
      Name name,
      String description,
      ComposedPojo.CompositionType type,
      PList<Schema<?>> schemas,
      PojoSettings pojoSettings) {

    final PList<PojoName> pojoNames =
        schemas
            .flatMapOptional(
                schema -> Optional.ofNullable(schema.get$ref()).map(ReferenceMapper::getRefName))
            .map(n -> PojoName.ofNameAndSuffix(n, pojoSettings.getSuffix()));

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
                  return new OpenApiPojo(
                      PojoName.ofNameAndSuffix(openApiPojoName, pojoSettings.getSuffix()), schema);
                });

    final PojoName pojoName = PojoName.ofNameAndSuffix(name, pojoSettings.getSuffix());

    return new ComposedPojo(pojoName, description, type, pojoNames, openApiPojos);
  }
}
