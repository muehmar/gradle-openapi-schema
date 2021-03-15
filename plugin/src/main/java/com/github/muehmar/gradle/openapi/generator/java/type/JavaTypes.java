package com.github.muehmar.gradle.openapi.generator.java.type;

public class JavaTypes {
  private JavaTypes() {}

  public static final JavaType OBJECT = JavaType.ofName("Object");

  public static final JavaType BOOLEAN = JavaType.ofName("Boolean");
  public static final JavaType BYTE = JavaType.ofName("Byte");
  public static final JavaType STRING = JavaType.ofName("String");
  public static final JavaType INTEGER = JavaType.ofName("Integer");
  public static final JavaType LONG = JavaType.ofName("Long");
  public static final JavaType FLOAT = JavaType.ofName("Float");
  public static final JavaType DOUBLE = JavaType.ofName("Double");
  public static final JavaType BYTE_ARRAY = JavaType.ofName("byte[]");

  public static final JavaType LOCAL_DATE =
      JavaType.ofNameAndImport("LocalDate", "java.time.LocalDate");
  public static final JavaType LOCAL_TIME =
      JavaType.ofNameAndImport("LocalTime", "java.time.LocalTime");
  public static final JavaType LOCAL_DATE_TIME =
      JavaType.ofNameAndImport("LocalDateTime", "java.time.LocalDateTime");

  public static final JavaType URI = JavaType.ofNameAndImport("URI", "java.net.URI");
  public static final JavaType URL = JavaType.ofNameAndImport("URL", "java.net.URL");

  public static final JavaType UUID = JavaType.ofNameAndImport("UUID", "java.util.UUID");
}
