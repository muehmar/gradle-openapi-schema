package com.github.muehmar.gradle.openapi.multitypepoc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import org.junit.jupiter.api.Test;

class MultiTypeTest {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void readValue_when_superObjectWithStringValue_then_ok() throws JsonProcessingException {
    final String json = "{\"feature\":\"striiiing\",\"hello\":\"hello\"}";
    final SuperObject superObject = MAPPER.readValue(json, SuperObject.class);
    assertEquals(
        "SuperObject{feature=MultiType{stringValue='striiiing', longValue=null, booleanValue=null, object=null}, hello='hello'}",
        superObject.toString());
  }

  @Test
  void readValue_then_superObjectWithLongValue_then_ok() throws JsonProcessingException {
    final String json = "{\"feature\":123,\"hello\":\"hello\"}";
    final SuperObject superObject = MAPPER.readValue(json, SuperObject.class);
    assertEquals(
        "SuperObject{feature=MultiType{stringValue='null', longValue=123, booleanValue=null, object=null}, hello='hello'}",
        superObject.toString());
  }

  @Test
  void readValue_then_superObjectWithBooleanValue_then_ok() throws JsonProcessingException {
    final String json = "{\"feature\":true,\"hello\":\"hello\"}";
    final SuperObject superObject = MAPPER.readValue(json, SuperObject.class);
    assertEquals(
        "SuperObject{feature=MultiType{stringValue='null', longValue=null, booleanValue=true, object=null}, hello='hello'}",
        superObject.toString());
  }

  @Test
  void readValue_then_superObjectWithSuperObject_then_ok() throws JsonProcessingException {
    final String json =
        "{\"feature\":{\"hello\":\"deepHello\",\"feature\":123},\"hello\":\"hello\"}";
    final SuperObject superObject = MAPPER.readValue(json, SuperObject.class);
    assertEquals(
        "SuperObject{feature=MultiType{stringValue='null', longValue=null, booleanValue=null, object=SuperObject{feature=MultiType{stringValue='null', longValue=123, booleanValue=null, object=null}, hello='deepHello'}}, hello='hello'}",
        superObject.toString());
  }
}
