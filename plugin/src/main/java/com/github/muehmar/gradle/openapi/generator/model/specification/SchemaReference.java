package com.github.muehmar.gradle.openapi.generator.model.specification;

import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import java.net.URI;
import java.util.Optional;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class SchemaReference {
  private final Optional<OpenApiSpec> remoteSpec;
  private final Name schemaName;

  private SchemaReference(Optional<OpenApiSpec> remoteSpec, Name schemaName) {
    this.remoteSpec = remoteSpec;
    this.schemaName = schemaName;
  }

  public static SchemaReference fromRefString(String ref) {
    final URI uri = URI.create(ref);
    final String fragment = uri.getFragment();
    if (fragment == null) {
      throw new IllegalArgumentException(
          "Invalid schema reference: '" + ref + "': Contains no fragment.");
    }

    final Name schemaName = parseSchemaName(fragment);
    final String path = uri.getPath();

    if (path.isEmpty()) {
      return new SchemaReference(Optional.empty(), schemaName);
    }

    if (ref.startsWith(path)) {
      final OpenApiSpec openApiSpec = OpenApiSpec.fromString(path);
      return new SchemaReference(Optional.of(openApiSpec), schemaName);
    }

    throw new IllegalArgumentException("Remote references are not yet supported: '" + ref + "'");
  }

  private static Name parseSchemaName(String ref) {
    final int i = ref.lastIndexOf('/');
    return Name.ofString(ref.substring(Math.max(i + 1, 0))).startUpperCase();
  }

  public Optional<OpenApiSpec> getRemoteSpec() {
    return remoteSpec;
  }

  public Name getSchemaName() {
    return schemaName;
  }
}
