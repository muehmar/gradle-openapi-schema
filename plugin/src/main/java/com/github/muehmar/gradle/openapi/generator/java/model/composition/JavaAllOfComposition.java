package com.github.muehmar.gradle.openapi.generator.java.model.composition;

import static com.github.muehmar.gradle.openapi.generator.java.model.composition.Assertion.assertAllObjectPojos;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.PojoType;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.model.composition.AllOfComposition;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class JavaAllOfComposition {
  private final NonEmptyList<JavaObjectPojo> pojos;

  private JavaAllOfComposition(NonEmptyList<JavaPojo> pojos) {
    this.pojos = assertAllObjectPojos(pojos);
  }

  public static JavaAllOfComposition wrap(
      AllOfComposition allOfComposition, PojoType type, TypeMappings typeMappings) {
    return new JavaAllOfComposition(
        allOfComposition
            .getPojos()
            .map(pojo -> JavaPojo.wrap(pojo, typeMappings))
            .map(result -> result.getTypeOrDefault(type)));
  }

  public static JavaAllOfComposition fromPojos(NonEmptyList<JavaPojo> pojos) {
    return new JavaAllOfComposition(pojos);
  }

  public NonEmptyList<JavaObjectPojo> getPojos() {
    return pojos;
  }

  public PList<JavaPojoMember> getMembers() {
    return pojos
        .toPList()
        .flatMap(JavaObjectPojo::getAllMembersForComposition)
        .map(JavaPojoMember::asAllOfMember);
  }
}
