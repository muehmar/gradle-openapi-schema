package com.github.muehmar.gradle.openapi.generator.settings;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import java.util.function.Predicate;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ExcludedSchemas {
  private final PList<Name> excludedNames;

  private ExcludedSchemas(PList<Name> excludedNames) {
    this.excludedNames = excludedNames;
  }

  public static ExcludedSchemas fromExcludedPojoNames(PList<Name> excludedNames) {
    return new ExcludedSchemas(excludedNames);
  }

  public Predicate<PojoSchema> getSchemaFilter() {
    return pojoSchema -> {
      final Predicate<Name> matchesPojoName =
          name -> pojoSchema.getPojoName().getName().equalsIgnoreCase(name);
      final Predicate<Name> matchesSchemaName =
          name -> pojoSchema.getSchemaName().asName().equalsIgnoreCase(name);

      return not(
          excludedNames.exists(
              excludedName ->
                  matchesPojoName.test(excludedName) || matchesSchemaName.test(excludedName)));
    };
  }
}
