package com.github.muehmar.gradle.openapi;

import groovy.lang.Closure;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;

public class OpenApiSchemaGeneratorExtension {

  private static final String DEFAULT_SOURCE_SET = "main";

  private String sourceSet;
  private String inputSpec;
  private String outputDir;
  private String suffix;
  private String packageName;
  private String jsonSupport;
  private final NamedDomainObjectContainer<ClassMapping> classMappings;
  private final NamedDomainObjectContainer<FormatTypeMapping> formatTypeMappings;

  @Inject
  public OpenApiSchemaGeneratorExtension(
      NamedDomainObjectContainer<ClassMapping> classMappings,
      NamedDomainObjectContainer<FormatTypeMapping> formatTypeMappings) {
    this.suffix = "";
    this.classMappings = classMappings;
    this.formatTypeMappings = formatTypeMappings;
  }

  public List<ClassMapping> getClassMappings() {
    return new ArrayList<>(classMappings);
  }

  public void classMappings(Closure<ClassMapping> closure) {
    this.classMappings.configure(closure);
  }

  public List<FormatTypeMapping> getFormatTypeMappings() {
    return new ArrayList<>(formatTypeMappings);
  }

  public void formatTypeMappings(Closure<FormatTypeMapping> closure) {
    this.formatTypeMappings.configure(closure);
  }

  public String getSourceSet() {
    return Optional.ofNullable(sourceSet).orElse(DEFAULT_SOURCE_SET);
  }

  public void setSourceSet(String sourceSet) {
    this.sourceSet = sourceSet;
  }

  public String getInputSpec() {
    return Optional.ofNullable(inputSpec)
        .orElseThrow(
            () ->
                new InvalidUserDataException(
                    "Could not generate schema, no input spec defined: Declare a correct path to a valid openapi spec."));
  }

  public void setInputSpec(String inputSpec) {
    this.inputSpec = inputSpec;
  }

  public String getOutputDir(Project project) {
    return Optional.ofNullable(outputDir)
        .orElseGet(() -> String.format("%s/generated/openapi", project.getBuildDir().toString()));
  }

  public void setOutputDir(String outputDir) {
    this.outputDir = outputDir;
  }

  public String getSuffix() {
    return Optional.ofNullable(suffix).orElse("");
  }

  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }

  public String getPackageName(Project project) {
    return Optional.ofNullable(packageName)
        .orElseGet(() -> String.format("%s.%s.api.model", project.getGroup(), project.getName()));
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public Optional<String> getJsonSupport() {
    return Optional.ofNullable(jsonSupport);
  }

  public void setJsonSupport(String jsonSupport) {
    this.jsonSupport = jsonSupport;
  }

  @Override
  public String toString() {
    return "OpenApiPojoGeneratorExtension{"
        + "sourceSet='"
        + sourceSet
        + '\''
        + ", inputSpec='"
        + inputSpec
        + '\''
        + ", outputDir='"
        + outputDir
        + '\''
        + ", suffix='"
        + suffix
        + '\''
        + ", packageName='"
        + packageName
        + '\''
        + ", jsonSupport='"
        + jsonSupport
        + '\''
        + ", classMappings="
        + new ArrayList<>(classMappings)
        + ", formatTypeMappings="
        + new ArrayList<>(formatTypeMappings)
        + '}';
  }

  public static class FormatTypeMapping {
    private final String name;
    private String formatType;
    private String classType;
    private String imports;

    public FormatTypeMapping(String name) {
      this.name = name;
    }

    public String getFormatType() {
      return formatType;
    }

    public void setFormatType(String formatType) {
      this.formatType = formatType;
    }

    public String getClassType() {
      return classType;
    }

    public void setClassType(String classType) {
      this.classType = classType;
    }

    public String getImports() {
      return imports;
    }

    public void setImports(String imports) {
      this.imports = imports;
    }

    @Override
    public String toString() {
      return "FormatTypeMapping{"
          + "name='"
          + name
          + '\''
          + ", formatType='"
          + formatType
          + '\''
          + ", classType='"
          + classType
          + '\''
          + ", imports='"
          + imports
          + '\''
          + '}';
    }
  }

  public static class ClassMapping {
    private final String name;
    private String fromClass;
    private String toClass;
    private String imports;

    public ClassMapping(String name) {
      this.name = name;
    }

    public String getFromClass() {
      return fromClass;
    }

    public void setFromClass(String fromClass) {
      this.fromClass = fromClass;
    }

    public String getToClass() {
      return toClass;
    }

    public void setToClass(String toClass) {
      this.toClass = toClass;
    }

    public String getImports() {
      return imports;
    }

    public void setImports(String imports) {
      this.imports = imports;
    }

    @Override
    public String toString() {
      return "ClassMapping{"
          + "name='"
          + name
          + '\''
          + ", fromClass='"
          + fromClass
          + '\''
          + ", toClass='"
          + toClass
          + '\''
          + ", imports='"
          + imports
          + '\''
          + '}';
    }
  }
}
