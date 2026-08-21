package com.github.muehmar.gradle.openapi.util;

/**
 * Abstraction over Jackson XmlMapper to allow testing XML serialization with different Jackson
 * versions. This interface has no Jackson dependencies to keep test code version-agnostic.
 */
public interface XmlMapper {

  /**
   * Serialize an object to XML string.
   *
   * @param value the object to serialize
   * @return XML string representation
   * @throws Exception if serialization fails
   */
  String writeValueAsString(Object value) throws Exception;

  /**
   * Deserialize XML string to an object.
   *
   * @param content the XML string
   * @param valueType the target class type
   * @param <T> the type parameter
   * @return the deserialized object
   * @throws Exception if deserialization fails
   */
  <T> T readValue(String content, Class<T> valueType) throws Exception;
}
