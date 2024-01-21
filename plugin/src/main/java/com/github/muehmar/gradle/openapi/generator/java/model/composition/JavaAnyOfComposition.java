package com.github.muehmar.gradle.openapi.generator.java.model.composition;

import static com.github.muehmar.gradle.openapi.generator.java.model.composition.Assertion.assertAllObjectPojos;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.PojoType;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TechnicalPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaPojoName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.promotion.PromotableMembers;
import com.github.muehmar.gradle.openapi.generator.model.composition.AnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.UntypedDiscriminator;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@EqualsAndHashCode
@ToString
public class JavaAnyOfComposition implements DiscriminatableJavaComposition {
  private final JavaComposition javaComposition;
  private final Optional<JavaDiscriminator> discriminator;

  JavaAnyOfComposition(JavaComposition javaComposition, Optional<JavaDiscriminator> discriminator) {
    this.javaComposition = javaComposition;
    this.discriminator = discriminator;
  }

  public static JavaAnyOfComposition wrap(
      AnyOfComposition anyOfComposition,
      Optional<UntypedDiscriminator> objectPojoDiscriminator,
      PojoType type,
      TypeMappings typeMappings) {
    final NonEmptyList<JavaPojo> javaPojos =
        anyOfComposition
            .getPojos()
            .map(pojo -> JavaPojo.wrap(pojo, typeMappings))
            .map(result -> result.getTypeOrDefault(type));
    final Optional<JavaDiscriminator> javaDiscriminator =
        anyOfComposition
            .determineDiscriminator(objectPojoDiscriminator)
            .map(JavaDiscriminator::wrap);
    final JavaComposition javaComposition = new JavaComposition(assertAllObjectPojos(javaPojos));
    return new JavaAnyOfComposition(javaComposition, javaDiscriminator);
  }

  public static JavaAnyOfComposition fromPojos(NonEmptyList<JavaPojo> pojos) {
    return new JavaAnyOfComposition(
        new JavaComposition(assertAllObjectPojos(pojos)), Optional.empty());
  }

  @Override
  public NonEmptyList<JavaObjectPojo> getPojos() {
    return javaComposition.getPojos();
  }

  public JavaPojoMembers getMembers() {
    return javaComposition.getMembers(JavaPojoMember::asAnyOfMember);
  }

  public PList<TechnicalPojoMember> getPojosAsTechnicalMembers() {
    return javaComposition.getPojosAsTechnicalMembers();
  }

  public AnyOfCompositionPromotionResult promote(
      JavaPojoName rootName, PromotableMembers promotableMembers) {
    final JavaComposition.CompositionPromotionResult result =
        javaComposition.promote(rootName, promotableMembers::addSubPojo);
    return new AnyOfCompositionPromotionResult(
        new JavaAnyOfComposition(result.getComposition(), discriminator), result.getNewPojos());
  }

  @Override
  public Optional<JavaDiscriminator> getDiscriminator() {
    return discriminator;
  }

  @Override
  public Type getType() {
    return Type.ANY_OF;
  }

  @Value
  public static class AnyOfCompositionPromotionResult {
    JavaAnyOfComposition composition;
    PList<JavaObjectPojo> newPojos;
  }
}
