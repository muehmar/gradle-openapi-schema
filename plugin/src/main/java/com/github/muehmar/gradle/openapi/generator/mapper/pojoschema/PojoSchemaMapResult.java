package com.github.muehmar.gradle.openapi.generator.mapper.pojoschema;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMemberReference;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import io.github.muehmar.pojoextension.annotations.SafeBuilder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@SafeBuilder(packagePrivateBuilder = true)
@EqualsAndHashCode
@ToString
public class PojoSchemaMapResult {
  private final PList<Pojo> pojos;
  private final PList<ComposedPojo> composedPojos;
  private final PList<PojoMemberReference> pojoMemberReferences;
  private final PList<OpenApiSpec> specifications;

  public PojoSchemaMapResult(
      PList<Pojo> pojos,
      PList<ComposedPojo> composedPojos,
      PList<PojoMemberReference> pojoMemberReferences,
      PList<OpenApiSpec> specifications) {
    this.pojos = pojos;
    this.composedPojos = composedPojos;
    this.pojoMemberReferences = pojoMemberReferences;
    this.specifications = specifications;
  }

  public static PojoSchemaMapResult empty() {
    return new PojoSchemaMapResult(PList.empty(), PList.empty(), PList.empty(), PList.empty());
  }

  public static PojoSchemaMapResult ofPojo(Pojo pojo) {
    return empty().addPojo(pojo);
  }

  public static PojoSchemaMapResult ofComposedPojo(ComposedPojo composedPojo) {
    return empty().addComposedPojo(composedPojo);
  }

  public PList<Pojo> getPojos() {
    return pojos;
  }

  public PList<ComposedPojo> getComposedPojos() {
    return composedPojos;
  }

  public PojoSchemaMapResult concat(PojoSchemaMapResult other) {
    return new PojoSchemaMapResult(
        pojos.concat(other.pojos),
        composedPojos.concat(other.composedPojos),
        pojoMemberReferences.concat(other.pojoMemberReferences),
        specifications.concat(other.specifications));
  }

  public PojoSchemaMapResult addComposedPojo(ComposedPojo composedPojo) {
    return new PojoSchemaMapResult(
        pojos, composedPojos.cons(composedPojo), pojoMemberReferences, specifications);
  }

  public PojoSchemaMapResult addPojo(Pojo pojo) {
    return new PojoSchemaMapResult(
        pojos.cons(pojo), composedPojos, pojoMemberReferences, specifications);
  }
}
