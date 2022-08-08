package com.github.muehmar.gradle.openapi.generator.java.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class JavaType implements Type {
  private final Name name;
  private final PList<String> imports;
  private final PList<JavaType> genericTypes;
  private final PList<String> enumMembers;
  private final Constraints constraints;
  private final boolean containsPojo;

  private JavaType(
      Name name,
      PList<String> imports,
      PList<JavaType> genericTypes,
      PList<String> enumMembers,
      Constraints constraints,
      boolean containsPojo) {
    this.name = name;
    this.imports = imports;
    this.genericTypes = genericTypes;
    this.enumMembers = enumMembers;
    this.constraints = constraints;
    this.containsPojo = containsPojo;
  }

  public static JavaType ofNameAndImport(Name name, String singleImport) {
    return new JavaType(
        name, PList.single(singleImport), PList.empty(), PList.empty(), Constraints.empty(), false);
  }

  public static JavaType ofNameAndImport(String name, String singleImport) {
    return JavaType.ofNameAndImport(Name.of(name), singleImport);
  }

  public static JavaType ofName(Name name) {
    return new JavaType(
        name, PList.empty(), PList.empty(), PList.empty(), Constraints.empty(), false);
  }

  public static JavaType ofName(String name) {
    return JavaType.ofName(Name.of(name));
  }

  public static JavaType ofUserDefined(Name name) {
    return new JavaType(
        name, PList.empty(), PList.empty(), PList.empty(), Constraints.empty(), true);
  }

  public static JavaType ofUserDefined(String name) {
    return JavaType.ofUserDefined(Name.of(name));
  }

  public static JavaType ofUserDefinedAndImport(Name name, String singleImport) {
    return new JavaType(
        name, PList.single(singleImport), PList.empty(), PList.empty(), Constraints.empty(), true);
  }

  public static JavaType ofUserDefinedAndImport(String name, String singleImport) {
    return JavaType.ofUserDefinedAndImport(Name.of(name), singleImport);
  }

  public static JavaType ofReference(Name name, String suffix) {
    return new JavaType(
        name.append(suffix),
        PList.empty(),
        PList.empty(),
        PList.empty(),
        Constraints.empty(),
        true);
  }

  public static JavaType ofOpenApiSchema(Name name, String suffix) {
    return new JavaType(
        name.append(suffix),
        PList.empty(),
        PList.empty(),
        PList.empty(),
        Constraints.empty(),
        true);
  }

  public static JavaType javaMap(JavaType key, JavaType value) {
    return new JavaType(
        Name.of("Map"),
        PList.single("java.util.Map"),
        PList.of(key, value),
        PList.empty(),
        Constraints.empty(),
        false);
  }

  public static JavaType javaList(JavaType itemType) {
    return new JavaType(
        Name.of("List"),
        PList.single("java.util.List"),
        PList.single(itemType),
        PList.empty(),
        Constraints.empty(),
        false);
  }

  public static JavaType javaEnum(PList<String> members) {
    return new JavaType(
        Name.of("enum"), PList.empty(), PList.empty(), members, Constraints.empty(), false);
  }

  public JavaType replaceClass(String fromClass, String toClass, Optional<String> newImports) {
    final PList<JavaType> generics =
        genericTypes.map(t -> t.replaceClass(fromClass, toClass, newImports));
    if (name.asString().equals(fromClass)) {
      return new JavaType(
          Name.of(toClass),
          PList.fromOptional(newImports),
          generics,
          enumMembers,
          constraints,
          true);
    } else {
      return new JavaType(name, imports, generics, enumMembers, constraints, containsPojo);
    }
  }

  public JavaType mapPrimitiveType(UnaryOperator<String> mapName) {
    return new JavaType(
        name.map(mapName), imports, genericTypes, enumMembers, constraints, containsPojo);
  }

  public JavaType withConstraints(Constraints constraints) {
    return new JavaType(name, imports, genericTypes, enumMembers, constraints, containsPojo);
  }

  @Override
  public Name getFullName() {
    final String genericNames =
        genericTypes.map(JavaType::getFullName).map(Name::asString).mkString(", ");
    return name.append(genericTypes.isEmpty() ? "" : String.format("<%s>", genericNames));
  }

  @Override
  public boolean isEnum() {
    return enumMembers.size() > 0;
  }

  @Override
  public boolean containsPojo() {
    return containsPojo || genericTypes.exists(JavaType::containsPojo);
  }

  @Override
  public void onEnum(Consumer<PList<String>> code) {
    if (isEnum()) {
      code.accept(enumMembers);
    }
  }

  @Override
  public PList<String> getEnumMembers() {
    return enumMembers;
  }

  @Override
  public PList<String> getImports() {
    return genericTypes.flatMap(Type::getImports).concat(imports).distinct(Function.identity());
  }

  public PList<JavaType> getGenericTypes() {
    return genericTypes;
  }

  @Override
  public Constraints getConstraints() {
    return constraints;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    JavaType javaType = (JavaType) o;
    return containsPojo == javaType.containsPojo
        && Objects.equals(name, javaType.name)
        && Objects.equals(imports, javaType.imports)
        && Objects.equals(genericTypes, javaType.genericTypes)
        && Objects.equals(enumMembers, javaType.enumMembers)
        && Objects.equals(constraints, javaType.constraints);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, imports, genericTypes, enumMembers, constraints, containsPojo);
  }

  @Override
  public String toString() {
    return "JavaType{"
        + "name='"
        + name
        + '\''
        + ", imports="
        + imports
        + ", genericTypes="
        + genericTypes
        + ", enumMembers="
        + enumMembers
        + ", constraints="
        + constraints
        + ", containsPojo="
        + containsPojo
        + '}';
  }
}
