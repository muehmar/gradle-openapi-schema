package com.github.muehmar.gradle.openapi.generator.java.model.name;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.PackageName;
import com.github.muehmar.gradle.openapi.generator.java.model.PackageNames;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
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

  public String asString() {
    return asName().asString();
  }

  public Optional<PackageName> getPackageName() {
    return pkg;
  }

  public boolean usedForImport() {
    return pkg.isPresent() && !isJavaLangPackage();
  }

  public boolean isJavaLangPackage() {
    return pkg.equals(Optional.of(PackageNames.JAVA_LANG));
  }

  public Name getClassNameWithGenerics(Name className, Name... more) {
    final PList<Name> genericTypes = PList.fromArray(more).cons(className);
    return getClassNameWithGenerics(genericTypes);
  }

  public Name getClassNameWithGenerics(PList<Name> genericTypes) {
    final String genericsCommaSeparated = genericTypes.map(Name::asString).mkString(", ");
    final String genericString =
        genericsCommaSeparated.isEmpty() ? "" : String.format("<%s>", genericsCommaSeparated);
    return getClassName().append(genericString);
  }

  public Optional<QualifiedClassName> mapWithClassMappings(PList<ClassTypeMapping> classMappings) {
    return classMappings
        .filter(classMapping -> classMapping.getFromClass().equals(name.asString()))
        .headOption()
        .map(QualifiedClassName::fromClassTypeMapping);
  }

  public QualifiedClassName asInnerClassOf(JavaName outerClassName) {
    return new QualifiedClassName(
        pkg, Name.ofString(outerClassName.asString()).append(".").append(name));
  }
}
