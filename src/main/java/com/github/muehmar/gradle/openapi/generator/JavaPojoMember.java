package com.github.muehmar.gradle.openapi.generator;

import com.github.muehmar.gradle.openapi.OpenApiSchemaGeneratorExtension;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.DateSchema;
import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class JavaPojoMember extends PojoMember {

  private static final Map<String, Type> intTypeFormatMap =
      Map.of("int32", new Type("Integer"), "int64", new Type("Long"));
  private static final Map<String, Type> numberTypeFormatMap =
      Map.of("float", new Type("Float"), "double", new Type("Double"));
  private static final Map<String, Type> stringTypeFormatMap =
      Map.of("uri", new Type("URI", "java.net.URI"));
  private static final Map<String, Type> dateTypeFormatMap =
      Map.of(
          "date",
          new Type("LocalDate", "java.time.LocalDate"),
          "date-time",
          new Type("LocalDateTime", "java.time.LocalDateTime"));

  private static final Map<String, String> primitivesMap =
      Map.of(
          "Boolean", "boolean", "Integer", "int", "Long", "long", "Float", "float", "Double",
          "double");

  private JavaPojoMember(boolean nullable, String description, Type type, String key) {
    super(nullable, description, type, key);
  }

  public static JavaPojoMember ofSchema(
      OpenApiSchemaGeneratorExtension config, Schema<?> schema, String key, boolean nullable) {

    final Function<Type, JavaPojoMember> createPojoMember =
        type -> {
          String toClass = type.getName();
          final List<String> allImports = new ArrayList<>(type.getImports());
          for (OpenApiSchemaGeneratorExtension.ClassMapping mapping : config.getClassMappings()) {
            final String replacedClass =
                toClass.replaceAll(
                    "([^A-Za-z]|\\b)(" + mapping.getFromClass() + ")([^A-Za-z]|\\b)",
                    "$1" + mapping.getToClass() + "$3");
            if (!replacedClass.equals(toClass)) {
              Optional.ofNullable(mapping.getImports()).ifPresent(allImports::add);
            }

            toClass = replacedClass;
          }

          return new JavaPojoMember(
              nullable, schema.getDescription(), new Type(toClass, allImports), key);
        };

    if (schema instanceof StringSchema) {
      return ofStringSchema(config, (StringSchema) schema, createPojoMember);
    } else if (schema instanceof DateSchema) {
      return ofDateSchema((DateSchema) schema, createPojoMember);
    }

    final Type type =
        getType(config, schema)
            .map(name -> nullable ? name : primitivesMap.getOrDefault(name, name));
    return createPojoMember.apply(type);
  }

  private static Type getType(OpenApiSchemaGeneratorExtension config, Schema<?> schema) {
    final String type = schema.getType();
    if ("integer".equals(type)) {
      return getIntegerType(schema.getFormat());
    } else if ("number".equals(type)) {
      return getNumberType(schema.getFormat());
    } else if ("boolean".equals(type)) {
      return getBooleanType(schema.getFormat());
    } else if (schema instanceof StringSchema) {
      return new Type("String");
    } else if (schema instanceof MapSchema) {
      final MapSchema mapSchema = (MapSchema) schema;
      final Object additionalProperties = mapSchema.getAdditionalProperties();
      if (additionalProperties instanceof Schema) {
        return getRefType(config, ((Schema<?>) additionalProperties).get$ref())
            .map(
                name -> String.format("Map<String, %s>", name),
                imports -> {
                  imports.add("java.util.Map");
                  return imports;
                });
      } else {
        throw new IllegalArgumentException("Not supported additionalProperties");
      }
    } else if (schema instanceof ArraySchema) {
      final ArraySchema arraySchema = (ArraySchema) schema;
      final Schema<?> items = arraySchema.getItems();
      return getType(config, items)
          .map(
              name -> String.format("List<%s>", name),
              imports -> {
                imports.add("java.util.List");
                return imports;
              });
    } else if (schema.get$ref() != null) {
      final String $ref = schema.get$ref();
      return getRefType(config, $ref);
    } else {
      return new Type("Void");
    }
  }

  private static JavaPojoMember ofStringSchema(
      OpenApiSchemaGeneratorExtension config,
      StringSchema schema,
      Function<Type, JavaPojoMember> createPojoMember) {
    return Optional.ofNullable(schema.getFormat())
        .map(stringTypeFormatMap::get)
        .map(createPojoMember)
        .orElseGet(
            () ->
                config.getFormatTypeMappings().stream()
                    .filter(mapping -> mapping.getFormatType().equals(schema.getFormat()))
                    .findFirst()
                    .map(
                        mapping -> {
                          final Type type =
                              Optional.ofNullable(mapping.getImports())
                                  .map(imports -> new Type(mapping.getClassType(), imports))
                                  .orElseGet(() -> new Type(mapping.getClassType()));
                          return createPojoMember.apply(type);
                        })
                    .orElseGet(() -> createPojoMember.apply(new Type("String"))));
  }

  private static JavaPojoMember ofDateSchema(
      DateSchema schema, Function<Type, JavaPojoMember> createPojoMember) {
    return createPojoMember.apply(
        dateTypeFormatMap.getOrDefault(schema.getFormat(), new Type("Void")));
  }

  private static Type getRefType(OpenApiSchemaGeneratorExtension config, String ref) {
    final int i = ref.lastIndexOf('/');
    return new Type(ref.substring(Math.max(i + 1, 0)) + config.getSuffix());
  }

  private static Type getIntegerType(String format) {
    return Optional.ofNullable(format).map(intTypeFormatMap::get).orElse(new Type("Integer"));
  }

  private static Type getNumberType(String format) {
    return Optional.ofNullable(format).map(numberTypeFormatMap::get).orElse(new Type("Double"));
  }

  private static Type getBooleanType(String format) {
    return new Type("Boolean");
  }
}
