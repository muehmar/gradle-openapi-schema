package com.github.muehmar.gradle.openapi.util;

/**
 * Abstraction over Jackson ObjectMapper to allow testing with different Jackson versions. This
 * interface has no Jackson dependencies to keep test code version-agnostic.
 */
public interface JsonMapper {

  /**
   * Serialize an object to JSON string.
   *
   * @param value the object to serialize
   * @return JSON string representation
   * @throws Exception if serialization fails
   */
  String writeValueAsString(Object value) throws Exception;

  /**
   * Deserialize JSON string to an object.
   *
   * @param content the JSON string
   * @param valueType the target class type
   * @param <T> the type parameter
   * @return the deserialized object
   * @throws Exception if deserialization fails
   */
  <T> T readValue(String content, Class<T> valueType) throws Exception;
}
