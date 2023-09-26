package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static com.github.muehmar.gradle.openapi.generator.model.name.PojoNames.pojoName;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.type.ArrayType;
import com.github.muehmar.gradle.openapi.generator.model.type.MapType;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import org.junit.jupiter.api.Test;

class JavaTypeTest {
  @Test
  void getImportsAsString_when_calledForMapType_then_noJavaLangImportsOrClassesFromSamePackage() {
    final MapType mapType =
        MapType.ofKeyAndValueType(
            StringType.noFormat(),
            ArrayType.ofItemType(ObjectType.ofName(pojoName("Object", "Dto"))));
    final JavaMapType javaMapType = JavaMapType.wrap(mapType, TypeMappings.empty());

    assertEquals(PList.of("java.util.Map", "java.util.List"), javaMapType.getImportsAsString());
  }
}
