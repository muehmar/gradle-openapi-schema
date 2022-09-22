package com.github.muehmar.gradle.openapi.generator.settings;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import java.util.function.Predicate;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ExcludedSchemas {
  private final PList<PojoName> excludedPojoNames;

  private ExcludedSchemas(PList<PojoName> excludedPojoNames) {
    this.excludedPojoNames = excludedPojoNames;
  }

  public static ExcludedSchemas fromExcludedPojoNames(PList<PojoName> excludedPojoNames) {
    return new ExcludedSchemas(excludedPojoNames);
  }

  public Predicate<PojoSchema> getSchemaFilter() {
    return pojoSchema ->
        not(
            excludedPojoNames.exists(
                excludedPojoName -> pojoSchema.getPojoName().equalsIgnoreCase(excludedPojoName)));
  }
}
