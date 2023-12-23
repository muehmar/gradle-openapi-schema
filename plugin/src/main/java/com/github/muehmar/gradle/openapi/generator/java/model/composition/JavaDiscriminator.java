package com.github.muehmar.gradle.openapi.generator.java.model.composition;

import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.model.composition.Discriminator;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class JavaDiscriminator {
  private final Discriminator discriminator;

  private JavaDiscriminator(Discriminator discriminator) {
    this.discriminator = discriminator;
  }

  public static JavaDiscriminator wrap(Discriminator discriminator) {
    return new JavaDiscriminator(discriminator);
  }

  public JavaName getPropertyName() {
    return JavaName.fromName(discriminator.getPropertyName());
  }

  public String getValueForSchemaName(Name schemaName) {
    return discriminator.getValueForSchemaName(schemaName);
  }
}
