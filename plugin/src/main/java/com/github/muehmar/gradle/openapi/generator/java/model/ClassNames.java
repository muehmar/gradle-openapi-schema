package com.github.muehmar.gradle.openapi.generator.java.model;

import com.github.muehmar.gradle.openapi.generator.model.Name;

public class ClassNames {
  private ClassNames() {}

  public static final ClassName OBJECT =
      ClassName.ofPackageAndName(PackageNames.JAVA_LANG, Name.ofString("Object"));
  public static final ClassName BOOLEAN =
      ClassName.ofPackageAndName(PackageNames.JAVA_LANG, Name.ofString("Boolean"));
  public static final ClassName BYTE =
      ClassName.ofPackageAndName(PackageNames.JAVA_LANG, Name.ofString("Byte"));
  public static final ClassName STRING =
      ClassName.ofPackageAndName(PackageNames.JAVA_LANG, Name.ofString("String"));
  public static final ClassName INTEGER =
      ClassName.ofPackageAndName(PackageNames.JAVA_LANG, Name.ofString("Integer"));
  public static final ClassName LONG =
      ClassName.ofPackageAndName(PackageNames.JAVA_LANG, Name.ofString("Long"));
  public static final ClassName FLOAT =
      ClassName.ofPackageAndName(PackageNames.JAVA_LANG, Name.ofString("Float"));
  public static final ClassName DOUBLE =
      ClassName.ofPackageAndName(PackageNames.JAVA_LANG, Name.ofString("Double"));
  public static final ClassName BYTE_ARRAY =
      ClassName.ofPackageAndName(PackageNames.JAVA_LANG, Name.ofString("byte[]"));
  public static final ClassName LOCAL_DATE =
      ClassName.ofPackageAndName(PackageNames.JAVA_TIME, Name.ofString("LocalDate"));
  public static final ClassName LOCAL_TIME =
      ClassName.ofPackageAndName(PackageNames.JAVA_TIME, Name.ofString("LocalTime"));
  public static final ClassName LOCAL_DATE_TIME =
      ClassName.ofPackageAndName(PackageNames.JAVA_TIME, Name.ofString("LocalDateTime"));
  public static final ClassName URI =
      ClassName.ofPackageAndName(PackageNames.JAVA_NET, Name.ofString("URI"));
  public static final ClassName URL =
      ClassName.ofPackageAndName(PackageNames.JAVA_NET, Name.ofString("URL"));
  public static final ClassName UUID =
      ClassName.ofPackageAndName(PackageNames.JAVA_UTIL, Name.ofString("UUID"));
  public static final ClassName LIST =
      ClassName.ofPackageAndName(PackageNames.JAVA_UTIL, Name.ofString("List"));
  public static final ClassName MAP =
      ClassName.ofPackageAndName(PackageNames.JAVA_UTIL, Name.ofString("Map"));
}
