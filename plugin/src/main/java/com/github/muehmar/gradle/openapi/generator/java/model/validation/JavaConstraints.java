package com.github.muehmar.gradle.openapi.generator.java.model.validation;

import static com.github.muehmar.gradle.openapi.generator.java.model.validation.ConstraintType.DECIMAL_MAX;
import static com.github.muehmar.gradle.openapi.generator.java.model.validation.ConstraintType.DECIMAL_MIN;
import static com.github.muehmar.gradle.openapi.generator.java.model.validation.ConstraintType.EMAIL;
import static com.github.muehmar.gradle.openapi.generator.java.model.validation.ConstraintType.MAX;
import static com.github.muehmar.gradle.openapi.generator.java.model.validation.ConstraintType.MIN;
import static com.github.muehmar.gradle.openapi.generator.java.model.validation.ConstraintType.MULTIPLE_OF;
import static com.github.muehmar.gradle.openapi.generator.java.model.validation.ConstraintType.PATTERN;
import static com.github.muehmar.gradle.openapi.generator.java.model.validation.ConstraintType.PROPERTY_COUNT;
import static com.github.muehmar.gradle.openapi.generator.java.model.validation.ConstraintType.SIZE;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassNames;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JavaConstraints {
  private static final Map<QualifiedClassName, PList<ConstraintType>> SUPPORTED_CONSTRAINTS =
      createSupportedConstraints();

  private static Map<QualifiedClassName, PList<ConstraintType>> createSupportedConstraints() {
    final Map<QualifiedClassName, PList<ConstraintType>> map = new HashMap<>();
    map.put(QualifiedClassNames.INTEGER, PList.of(MIN, MAX, MULTIPLE_OF));
    map.put(QualifiedClassNames.LONG, PList.of(MIN, MAX, MULTIPLE_OF));
    map.put(QualifiedClassNames.FLOAT, PList.of(DECIMAL_MIN, DECIMAL_MAX, MULTIPLE_OF));
    map.put(QualifiedClassNames.DOUBLE, PList.of(DECIMAL_MIN, DECIMAL_MAX, MULTIPLE_OF));
    map.put(QualifiedClassNames.STRING, PList.of(PATTERN, EMAIL, SIZE));

    QualifiedClassNames.ALL_LIST_CLASSNAMES.forEach(
        listClassName -> map.put(listClassName, PList.of(SIZE)));

    QualifiedClassNames.ALL_MAP_CLASSNAMES.forEach(
        mapClassName -> map.put(mapClassName, PList.of(SIZE, PROPERTY_COUNT)));

    return Collections.unmodifiableMap(map);
  }

  private JavaConstraints() {}

  public static boolean isSupported(JavaType javaType, ConstraintType constraintType) {
    return isSupported(javaType.getQualifiedClassName(), constraintType);
  }

  private static boolean isSupported(QualifiedClassName className, ConstraintType constraintType) {
    return SUPPORTED_CONSTRAINTS
        .getOrDefault(className, PList.empty())
        .exists(constraintType::equals);
  }
}
