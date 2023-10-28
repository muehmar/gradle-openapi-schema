package com.github.muehmar.gradle.openapi.generator.java.model.name;

import static com.github.muehmar.gradle.openapi.generator.java.model.PackageNames.JAVA_LANG;
import static com.github.muehmar.gradle.openapi.generator.java.model.PackageNames.JAVA_NET;
import static com.github.muehmar.gradle.openapi.generator.java.model.PackageNames.JAVA_TIME;
import static com.github.muehmar.gradle.openapi.generator.java.model.PackageNames.JAVA_UTIL;
import static com.github.muehmar.gradle.openapi.generator.java.model.PackageNames.JAVA_UTIL_CONCURRENT;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.PackageName;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;

public class QualifiedClassNames {
  private QualifiedClassNames() {}

  public static final QualifiedClassName OBJECT = ofPackageAndName(JAVA_LANG, "Object");
  public static final QualifiedClassName BOOLEAN = ofPackageAndName(JAVA_LANG, "Boolean");
  public static final QualifiedClassName BYTE = ofPackageAndName(JAVA_LANG, "Byte");
  public static final QualifiedClassName SHORT = ofPackageAndName(JAVA_LANG, "Short");
  public static final QualifiedClassName CHARACTER = ofPackageAndName(JAVA_LANG, "Character");
  public static final QualifiedClassName STRING = ofPackageAndName(JAVA_LANG, "String");
  public static final QualifiedClassName INTEGER = ofPackageAndName(JAVA_LANG, "Integer");
  public static final QualifiedClassName LONG = ofPackageAndName(JAVA_LANG, "Long");
  public static final QualifiedClassName FLOAT = ofPackageAndName(JAVA_LANG, "Float");
  public static final QualifiedClassName DOUBLE = ofPackageAndName(JAVA_LANG, "Double");
  public static final QualifiedClassName BYTE_ARRAY = ofPackageAndName(JAVA_LANG, "byte[]");
  public static final QualifiedClassName LOCAL_DATE = ofPackageAndName(JAVA_TIME, "LocalDate");
  public static final QualifiedClassName LOCAL_TIME = ofPackageAndName(JAVA_TIME, "LocalTime");
  public static final QualifiedClassName LOCAL_DATE_TIME =
      ofPackageAndName(JAVA_TIME, "LocalDateTime");
  public static final QualifiedClassName URI = ofPackageAndName(JAVA_NET, "URI");
  public static final QualifiedClassName URL = ofPackageAndName(JAVA_NET, "URL");
  public static final QualifiedClassName UUID = ofPackageAndName(JAVA_UTIL, "UUID");
  public static final QualifiedClassName LIST = ofPackageAndName(JAVA_UTIL, "List");
  public static final QualifiedClassName MAP = ofPackageAndName(JAVA_UTIL, "Map");

  public static final PList<QualifiedClassName> ALL_LIST_CLASSNAMES =
      PList.of(
              "List",
              "AbstractList",
              "AbstractSequentialList",
              "ArrayList",
              "LinkedList",
              "Stack",
              "Vector")
          .map(name -> ofPackageAndName(JAVA_UTIL, name))
          .cons(ofPackageAndName("javax.management", "AttributeList"))
          .cons(ofPackageAndName("javax.management.relation", "RoleList"))
          .cons(ofPackageAndName("javax.management.relation", "RoleUnresolvedList"))
          .cons(ofPackageAndName(JAVA_UTIL_CONCURRENT, "CopyOnWriteArrayList"));

  public static final PList<QualifiedClassName> ALL_MAP_CLASSNAMES =
      PList.of(
              "Map",
              "AbstractMap",
              "EnumMap",
              "HashMap",
              "Hashtable",
              "IdentityHashMap",
              "LinkedHashMap",
              "Properties",
              "TreeMap",
              "WeakHashMap",
              "NavigableMap",
              "SequencedMap",
              "SortedMap")
          .map(name -> ofPackageAndName(JAVA_UTIL, name))
          .cons(ofPackageAndName("java.util.jar", "Attributes"))
          .cons(ofPackageAndName(JAVA_UTIL_CONCURRENT, "ConcurrentHashMap"))
          .cons(ofPackageAndName(JAVA_UTIL_CONCURRENT, "ConcurrentSkipListMap"))
          .cons(ofPackageAndName(JAVA_UTIL_CONCURRENT, "ConcurrentMap"))
          .cons(ofPackageAndName(JAVA_UTIL_CONCURRENT, "ConcurrentNavigableMap"))
          .cons(ofPackageAndName("com.sun.net.httpserver", "Headers"))
          .cons(ofPackageAndName("javax.print.attribute.standard", "PrinterStateReasons"))
          .cons(ofPackageAndName("java.security", "AuthProvider"))
          .cons(ofPackageAndName("java.security", "Provider"))
          .cons(ofPackageAndName("java.awt", "RenderingHints"))
          .cons(ofPackageAndName("javax.management.openmbean", "TabularDataSupport"))
          .cons(ofPackageAndName("javax.swing", "UIDefaults"))
          .cons(ofPackageAndName("javax.script", "SimpleBindings"))
          .cons(ofPackageAndName("javax.script", "Bindings"));

  private static QualifiedClassName ofPackageAndName(PackageName pkg, String name) {
    return QualifiedClassName.ofPackageAndName(pkg, Name.ofString(name));
  }

  private static QualifiedClassName ofPackageAndName(String pkg, String name) {
    return QualifiedClassName.ofPackageAndName(PackageName.ofString(pkg), Name.ofString(name));
  }
}
