package com.github.muehmar.gradle.openapi.multitypepoc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class MultiTypeTest {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void readValue_when_superObjectWithStringValue_then_ok() throws JsonProcessingException {
    final String json = "{\"feature\":\"striiiing\",\"hello\":\"hello\"}";
    final SuperObject superObject = MAPPER.readValue(json, SuperObject.class);
    assertEquals(
        "SuperObject{feature=MultiType{stringValue='striiiing', longValue=null, booleanValue=null, list=null, object=null}, hello='hello'}",
        superObject.toString());
  }

  @Test
  void readValue_then_superObjectWithLongValue_then_ok() throws JsonProcessingException {
    final String json = "{\"feature\":123,\"hello\":\"hello\"}";
    final SuperObject superObject = MAPPER.readValue(json, SuperObject.class);
    assertEquals(
        "SuperObject{feature=MultiType{stringValue='null', longValue=123, booleanValue=null, list=null, object=null}, hello='hello'}",
        superObject.toString());
  }

  @Test
  void readValue_then_superObjectWithBooleanValue_then_ok() throws JsonProcessingException {
    final String json = "{\"feature\":true,\"hello\":\"hello\"}";
    final SuperObject superObject = MAPPER.readValue(json, SuperObject.class);
    assertEquals(
        "SuperObject{feature=MultiType{stringValue='null', longValue=null, booleanValue=true, list=null, object=null}, hello='hello'}",
        superObject.toString());
  }

  @Test
  void readValue_then_superObjectWithList_then_ok() throws JsonProcessingException {
    final String json = "{\"feature\":[\"hello\",\"world\"],\"hello\":\"hello\"}";
    final SuperObject superObject = MAPPER.readValue(json, SuperObject.class);
    assertEquals(
        "SuperObject{feature=MultiType{stringValue='null', longValue=null, booleanValue=null, list=[hello, world], object=null}, hello='hello'}",
        superObject.toString());
  }

  @Test
  void readValue_then_superObjectWithSuperObject_then_ok() throws JsonProcessingException {
    final String json =
        "{\"feature\":{\"hello\":\"deepHello\",\"feature\":123},\"hello\":\"hello\"}";
    final SuperObject superObject = MAPPER.readValue(json, SuperObject.class);
    assertEquals(
        "SuperObject{feature=MultiType{stringValue='null', longValue=null, booleanValue=null, list=null, object=SuperObject{feature=MultiType{stringValue='null', longValue=123, booleanValue=null, list=null, object=null}, hello='deepHello'}}, hello='hello'}",
        superObject.toString());
  }

  @Test
  void writeValueAsString_when_superObjectWithStringValue_then_ok() throws JsonProcessingException {
    final SuperObject superObject = new SuperObject(MultiType.fromString("striiiing"), "hello");
    final String json = MAPPER.writeValueAsString(superObject);
    assertEquals("{\"feature\":\"striiiing\",\"hello\":\"hello\"}", json);
  }

  @Test
  void writeValueAsString_when_superObjectWithLongValue_then_ok() throws JsonProcessingException {
    final SuperObject superObject = new SuperObject(MultiType.fromLong(123L), "hello");
    final String json = MAPPER.writeValueAsString(superObject);
    assertEquals("{\"feature\":123,\"hello\":\"hello\"}", json);
  }

  @Test
  void writeValueAsString_when_superObjectWithBooleanValue_then_ok()
      throws JsonProcessingException {
    final SuperObject superObject = new SuperObject(MultiType.fromBoolean(true), "hello");
    final String json = MAPPER.writeValueAsString(superObject);
    assertEquals("{\"feature\":true,\"hello\":\"hello\"}", json);
  }

  @Test
  void writeValueAsString_when_superObjectWithSuperObject_then_ok() throws JsonProcessingException {
    final SuperObject superObject =
        new SuperObject(
            MultiType.fromObject(new SuperObject(MultiType.fromLong(123L), "deepHello")), "hello");
    final String json = MAPPER.writeValueAsString(superObject);
    assertEquals(
        "{\"feature\":{\"feature\":123,\"hello\":\"deepHello\"},\"hello\":\"hello\"}", json);
  }

  @Test
  void writeValueAsString_when_superObjectWithList_then_ok() throws JsonProcessingException {
    final SuperObject superObject =
        new SuperObject(MultiType.fromList(Arrays.asList("hello", "world")), "hello");
    final String json = MAPPER.writeValueAsString(superObject);
    assertEquals("{\"feature\":[\"hello\",\"world\"],\"hello\":\"hello\"}", json);
  }
}
