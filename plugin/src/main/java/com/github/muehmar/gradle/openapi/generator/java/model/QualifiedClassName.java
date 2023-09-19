package com.github.muehmar.gradle.openapi.generator.java.model;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.FormatTypeMapping;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class QualifiedClassName {
  private final Optional<PackageName> pkg;

  private final Name name;

  private QualifiedClassName(Optional<PackageName> pkg, Name name) {
    this.pkg = pkg;
    this.name = name;
  }

  private QualifiedClassName(Name name) {
    this(Optional.empty(), name);
  }

  private QualifiedClassName(PackageName pkg, Name name) {
    this(Optional.of(pkg), name);
  }

  public static QualifiedClassName ofQualifiedClassName(String qualifiedClassName) {
    final int i = qualifiedClassName.lastIndexOf(".");
    if (i > 0 && i < qualifiedClassName.length() - 1) {
      return new QualifiedClassName(
          PackageName.ofString(qualifiedClassName.substring(0, i)),
          Name.ofString(qualifiedClassName.substring(i + 1)));
    } else {
      return new QualifiedClassName(Name.ofString(qualifiedClassName));
    }
  }

  public static QualifiedClassName ofPojoName(PojoName pojoName) {
    final JavaIdentifier javaIdentifier = JavaIdentifier.fromString(pojoName.asString());
    return new QualifiedClassName(Name.ofString(javaIdentifier.asString()));
  }

  public static QualifiedClassName ofName(String name) {
    return new QualifiedClassName(Name.ofString(name));
  }

  public static QualifiedClassName ofName(Name name) {
    return new QualifiedClassName(name);
  }

  public static QualifiedClassName ofPackageAndName(PackageName pkg, Name name) {
    return new QualifiedClassName(pkg, name);
  }

  public static QualifiedClassName fromFormatTypeMapping(FormatTypeMapping formatTypeMapping) {
    return QualifiedClassName.ofQualifiedClassName(formatTypeMapping.getClassType());
  }

  public static QualifiedClassName fromClassTypeMapping(ClassTypeMapping classTypeMapping) {
    return QualifiedClassName.ofQualifiedClassName(classTypeMapping.getToClass());
  }

  public static Optional<QualifiedClassName> fromFormatTypeMapping(
      String formatString, PList<FormatTypeMapping> formatTypeMappings) {
    return formatTypeMappings
        .find(formatTypeMapping -> formatTypeMapping.getFormatType().equals(formatString))
        .map(QualifiedClassName::fromFormatTypeMapping);
  }

  public Name getClassName() {
    return name;
  }

  public Name asName() {
    return pkg.map(p -> p.qualifiedClassName(name)).orElse(name);
  }

  public Name getClassNameWithGenerics(Name className, Name... more) {
    final PList<Name> generics = PList.fromArray(more).cons(className);
    final String genericsCommaSeparated = generics.map(Name::asString).mkString(", ");
    final String genericString =
        genericsCommaSeparated.isEmpty() ? "" : String.format("<%s>", genericsCommaSeparated);
    return getClassName().append(genericString);
  }

  public QualifiedClassName mapWithClassMappings(PList<ClassTypeMapping> classMappings) {
    return classMappings
        .filter(classMapping -> classMapping.getFromClass().equals(name.asString()))
        .headOption()
        .map(QualifiedClassName::fromClassTypeMapping)
        .orElse(this);
  }

  public QualifiedClassName asInnerClassOf(JavaIdentifier outerClassName) {
    return new QualifiedClassName(
        pkg, Name.ofString(outerClassName.asString()).append(".").append(name));
  }
}
