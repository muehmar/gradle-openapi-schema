package com.github.muehmar.gradle.openapi.generator.java.model.composition;

import static com.github.muehmar.gradle.openapi.generator.java.model.composition.Assertion.assertAllObjectPojos;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.PojoType;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.model.composition.AllOfComposition;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class JavaAllOfComposition {
  private final JavaComposition javaComposition;

  private JavaAllOfComposition(JavaComposition javaComposition) {
    this.javaComposition = javaComposition;
  }

  public static JavaAllOfComposition wrap(
      AllOfComposition allOfComposition, PojoType type, TypeMappings typeMappings) {
    final NonEmptyList<JavaPojo> javaPojos =
        allOfComposition
            .getPojos()
            .map(pojo -> JavaPojo.wrap(pojo, typeMappings))
            .map(result -> result.getTypeOrDefault(type));
    final JavaComposition javaComposition = new JavaComposition(assertAllObjectPojos(javaPojos));
    return new JavaAllOfComposition(javaComposition);
  }

  public static JavaAllOfComposition fromPojos(NonEmptyList<JavaPojo> pojos) {
    return new JavaAllOfComposition(new JavaComposition(assertAllObjectPojos(pojos)));
  }

  public NonEmptyList<JavaObjectPojo> getPojos() {
    return javaComposition.getPojos();
  }

  public PList<JavaPojoMember> getMembers() {
    return javaComposition.getMembers(JavaPojoMember::asAllOfMember);
  }
}
