package com.github.muehmar.gradle.openapi.generator.java.type;

import com.github.muehmar.gradle.openapi.generator.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class JavaType implements Type {
  private final String name;
  private final Set<String> imports;
  private final List<JavaType> genericTypes;

  private JavaType(String name, Set<String> imports, List<JavaType> genericTypes) {
    this.name = name;
    this.imports = Collections.unmodifiableSet(imports);
    this.genericTypes = genericTypes;
  }

  public static JavaType ofNameAndImport(String name, String singleImport) {
    return new JavaType(name, Collections.singleton(singleImport), Collections.emptyList());
  }

  public static JavaType ofName(String name) {
    return new JavaType(name, Collections.emptySet(), Collections.emptyList());
  }

  public static JavaType javaMap(JavaType key, JavaType value) {
    return new JavaType("Map", Collections.singleton("java.util.Map"), Arrays.asList(key, value));
  }

  public static JavaType javaList(JavaType itemType) {
    return new JavaType(
        "List", Collections.singleton("java.util.List"), Collections.singletonList(itemType));
  }

  public JavaType replaceClass(String fromClass, String toClass, Optional<String> imports) {
    final List<JavaType> generics =
        genericTypes.stream()
            .map(t -> t.replaceClass(fromClass, toClass, imports))
            .collect(Collectors.toList());

    if (name.equals(fromClass)) {
      return new JavaType(
          toClass, imports.map(Collections::singleton).orElseGet(Collections::emptySet), generics);
    } else {
      return new JavaType(name, this.imports, generics);
    }
  }

  public JavaType mapPrimitiveType(UnaryOperator<String> mapName) {
    return new JavaType(mapName.apply(name), imports, genericTypes);
  }

  @Override
  public String getName() {
    final String genericNames =
        genericTypes.stream().map(JavaType::getName).collect(Collectors.joining(", "));
    return String.format(
        "%s%s", name, genericTypes.isEmpty() ? "" : String.format("<%s>", genericNames));
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
