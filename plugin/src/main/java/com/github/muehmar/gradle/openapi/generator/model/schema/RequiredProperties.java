package com.github.muehmar.gradle.openapi.generator.model.schema;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojobuilder.annotations.FieldBuilder;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@PojoBuilder
@EqualsAndHashCode
@ToString
class RequiredProperties {
  private final PList<String> propertyNames;
  private final PList<String> requiredPropertyNames;

  RequiredProperties(PList<String> propertyNames, PList<String> requiredPropertyNames) {
    this.propertyNames = propertyNames;
    this.requiredPropertyNames = requiredPropertyNames;
  }

  public PList<String> getRequiredAdditionalPropertyNames() {
    final Predicate<String> isNotNormalRequiredProperty =
        req -> not(propertyNames.exists(req::equals));
    return requiredPropertyNames.filter(isNotNormalRequiredProperty);
  }

  public boolean isRequired(MemberSchema memberSchema) {
    return requiredPropertyNames.exists(name -> memberSchema.getName().asString().equals(name));
  }

  @FieldBuilder(fieldName = "propertyNames", disableDefaultMethods = true)
  static PList<String> propertyNames(Iterable<String> propertyNames) {
    return PList.fromIter(propertyNames);
  }

  @FieldBuilder(fieldName = "requiredPropertyNames", disableDefaultMethods = true)
  static PList<String> requiredPropertyNamesNullable(Iterable<String> requiredPropertyNames) {
    return Optional.ofNullable(requiredPropertyNames).map(PList::fromIter).orElseGet(PList::empty);
  }
}
