package com.github.muehmar.gradle.openapi.generator.java.model.type;

import com.github.muehmar.gradle.openapi.generator.java.model.ClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.PackageNames;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
public class JavaBooleanType extends NonGenericJavaType {
  private static final ClassName JAVA_CLASS_NAME =
      ClassName.ofPackageAndName(PackageNames.JAVA_LANG, Name.of("Boolean"));

  private JavaBooleanType(ClassName className) {
    super(className);
  }

  public static JavaBooleanType wrap(TypeMappings typeMappings) {
    final ClassName className =
        JAVA_CLASS_NAME.mapWithClassMappings(typeMappings.getClassTypeMappings());
    return new JavaBooleanType(className);
  }
}
