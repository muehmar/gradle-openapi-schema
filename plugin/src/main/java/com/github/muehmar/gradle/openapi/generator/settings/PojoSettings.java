package com.github.muehmar.gradle.openapi.generator.settings;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.OpenApiSchemaGeneratorExtension;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import org.gradle.api.Project;

public class PojoSettings implements Serializable {
  private final JsonSupport jsonSupport;
  private final String packageName;
  private final String suffix;
  private final boolean enableSafeBuilder;
  private final List<ClassTypeMapping> classTypeMappings;
  private final List<FormatTypeMapping> formatTypeMappings;

  private PojoSettings(
      JsonSupport jsonSupport,
      String packageName,
      String suffix,
      boolean enableSafeBuilder,
      PList<ClassTypeMapping> classTypeMappings,
      PList<FormatTypeMapping> formatTypeMappings) {
    this.jsonSupport = jsonSupport;
    this.packageName = packageName;
    this.suffix = suffix;
    this.enableSafeBuilder = enableSafeBuilder;
    this.classTypeMappings = classTypeMappings.toArrayList();
    this.formatTypeMappings = formatTypeMappings.toArrayList();
  }

  public static PojoSettings fromOpenApiSchemaGeneratorExtension(
      OpenApiSchemaGeneratorExtension extension, Project project) {
    return new PojoSettings(
        getJsonSupport(extension),
        extension.getPackageName(project),
        extension.getSuffix(),
        extension.getEnableSafeBuilder(),
        extension.getClassMappings().map(ClassTypeMapping::fromExtension),
        extension.getFormatTypeMappings().map(FormatTypeMapping::fromExtension));
  }

  public String getPackageName() {
    return packageName;
  }

  public String getSuffix() {
    return suffix;
  }

  public JsonSupport getJsonSupport() {
    return jsonSupport;
  }

  public boolean isJacksonJson() {
    return jsonSupport.equals(JsonSupport.JACKSON);
  }

  public PList<ClassTypeMapping> getClassTypeMappings() {
    return PList.fromIter(classTypeMappings);
  }

  public PList<FormatTypeMapping> getFormatTypeMappings() {
    return PList.fromIter(formatTypeMappings);
  }

  public boolean isEnableSafeBuilder() {
    return enableSafeBuilder;
  }

  public boolean isDisableSafeBuilder() {
    return !isEnableSafeBuilder();
  }

  private static JsonSupport getJsonSupport(OpenApiSchemaGeneratorExtension extension) {
    return extension
        .getJsonSupport()
        .map(
            jsonSupport -> {
              if ("jackson".equalsIgnoreCase(jsonSupport)) {
                return JsonSupport.JACKSON;
              } else {
                return JsonSupport.NONE;
              }
            })
        .orElse(JsonSupport.NONE);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PojoSettings that = (PojoSettings) o;
    return enableSafeBuilder == that.enableSafeBuilder
        && jsonSupport == that.jsonSupport
        && Objects.equals(packageName, that.packageName)
        && Objects.equals(suffix, that.suffix)
        && Objects.equals(classTypeMappings, that.classTypeMappings)
        && Objects.equals(formatTypeMappings, that.formatTypeMappings);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        jsonSupport, packageName, suffix, enableSafeBuilder, classTypeMappings, formatTypeMappings);
  }

  @Override
  public String toString() {
    return "PojoSettings{"
        + "jsonSupport="
        + jsonSupport
        + ", packageName='"
        + packageName
        + '\''
        + ", suffix='"
        + suffix
        + '\''
        + ", enableSafeBuilder="
        + enableSafeBuilder
        + ", classTypeMappings="
        + classTypeMappings
        + ", formatTypeMappings="
        + formatTypeMappings
        + '}';
  }
}
