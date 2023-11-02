package com.github.muehmar.gradle.openapi.generator.java.model.composition;

import static com.github.muehmar.gradle.openapi.generator.java.model.composition.Assertion.assertAllObjectPojos;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.PojoType;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TechnicalPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.model.composition.AnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class JavaAnyOfComposition {
  private final JavaComposition javaComposition;

  private JavaAnyOfComposition(JavaComposition javaComposition) {
    this.javaComposition = javaComposition;
  }

  public static JavaAnyOfComposition wrap(
      AnyOfComposition anyOfComposition, PojoType type, TypeMappings typeMappings) {
    final NonEmptyList<JavaPojo> javaPojos =
        anyOfComposition
            .getPojos()
            .map(pojo -> JavaPojo.wrap(pojo, typeMappings))
            .map(result -> result.getTypeOrDefault(type));
    final JavaComposition javaComposition = new JavaComposition(assertAllObjectPojos(javaPojos));
    return new JavaAnyOfComposition(javaComposition);
  }

  public static JavaAnyOfComposition fromPojos(NonEmptyList<JavaPojo> pojos) {
    return new JavaAnyOfComposition(new JavaComposition(assertAllObjectPojos(pojos)));
  }

  public NonEmptyList<JavaObjectPojo> getPojos() {
    return javaComposition.getPojos();
  }

  public PList<JavaPojoMember> getMembers() {
    return javaComposition.getMembers(JavaPojoMember::asAnyOfMember);
  }

  public PList<TechnicalPojoMember> getPojosAsTechnicalMembers() {
    return javaComposition.getPojosAsTechnicalMembers();
  }
}
