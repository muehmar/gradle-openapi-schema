package com.github.muehmar.gradle.openapi.generator.mapper.pojoschema;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMemberReference;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import io.github.muehmar.pojoextension.annotations.SafeBuilder;
import java.util.function.BiFunction;
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

  public static PojoSchemaMapResult ofPojoMemberReference(PojoMemberReference pojoMemberReference) {
    return empty().addPojoMemberReference(pojoMemberReference);
  }

  public static PojoSchemaMapResult ofSpecification(OpenApiSpec spec) {
    return PojoSchemaMapResultBuilder.create()
        .pojos(PList.empty())
        .composedPojos(PList.empty())
        .pojoMemberReferences(PList.empty())
        .specifications(PList.single(spec))
        .build();
  }

  public static PojoSchemaMapResult ofSpecifications(PList<OpenApiSpec> specs) {
    return PojoSchemaMapResultBuilder.create()
        .pojos(PList.empty())
        .composedPojos(PList.empty())
        .pojoMemberReferences(PList.empty())
        .specifications(specs)
        .build();
  }

  public PList<Pojo> getPojos() {
    return pojos;
  }

  public PList<ComposedPojo> getComposedPojos() {
    return composedPojos;
  }

  public PList<PojoMemberReference> getPojoMemberReferences() {
    return pojoMemberReferences;
  }

  public PojoSchemaMapResult concat(PojoSchemaMapResult other) {
    return new PojoSchemaMapResult(
        pojos.concat(other.pojos),
        composedPojos.concat(other.composedPojos),
        pojoMemberReferences.concat(other.pojoMemberReferences),
        specifications.concat(other.specifications));
  }

  public PojoSchemaMapResult popSpecification(
      BiFunction<PojoSchemaMapResult, OpenApiSpec, PojoSchemaMapResult> f) {
    return specifications
        .headOption()
        .map(
            spec ->
                f.apply(
                    new PojoSchemaMapResult(
                        pojos, composedPojos, pojoMemberReferences, specifications.tail()),
                    spec))
        .orElse(this);
  }

  public PojoSchemaMapResult addComposedPojo(ComposedPojo composedPojo) {
    return new PojoSchemaMapResult(
        pojos, composedPojos.cons(composedPojo), pojoMemberReferences, specifications);
  }

  public PojoSchemaMapResult addPojo(Pojo pojo) {
    return new PojoSchemaMapResult(
        pojos.cons(pojo), composedPojos, pojoMemberReferences, specifications);
  }

  public PojoSchemaMapResult addPojoMemberReference(PojoMemberReference pojoMemberReference) {
    return new PojoSchemaMapResult(
        pojos, composedPojos, pojoMemberReferences.cons(pojoMemberReference), specifications);
  }

  public PojoSchemaMapResult addSpecifications(PList<OpenApiSpec> specs) {
    return new PojoSchemaMapResult(
        pojos, composedPojos, pojoMemberReferences, specifications.concat(specs));
  }
}
