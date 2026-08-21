package com.github.muehmar.gradle.openapi.util;

import java.util.ServiceLoader;

/**
 * Factory for creating JSON and XML mappers. Uses ServiceLoader to dynamically load the appropriate
 * Jackson implementation based on the classpath, allowing tests to run with different Jackson
 * versions.
 */
public class MapperFactory {

  private static final JsonMapper JSON_MAPPER = loadJsonMapper();
  private static final XmlMapper XML_MAPPER = loadXmlMapper();

  private MapperFactory() {}

  /**
   * Get the JSON mapper instance. This is the primary method for test code to use.
   *
   * @return the JSON mapper abstraction
   */
  public static JsonMapper jsonMapper() {
    return JSON_MAPPER;
  }

  /**
   * Get the XML mapper instance. Used by XML serialization tests.
   *
   * @return the XML mapper abstraction
   */
  public static XmlMapper xmlMapper() {
    return XML_MAPPER;
  }

  private static JsonMapper loadJsonMapper() {
    ServiceLoader<JsonMapper> loader = ServiceLoader.load(JsonMapper.class);
    return loader
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalStateException(
                    "No JsonMapper implementation found on classpath. "
                        + "Make sure testFixturesJackson2 or testFixturesJackson3 is on the classpath."));
  }

  private static XmlMapper loadXmlMapper() {
    ServiceLoader<XmlMapper> loader = ServiceLoader.load(XmlMapper.class);
    return loader
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalStateException(
                    "No XmlMapper implementation found on classpath. "
                        + "Make sure testFixturesJackson2 or testFixturesJackson3 is on the classpath."));
  }
}
