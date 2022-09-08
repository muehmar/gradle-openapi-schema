package com.github.muehmar.gradle.openapi.generator.java.model;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.FormatTypeMapping;
import com.github.muehmar.gradle.openapi.util.Suppliers;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ClassName {

  /** This map cannot be initialized statically, as the map is populated instances of this class. */
  private static final Supplier<Map<ClassName, ClassName>> PRIMITIVE_MAP =
      Suppliers.cached(ClassName::createPrimitiveMap);

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
          Name.ofString(qualifiedClassName.substring(i + 1)));
    } else {
      return new ClassName(Name.ofString(qualifiedClassName));
    }
  }

  public static ClassName ofName(String name) {
    return new ClassName(Name.ofString(name));
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

  public ClassName asPrimitive() {
    final ClassName value = PRIMITIVE_MAP.get().get(this);
    return Optional.ofNullable(value).orElse(this);
  }

  private static Map<ClassName, ClassName> createPrimitiveMap() {
    final Map<ClassName, ClassName> map = new HashMap<>();
    map.put(ClassNames.DOUBLE, ClassNames.DOUBLE_PRIMITIVE);
    map.put(ClassNames.FLOAT, ClassNames.FLOAT_PRIMITIVE);
    map.put(ClassNames.LONG, ClassNames.LONG_PRIMITIVE);
    map.put(ClassNames.INTEGER, ClassNames.INTEGER_PRIMITIVE);
    map.put(ClassNames.BOOLEAN, ClassNames.BOOLEAN_PRIMITIVE);
    map.put(ClassNames.BYTE, ClassNames.BYTE_PRIMITIVE);
    map.put(ClassNames.CHARACTER, ClassNames.CHAR_PRIMITIVE);
    map.put(ClassNames.SHORT, ClassNames.SHORT_PRIMITIVE);
    return map;
  }
}
