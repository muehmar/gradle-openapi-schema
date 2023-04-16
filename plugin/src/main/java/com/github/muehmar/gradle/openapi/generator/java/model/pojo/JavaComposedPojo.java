package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import static com.github.muehmar.gradle.openapi.generator.model.pojo.ComposedPojo.CompositionType.ANY_OF;
import static com.github.muehmar.gradle.openapi.generator.model.pojo.ComposedPojo.CompositionType.ONE_OF;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoName;
import com.github.muehmar.gradle.openapi.generator.java.model.PojoType;
import com.github.muehmar.gradle.openapi.generator.model.Discriminator;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class JavaComposedPojo implements JavaPojo {
  private final JavaPojoName name;
  private final String description;
  private final PList<JavaPojo> javaPojos;
  private final ComposedPojo.CompositionType compositionType;
  private final Constraints constraints;
  private final Optional<Discriminator> discriminator;

  JavaComposedPojo(
      JavaPojoName name,
      String description,
      PList<JavaPojo> javaPojos,
      ComposedPojo.CompositionType compositionType,
      Constraints constraints,
      Optional<Discriminator> discriminator) {
    assertPropertiesHaveNotSameNameAndDifferentAttributes(javaPojos);
    this.name = name;
    this.description = Optional.ofNullable(description).orElse("");
    this.javaPojos = javaPojos;
    this.compositionType = compositionType;
    this.constraints = constraints;
    this.discriminator = discriminator;
  }

  private static void assertPropertiesHaveNotSameNameAndDifferentAttributes(
      PList<JavaPojo> javaPojos) {
    final PList<JavaPojoMember> allMembers = javaPojos.flatMap(JavaPojo::getMembersOrEmpty);
    final PList<JavaPojoMember> invalidMembers =
        allMembers
            .filter(
                member1 ->
                    allMembers.exists(member2 -> sameNameButDifferentAttributes(member1, member2)))
            .duplicates(Comparator.comparing(m -> m.getName().asString()));
    if (invalidMembers.nonEmpty()) {
      throw new IllegalArgumentException(
          String.format(
              "For schema compositions (oneOf, anyOf), two schemas must not have properties with same name but different attributes (e.g. type, constraints)! Invalid properties: [%s].",
              invalidMembers.map(JavaPojoMember::getName).mkString(", ")));
    }
  }

  private static boolean sameNameButDifferentAttributes(JavaPojoMember m1, JavaPojoMember m2) {
    return m1.getName().equals(m2.getName()) && not(m1.equals(m2));
  }

  public static NonEmptyList<JavaComposedPojo> wrap(
      ComposedPojo composedPojo, TypeMappings typeMappings) {
    return NonEmptyList.single(
        new JavaComposedPojo(
            JavaPojoName.wrap(composedPojo.getName()),
            composedPojo.getDescription(),
            composedPojo
                .getPojos()
                .flatMap(pojo -> JavaPojo.wrap(pojo, typeMappings))
                .map(pojo -> pojo),
            composedPojo.getCompositionType(),
            composedPojo.getConstraints(),
            composedPojo.getDiscriminator()));
  }

  @Override
  public JavaName getSchemaName() {
    return JavaName.fromName(name.getSchemaName());
  }

  @Override
  public JavaIdentifier getClassName() {
    return name.asJavaName().asIdentifier();
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public PojoType getType() {
    return PojoType.DEFAULT;
  }

  public PList<JavaPojo> getJavaPojos() {
    return javaPojos;
  }

  public ComposedPojo.CompositionType getCompositionType() {
    return compositionType;
  }

  public boolean isAnyOf() {
    return compositionType.equals(ANY_OF);
  }

  public boolean isOneOf() {
    return compositionType.equals(ONE_OF);
  }

  public Optional<Discriminator> getDiscriminator() {
    return discriminator;
  }

  public JavaObjectPojo wrapIntoJavaObjectPojo() {
    return JavaObjectPojo.from(name, description, getMembers(), constraints);
  }

  public PList<JavaPojoMember> getMembers() {
    return javaPojos.flatMap(JavaPojo::getMembersOrEmpty).distinct(Function.identity());
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
