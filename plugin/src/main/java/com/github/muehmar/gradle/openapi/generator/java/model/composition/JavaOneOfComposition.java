package com.github.muehmar.gradle.openapi.generator.java.model.composition;

import static com.github.muehmar.gradle.openapi.generator.java.model.composition.Assertion.assertAllObjectPojos;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.PojoType;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.model.Discriminator;
import com.github.muehmar.gradle.openapi.generator.model.composition.OneOfComposition;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class JavaOneOfComposition {
  private final PList<JavaObjectPojo> pojos;
  private final Optional<Discriminator> discriminator;

  private JavaOneOfComposition(PList<JavaPojo> pojos, Optional<Discriminator> discriminator) {
    this.pojos = assertAllObjectPojos(pojos);
    this.discriminator = discriminator;
  }

  public static JavaOneOfComposition wrap(
      OneOfComposition oneOfComposition, PojoType type, TypeMappings typeMappings) {
    return new JavaOneOfComposition(
        oneOfComposition
            .getPojos()
            .map(pojo -> JavaPojo.wrap(pojo, typeMappings))
            .map(result -> result.getTypeOrDefault(type)),
        oneOfComposition.getDiscriminator());
  }

  public static JavaOneOfComposition fromPojos(PList<JavaPojo> pojos) {
    return new JavaOneOfComposition(pojos, Optional.empty());
  }

  public static JavaOneOfComposition fromPojosAndDiscriminator(
      PList<JavaPojo> pojos, Discriminator discriminator) {
    return new JavaOneOfComposition(pojos, Optional.of(discriminator));
  }

  public PList<JavaObjectPojo> getPojos() {
    return pojos;
  }
}