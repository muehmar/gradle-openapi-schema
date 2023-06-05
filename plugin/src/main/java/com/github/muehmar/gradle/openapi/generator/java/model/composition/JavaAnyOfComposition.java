package com.github.muehmar.gradle.openapi.generator.java.model.composition;

import static com.github.muehmar.gradle.openapi.generator.java.model.composition.Assertion.assertAllObjectPojos;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.PojoType;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.model.composition.AnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class JavaAnyOfComposition {
  private final PList<JavaObjectPojo> pojos;

  private JavaAnyOfComposition(PList<JavaPojo> pojos) {
    this.pojos = assertAllObjectPojos(pojos);
  }

  public static JavaAnyOfComposition wrap(
      AnyOfComposition anyOfComposition, PojoType type, TypeMappings typeMappings) {
    return new JavaAnyOfComposition(
        anyOfComposition
            .getPojos()
            .map(pojo -> JavaPojo.wrap(pojo, typeMappings))
            .map(result -> result.getTypeOrDefault(type)));
  }

  public static JavaAnyOfComposition fromPojos(PList<JavaPojo> pojos) {
    return new JavaAnyOfComposition(pojos);
  }

  public PList<JavaObjectPojo> getPojos() {
    return pojos;
  }
}
