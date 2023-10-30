package com.github.muehmar.gradle.openapi.generator.java.model.composition;

import static com.github.muehmar.gradle.openapi.generator.java.model.composition.Assertion.assertAllObjectPojos;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.PojoType;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TechnicalPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.model.Discriminator;
import com.github.muehmar.gradle.openapi.generator.model.composition.OneOfComposition;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class JavaOneOfComposition {
  private final NonEmptyList<JavaObjectPojo> pojos;
  private final Optional<JavaDiscriminator> discriminator;

  JavaOneOfComposition(NonEmptyList<JavaPojo> pojos, Optional<JavaDiscriminator> discriminator) {
    this.pojos = assertAllObjectPojos(pojos);
    this.discriminator = discriminator;
  }

  public static JavaOneOfComposition wrap(
      OneOfComposition oneOfComposition,
      Optional<Discriminator> objectPojoDiscriminator,
      PojoType type,
      TypeMappings typeMappings) {
    final Optional<JavaDiscriminator> javaDiscriminator =
        oneOfComposition
            .determineDiscriminator(objectPojoDiscriminator)
            .map(JavaDiscriminator::wrap);
    return new JavaOneOfComposition(
        oneOfComposition
            .getPojos()
            .map(pojo -> JavaPojo.wrap(pojo, typeMappings))
            .map(result -> result.getTypeOrDefault(type)),
        javaDiscriminator);
  }

  public static JavaOneOfComposition fromPojos(NonEmptyList<JavaPojo> pojos) {
    return new JavaOneOfComposition(pojos, Optional.empty());
  }

  public NonEmptyList<JavaObjectPojo> getPojos() {
    return pojos;
  }

  public PList<JavaPojoMember> getMembers() {
    return pojos
        .toPList()
        .flatMap(JavaObjectPojo::getAllMembersForComposition)
        .map(JavaPojoMember::asOneOfMember)
        .distinct(Function.identity());
  }

  public PList<TechnicalPojoMember> getPojosAsTechnicalMembers() {
    return pojos.toPList().map(TechnicalPojoMember::wrapJavaObjectPojo);
  }

  public Optional<JavaDiscriminator> getDiscriminator() {
    return discriminator;
  }
}
