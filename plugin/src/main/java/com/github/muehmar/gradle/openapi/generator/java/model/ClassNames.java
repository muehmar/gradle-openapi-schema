package com.github.muehmar.gradle.openapi.generator.java.model;

import com.github.muehmar.gradle.openapi.generator.model.Name;

public class ClassNames {
  private ClassNames() {}

  public static final ClassName OBJECT =
      ClassName.ofPackageAndName(PackageNames.JAVA_LANG, Name.of("Object"));
  public static final ClassName BOOLEAN =
      ClassName.ofPackageAndName(PackageNames.JAVA_LANG, Name.of("Boolean"));
  public static final ClassName BYTE =
      ClassName.ofPackageAndName(PackageNames.JAVA_LANG, Name.of("Byte"));
  public static final ClassName STRING =
      ClassName.ofPackageAndName(PackageNames.JAVA_LANG, Name.of("String"));
  public static final ClassName INTEGER =
      ClassName.ofPackageAndName(PackageNames.JAVA_LANG, Name.of("Integer"));
  public static final ClassName LONG =
      ClassName.ofPackageAndName(PackageNames.JAVA_LANG, Name.of("Long"));
  public static final ClassName FLOAT =
      ClassName.ofPackageAndName(PackageNames.JAVA_LANG, Name.of("Float"));
  public static final ClassName DOUBLE =
      ClassName.ofPackageAndName(PackageNames.JAVA_LANG, Name.of("Double"));
  public static final ClassName BYTE_ARRAY =
      ClassName.ofPackageAndName(PackageNames.JAVA_LANG, Name.of("byte[]"));
  public static final ClassName LOCAL_DATE =
      ClassName.ofPackageAndName(PackageNames.JAVA_TIME, Name.of("LocalDate"));
  public static final ClassName LOCAL_TIME =
      ClassName.ofPackageAndName(PackageNames.JAVA_TIME, Name.of("LocalTime"));
  public static final ClassName LOCAL_DATE_TIME =
      ClassName.ofPackageAndName(PackageNames.JAVA_TIME, Name.of("LocalDateTime"));
  public static final ClassName URI =
      ClassName.ofPackageAndName(PackageNames.JAVA_NET, Name.of("URI"));
  public static final ClassName URL =
      ClassName.ofPackageAndName(PackageNames.JAVA_NET, Name.of("URL"));
  public static final ClassName UUID =
      ClassName.ofPackageAndName(PackageNames.JAVA_UTIL, Name.of("UUID"));
  public static final ClassName LIST =
      ClassName.ofPackageAndName(PackageNames.JAVA_UTIL, Name.of("List"));
  public static final ClassName MAP =
      ClassName.ofPackageAndName(PackageNames.JAVA_UTIL, Name.of("Map"));
}
