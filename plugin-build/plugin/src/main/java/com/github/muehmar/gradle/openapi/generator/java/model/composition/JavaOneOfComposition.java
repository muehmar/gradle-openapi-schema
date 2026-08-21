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
import com.github.muehmar.gradle.openapi.generator.model.composition.OneOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.UntypedDiscriminator;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@EqualsAndHashCode
@ToString
public class JavaOneOfComposition implements DiscriminatableJavaComposition {
  private final JavaComposition javaComposition;
  private final Optional<JavaDiscriminator> discriminator;

  JavaOneOfComposition(JavaComposition javaComposition, Optional<JavaDiscriminator> discriminator) {
    this.javaComposition = javaComposition;
    this.discriminator = discriminator;
  }

  public static JavaOneOfComposition wrap(
      OneOfComposition oneOfComposition,
      Optional<UntypedDiscriminator> objectPojoDiscriminator,
      PojoType type,
      TypeMappings typeMappings) {
    final Optional<JavaDiscriminator> javaDiscriminator =
        oneOfComposition
            .determineDiscriminator(objectPojoDiscriminator)
            .map(JavaDiscriminator::wrap);
    final NonEmptyList<JavaPojo> javaPojos =
        oneOfComposition
            .getPojos()
            .map(pojo -> JavaPojo.wrap(pojo, typeMappings))
            .map(result -> result.getTypeOrDefault(type));
    final JavaComposition javaComposition = new JavaComposition(assertAllObjectPojos(javaPojos));
    return new JavaOneOfComposition(javaComposition, javaDiscriminator);
  }

  public static JavaOneOfComposition fromPojos(NonEmptyList<JavaPojo> pojos) {
    return new JavaOneOfComposition(
        new JavaComposition(assertAllObjectPojos(pojos)), Optional.empty());
  }

  @Override
  public NonEmptyList<JavaObjectPojo> getPojos() {
    return javaComposition.getPojos();
  }

  public JavaPojoMembers getMembers() {
    return javaComposition.getMembers(JavaPojoMember::asOneOfMember);
  }

  @Override
  public PList<TechnicalPojoMember> getPojosAsTechnicalMembers() {
    return javaComposition.getPojosAsTechnicalMembers();
  }

  @Override
  public Optional<JavaDiscriminator> getDiscriminator() {
    return discriminator;
  }

  @Override
  public Type getType() {
    return Type.ONE_OF;
  }

  public OneOfCompositionPromotionResult promote(
      JavaPojoName rootName, PromotableMembers promotableMembers) {
    final JavaComposition.CompositionPromotionResult result =
        javaComposition.promote(rootName, promotableMembers::addSubPojo);
    return new OneOfCompositionPromotionResult(
        new JavaOneOfComposition(result.getComposition(), discriminator), result.getNewPojos());
  }

  @Value
  public static class OneOfCompositionPromotionResult {
    JavaOneOfComposition composition;
    PList<JavaObjectPojo> newPojos;
  }
}
