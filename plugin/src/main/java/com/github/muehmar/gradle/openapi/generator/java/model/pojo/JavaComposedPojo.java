package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.model.Discriminator;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Optional;
import java.util.function.Function;

public class JavaComposedPojo implements JavaPojo {
  private final PojoName name;
  private final String description;
  private final PList<JavaPojo> javaPojos;
  private final ComposedPojo.CompositionType compositionType;
  private final Constraints constraints;
  private final Optional<Discriminator> discriminator;

  JavaComposedPojo(
      PojoName name,
      String description,
      PList<JavaPojo> javaPojos,
      ComposedPojo.CompositionType compositionType,
      Constraints constraints,
      Optional<Discriminator> discriminator) {
    this.name = name;
    this.description = description;
    this.javaPojos = javaPojos;
    this.compositionType = compositionType;
    this.constraints = constraints;
    this.discriminator = discriminator;
  }

  public static JavaComposedPojo wrap(ComposedPojo composedPojo, TypeMappings typeMappings) {
    return new JavaComposedPojo(
        composedPojo.getName(),
        composedPojo.getDescription(),
        composedPojo.getPojos().map(pojo -> JavaPojo.wrap(pojo, typeMappings)),
        composedPojo.getCompositionType(),
        composedPojo.getConstraints(),
        composedPojo.getDiscriminator());
  }

  @Override
  public PojoName getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  public PList<JavaPojo> getJavaPojos() {
    return javaPojos;
  }

  public JavaObjectPojo wrapIntoJavaObjectPojo() {
    return JavaObjectPojo.from(name, description, getMembers(), constraints);
  }

  public PList<JavaPojoMember> getMembers() {
    return javaPojos.flatMap(JavaPojo::getMembersOrEmpty);
  }

  @Override
  public <T> T fold(
      Function<JavaArrayPojo, T> onArrayPojo,
      Function<JavaEnumPojo, T> onEnumPojo,
      Function<JavaObjectPojo, T> onObjectPojo,
      Function<JavaComposedPojo, T> onComposedPojo,
      Function<JavaFreeFormPojo, T> onFreeFormPojo) {
    return onComposedPojo.apply(this);
  }
}