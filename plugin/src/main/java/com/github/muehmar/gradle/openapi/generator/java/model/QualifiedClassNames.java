package com.github.muehmar.gradle.openapi.generator.java.model;

import com.github.muehmar.gradle.openapi.generator.model.name.Name;

public class QualifiedClassNames {
  private QualifiedClassNames() {}

  public static final QualifiedClassName OBJECT =
      QualifiedClassName.ofPackageAndName(PackageNames.JAVA_LANG, Name.ofString("Object"));
  public static final QualifiedClassName BOOLEAN =
      QualifiedClassName.ofPackageAndName(PackageNames.JAVA_LANG, Name.ofString("Boolean"));
  public static final QualifiedClassName BYTE =
      QualifiedClassName.ofPackageAndName(PackageNames.JAVA_LANG, Name.ofString("Byte"));
  public static final QualifiedClassName SHORT =
      QualifiedClassName.ofPackageAndName(PackageNames.JAVA_LANG, Name.ofString("Short"));
  public static final QualifiedClassName CHARACTER =
      QualifiedClassName.ofPackageAndName(PackageNames.JAVA_LANG, Name.ofString("Character"));
  public static final QualifiedClassName STRING =
      QualifiedClassName.ofPackageAndName(PackageNames.JAVA_LANG, Name.ofString("String"));
  public static final QualifiedClassName INTEGER =
      QualifiedClassName.ofPackageAndName(PackageNames.JAVA_LANG, Name.ofString("Integer"));
  public static final QualifiedClassName LONG =
      QualifiedClassName.ofPackageAndName(PackageNames.JAVA_LANG, Name.ofString("Long"));
  public static final QualifiedClassName FLOAT =
      QualifiedClassName.ofPackageAndName(PackageNames.JAVA_LANG, Name.ofString("Float"));
  public static final QualifiedClassName DOUBLE =
      QualifiedClassName.ofPackageAndName(PackageNames.JAVA_LANG, Name.ofString("Double"));
  public static final QualifiedClassName BYTE_ARRAY =
      QualifiedClassName.ofPackageAndName(PackageNames.JAVA_LANG, Name.ofString("byte[]"));
  public static final QualifiedClassName LOCAL_DATE =
      QualifiedClassName.ofPackageAndName(PackageNames.JAVA_TIME, Name.ofString("LocalDate"));
  public static final QualifiedClassName LOCAL_TIME =
      QualifiedClassName.ofPackageAndName(PackageNames.JAVA_TIME, Name.ofString("LocalTime"));
  public static final QualifiedClassName LOCAL_DATE_TIME =
      QualifiedClassName.ofPackageAndName(PackageNames.JAVA_TIME, Name.ofString("LocalDateTime"));
  public static final QualifiedClassName URI =
      QualifiedClassName.ofPackageAndName(PackageNames.JAVA_NET, Name.ofString("URI"));
  public static final QualifiedClassName URL =
      QualifiedClassName.ofPackageAndName(PackageNames.JAVA_NET, Name.ofString("URL"));
  public static final QualifiedClassName UUID =
      QualifiedClassName.ofPackageAndName(PackageNames.JAVA_UTIL, Name.ofString("UUID"));
  public static final QualifiedClassName LIST =
      QualifiedClassName.ofPackageAndName(PackageNames.JAVA_UTIL, Name.ofString("List"));
  public static final QualifiedClassName ARRAY_LIST =
      QualifiedClassName.ofPackageAndName(PackageNames.JAVA_UTIL, Name.ofString("ArrayList"));
  public static final QualifiedClassName LINKED_LIST =
      QualifiedClassName.ofPackageAndName(PackageNames.JAVA_UTIL, Name.ofString("LinkedList"));
  public static final QualifiedClassName MAP =
      QualifiedClassName.ofPackageAndName(PackageNames.JAVA_UTIL, Name.ofString("Map"));

  public static final QualifiedClassName DOUBLE_PRIMITIVE = QualifiedClassName.ofName("double");
  public static final QualifiedClassName FLOAT_PRIMITIVE = QualifiedClassName.ofName("float");
  public static final QualifiedClassName LONG_PRIMITIVE = QualifiedClassName.ofName("long");
  public static final QualifiedClassName INTEGER_PRIMITIVE = QualifiedClassName.ofName("int");
  public static final QualifiedClassName BYTE_PRIMITIVE = QualifiedClassName.ofName("byte");
  public static final QualifiedClassName SHORT_PRIMITIVE = QualifiedClassName.ofName("short");
  public static final QualifiedClassName BOOLEAN_PRIMITIVE = QualifiedClassName.ofName("boolean");
  public static final QualifiedClassName CHAR_PRIMITIVE = QualifiedClassName.ofName("char");
}
