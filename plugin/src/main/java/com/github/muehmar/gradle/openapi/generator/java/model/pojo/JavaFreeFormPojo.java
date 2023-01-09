package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.pojo.FreeFormPojo;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class JavaFreeFormPojo implements JavaPojo {
  private final PojoName name;
  private final String description;

  private JavaFreeFormPojo(PojoName name, String description) {
    this.name = name;
    this.description = description;
  }

  public static JavaFreeFormPojo wrap(FreeFormPojo freeFormPojo) {
    return new JavaFreeFormPojo(freeFormPojo.getName(), freeFormPojo.getDescription());
  }

  @Override
  public PojoName getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public <T> T fold(
      Function<JavaArrayPojo, T> onArrayPojo,
      Function<JavaEnumPojo, T> onEnumPojo,
      Function<JavaObjectPojo, T> onObjectPojo,
      Function<JavaComposedPojo, T> onComposedPojo,
      Function<JavaFreeFormPojo, T> onFreeFormPojo) {
    return onFreeFormPojo.apply(this);
  }
}
