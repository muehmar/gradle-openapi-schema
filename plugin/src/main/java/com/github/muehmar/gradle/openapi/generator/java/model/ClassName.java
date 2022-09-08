package com.github.muehmar.gradle.openapi.generator.java.model;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.FormatTypeMapping;
import java.util.Optional;

public class ClassName {
  private final Optional<PackageName> pkg;
  private final Name name;

  private ClassName(Optional<PackageName> pkg, Name name) {
    this.pkg = pkg;
    this.name = name;
  }

  private ClassName(Name name) {
    this(Optional.empty(), name);
  }

  private ClassName(PackageName pkg, Name name) {
    this(Optional.of(pkg), name);
  }

  public static ClassName ofQualifiedClassName(String qualifiedClassName) {
    final int i = qualifiedClassName.lastIndexOf(".");
    if (i > 0 && i < qualifiedClassName.length() - 1) {
      return new ClassName(
          PackageName.ofString(qualifiedClassName.substring(0, i)),
          Name.of(qualifiedClassName.substring(i + 1)));
    } else {
      return new ClassName(Name.of(qualifiedClassName));
    }
  }

  public static ClassName ofName(String name) {
    return new ClassName(Name.of(name));
  }

  public static ClassName ofName(Name name) {
    return new ClassName(name);
  }

  public static ClassName ofPackageAndName(PackageName pkg, Name name) {
    return new ClassName(pkg, name);
  }

  public static ClassName fromFormatTypeMapping(FormatTypeMapping formatTypeMapping) {
    return Optional.of(formatTypeMapping.getImports())
        .map(String::trim)
        .filter(s -> not(s.isEmpty()))
        .map(ClassName::ofQualifiedClassName)
        .orElse(ClassName.ofName(formatTypeMapping.getClassType()));
  }

  public static ClassName fromClassTypeMapping(ClassTypeMapping classTypeMapping) {
    return Optional.of(classTypeMapping.getImports())
        .map(String::trim)
        .filter(s -> not(s.isEmpty()))
        .map(ClassName::ofQualifiedClassName)
        .orElse(ClassName.ofName(classTypeMapping.getToClass()));
  }

  public static Optional<ClassName> fromFormatTypeMapping(
      String formatString, PList<FormatTypeMapping> formatTypeMappings) {
    return formatTypeMappings
        .filter(formatTypeMapping -> formatTypeMapping.getFormatType().equals(formatString))
        .headOption()
        .map(ClassName::fromFormatTypeMapping);
  }

  public Name getClassName() {
    return name;
  }

  public Name getQualifiedClassName() {
    return pkg.map(p -> p.qualifiedClassName(name)).orElse(name);
  }

  public Name getClassNameWithGenerics(Name className, Name... more) {
    final PList<Name> generics = PList.fromArray(more).cons(className);
    final String genericsCommaSeparated = generics.map(Name::asString).mkString(", ");
    final String genericString =
        genericsCommaSeparated.isEmpty() ? "" : String.format("<%s>", genericsCommaSeparated);
    return getClassName().append(genericString);
  }

  public ClassName mapWithClassMappings(PList<ClassTypeMapping> classMappings) {
    return classMappings
        .filter(classMapping -> classMapping.getFromClass().equals(name.asString()))
        .headOption()
        .map(ClassName::fromClassTypeMapping)
        .orElse(this);
  }
}
