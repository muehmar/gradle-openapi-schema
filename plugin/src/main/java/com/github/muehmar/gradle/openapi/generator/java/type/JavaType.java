package com.github.muehmar.gradle.openapi.generator.java.type;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;

import com.github.muehmar.gradle.openapi.generator.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class JavaType implements Type {
  private final String name;
  private final Set<String> imports;
  private final List<JavaType> genericTypes;
  private final List<String> enumMembers;

  private JavaType(
      String name, Set<String> imports, List<JavaType> genericTypes, List<String> enumMembers) {
    this.name = name;
    this.imports = unmodifiableSet(imports);
    this.genericTypes = unmodifiableList(genericTypes);
    this.enumMembers = unmodifiableList(enumMembers);
  }

  public static JavaType ofNameAndImport(String name, String singleImport) {
    return new JavaType(
        name, singleton(singleImport), Collections.emptyList(), Collections.emptyList());
  }

  public static JavaType ofName(String name) {
    return new JavaType(name, emptySet(), Collections.emptyList(), Collections.emptyList());
  }

  public static JavaType javaMap(JavaType key, JavaType value) {
    return new JavaType(
        "Map", singleton("java.util.Map"), Arrays.asList(key, value), Collections.emptyList());
  }

  public static JavaType javaList(JavaType itemType) {
    return new JavaType(
        "List",
        singleton("java.util.List"),
        Collections.singletonList(itemType),
        Collections.emptyList());
  }

  public static JavaType javaEnum(List<String> members) {
    return new JavaType("enum", emptySet(), emptyList(), members);
  }

  public JavaType replaceClass(String fromClass, String toClass, Optional<String> imports) {
    final List<JavaType> generics =
        genericTypes
            .stream()
            .map(t -> t.replaceClass(fromClass, toClass, imports))
            .collect(Collectors.toList());

    if (name.equals(fromClass)) {
      return new JavaType(
          toClass,
          imports.map(Collections::singleton).orElseGet(Collections::emptySet),
          generics,
          enumMembers);
    } else {
      return new JavaType(name, this.imports, generics, enumMembers);
    }
  }

  public JavaType mapPrimitiveType(UnaryOperator<String> mapName) {
    return new JavaType(mapName.apply(name), imports, genericTypes, enumMembers);
  }

  @Override
  public String getName() {
    final String genericNames =
        genericTypes.stream().map(JavaType::getName).collect(Collectors.joining(", "));
    return String.format(
        "%s%s", name, genericTypes.isEmpty() ? "" : String.format("<%s>", genericNames));
  }

  @Override
  public boolean isEnum() {
    return enumMembers.size() > 0;
  }

  @Override
  public void onEnum(Consumer<List<String>> code) {
    if (isEnum()) {
      code.accept(enumMembers);
    }
  }

  @Override
  public Set<String> getImports() {
    final Set<String> allImports = new HashSet<>();
    genericTypes.forEach(t -> allImports.addAll(t.getImports()));
    allImports.addAll(this.imports);
    return allImports;
  }

  public List<JavaType> getGenericTypes() {
    return genericTypes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    JavaType javaType = (JavaType) o;
    return Objects.equals(name, javaType.name)
        && Objects.equals(imports, javaType.imports)
        && Objects.equals(genericTypes, javaType.genericTypes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, imports, genericTypes);
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
        + '}';
  }
}
