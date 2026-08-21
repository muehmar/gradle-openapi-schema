package com.github.muehmar.gradle.openapi.generator.mapper.resolver;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.exception.OpenApiGeneratorException;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.UnresolvedSchemaReference;
import java.util.Objects;

public class SchemaReferenceResolver {

  public static PList<Pojo> resolve(
      PList<Pojo> pojos, PList<UnresolvedSchemaReference> unresolvedSchemaReferences) {

    final PList<Pojo> resolvedPojos =
        unresolvedSchemaReferences.flatMapOptional(ref -> ref.resolve(pojos));
    final PList<UnresolvedSchemaReference> remainginReferences =
        unresolvedSchemaReferences.filter(
            ref ->
                !unresolvedSchemaReferences
                    .map(UnresolvedSchemaReference::getComponentName)
                    .contains(ref.getComponentName(), Objects::equals));
    final PList<Pojo> newPojos = pojos.concat(resolvedPojos);
    if (remainginReferences.isEmpty()) {
      return newPojos;
    } else if (remainginReferences.size() < unresolvedSchemaReferences.size()) {
      return resolve(newPojos, remainginReferences);
    } else {
      throw new OpenApiGeneratorException(
          "Unable to resolve schema references: "
              + remainginReferences
                  .map(ref -> ref.getComponentName().getSchemaName().asString())
                  .mkString(", "));
    }
  }
}
