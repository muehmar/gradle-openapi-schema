package com.github.muehmar.gradle.openapi.generator.java.model.composition;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.PojoType;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.model.composition.AllOfComposition;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class JavaAllOfComposition {
  private final PList<JavaPojo> pojos;

  private JavaAllOfComposition(PList<JavaPojo> pojos) {
    this.pojos = pojos;
  }

  public static JavaAllOfComposition wrap(
      AllOfComposition allOfComposition, PojoType type, TypeMappings typeMappings) {
    return new JavaAllOfComposition(
        allOfComposition
            .getPojos()
            .map(pojo -> JavaPojo.wrap(pojo, typeMappings))
            .map(result -> result.getTypeOrDefault(type)));
  }

  public static JavaAllOfComposition fromPojos(PList<JavaPojo> pojos) {
    return new JavaAllOfComposition(pojos);
  }

  public PList<JavaPojo> getPojos() {
    return pojos;
  }
}
