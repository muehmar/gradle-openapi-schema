package com.github.muehmar.gradle.openapi.issues.issue292;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class Issue292Test {
  private static final ObjectMapper mapper = MapperFactory.mapper();

  @Test
  void deserialize_when_requiredNullableMapWithNullValues_then_wrapsInOptional()
      throws JsonProcessingException {
    final String json = "{\"data\":{\"key1\":\"value1\",\"key2\":null},\"metadata\":\"meta\"}";
    final RequiredNullableMapObjectDto dto =
        mapper.readValue(json, RequiredNullableMapObjectDto.class);

    final Map<String, Optional<String>> data = dto.getData();
    assertEquals(2, data.size());
    assertEquals(Optional.of("value1"), data.get("key1"));
    assertEquals(Optional.empty(), data.get("key2"));
    assertEquals("meta", dto.getMetadata());
  }

  @Test
  void serialize_when_requiredNullableMapBuiltWithBuilder_then_correctJsonOutput()
      throws JsonProcessingException {
    final Map<String, Optional<String>> data = new HashMap<>();
    data.put("key1", Optional.of("value1"));
    data.put("key2", Optional.empty());
    data.put("key3", Optional.of("value3"));

    final RequiredNullableMapObjectDto dto =
        RequiredNullableMapObjectDto.builder().setData_(data).setMetadata("meta").build();

    final String json = mapper.writeValueAsString(dto);

    assertEquals(
        "{\"data\":{\"key1\":\"value1\",\"key2\":null,\"key3\":\"value3\"},\"metadata\":\"meta\"}",
        json);
  }

  // OptionalNullableMapObject tests
  @Test
  void deserialize_when_optionalNullableMapAbsent_then_optionalEmpty()
      throws JsonProcessingException {
    final String json = "{\"id\":\"123\"}";
    final OptionalNullableMapObjectDto dto =
        mapper.readValue(json, OptionalNullableMapObjectDto.class);

    assertEquals("123", dto.getId());
    assertFalse(dto.getDataOpt().isPresent());
  }

  @Test
  void deserialize_when_optionalNullableMapWithNullValues_then_wrapsInOptional()
      throws JsonProcessingException {
    final String json = "{\"id\":\"123\",\"data\":{\"k1\":\"v1\",\"k2\":null}}";
    final OptionalNullableMapObjectDto dto =
        mapper.readValue(json, OptionalNullableMapObjectDto.class);

    assertEquals("123", dto.getId());
    assertTrue(dto.getDataOpt().isPresent());
    final Map<String, Optional<String>> data = dto.getDataOpt().get();
    assertEquals(2, data.size());
    assertEquals(Optional.of("v1"), data.get("k1"));
    assertEquals(Optional.empty(), data.get("k2"));
  }

  @Test
  void serialize_when_optionalNullableMapWithData_then_correctJsonOutput()
      throws JsonProcessingException {
    final Map<String, Optional<String>> data = new HashMap<>();
    data.put("k1", Optional.of("v1"));
    data.put("k2", Optional.empty());

    final OptionalNullableMapObjectDto dto =
        OptionalNullableMapObjectDto.builder()
            .setId("123")
            .andAllOptionals()
            .setData_(Optional.of(data))
            .build();

    final String json = mapper.writeValueAsString(dto);

    assertEquals("{\"data\":{\"k1\":\"v1\",\"k2\":null},\"id\":\"123\"}", json);
  }

  @Test
  void serialize_when_optionalNullableMapAbsent_then_fieldOmitted() throws JsonProcessingException {
    final OptionalNullableMapObjectDto dto =
        OptionalNullableMapObjectDto.builder()
            .setId("123")
            .andAllOptionals()
            .setData_(Optional.empty())
            .build();

    final String json = mapper.writeValueAsString(dto);

    assertEquals("{\"id\":\"123\"}", json);
  }

  // NestedObjectMapObject tests
  @Test
  void deserialize_when_nestedObjectMapWithOptionalFields_then_tristateHandlesAbsent()
      throws JsonProcessingException {
    final String json =
        "{\"nestedMap\":{\"item1\":{\"id\":\"id1\",\"description\":\"desc1\"},\"item2\":{\"id\":\"id2\"}}}";
    final NestedObjectMapObjectDto dto = mapper.readValue(json, NestedObjectMapObjectDto.class);

    final Map<String, NestedValueDto> nestedMap = dto.getNestedMap();
    assertEquals(2, nestedMap.size());

    assertEquals("id1", nestedMap.get("item1").getId());
    final String desc1 =
        nestedMap
            .get("item1")
            .getDescriptionTristate()
            .onValue(v -> v)
            .onNull(() -> "null")
            .onAbsent(() -> "absent");
    assertEquals("desc1", desc1);

    assertEquals("id2", nestedMap.get("item2").getId());
    final String desc2 =
        nestedMap
            .get("item2")
            .getDescriptionTristate()
            .onValue(v -> v)
            .onNull(() -> "null")
            .onAbsent(() -> "absent");
    assertEquals("absent", desc2);
  }

  @Test
  void serialize_when_nestedObjectMapWithOptionalFields_then_correctJsonOutput()
      throws JsonProcessingException {
    final Map<String, NestedValueDto> nestedMap = new HashMap<>();
    nestedMap.put(
        "item1",
        NestedValueDto.builder().setId("id1").andAllOptionals().setDescription("desc1").build());
    nestedMap.put("item2", NestedValueDto.builder().setId("id2").build());

    final NestedObjectMapObjectDto dto =
        NestedObjectMapObjectDto.builder().setNestedMap(nestedMap).build();

    final String json = mapper.writeValueAsString(dto);

    assertEquals(
        "{\"nestedMap\":{\"item2\":{\"id\":\"id2\"},\"item1\":{\"description\":\"desc1\",\"id\":\"id1\"}}}",
        json);
  }

  // IntegerMapObject tests
  @Test
  void deserialize_when_integerMapWithNullValues_then_wrapsInOptional()
      throws JsonProcessingException {
    final String json = "{\"counters\":{\"count1\":100,\"count2\":null,\"count3\":0}}";
    final IntegerMapObjectDto dto = mapper.readValue(json, IntegerMapObjectDto.class);

    final Map<String, Optional<Integer>> counters = dto.getCounters();
    assertEquals(3, counters.size());
    assertEquals(Optional.of(100), counters.get("count1"));
    assertEquals(Optional.empty(), counters.get("count2"));
    assertEquals(Optional.of(0), counters.get("count3"));
  }

  @Test
  void serialize_when_integerMapBuiltWithBuilder_then_correctJsonOutput()
      throws JsonProcessingException {
    final Map<String, Optional<Integer>> counters = new HashMap<>();
    counters.put("count1", Optional.of(100));
    counters.put("count2", Optional.empty());
    counters.put("count3", Optional.of(0));

    final IntegerMapObjectDto dto = IntegerMapObjectDto.builder().setCounters_(counters).build();

    final String json = mapper.writeValueAsString(dto);

    assertEquals("{\"counters\":{\"count1\":100,\"count2\":null,\"count3\":0}}", json);
  }

  // Round-trip tests to ensure serialization and deserialization are consistent
  @Test
  void serializeAndDeserialize_when_requiredNullableMapRoundTrip_then_objectsEqual()
      throws JsonProcessingException {
    final Map<String, Optional<String>> data = new HashMap<>();
    data.put("key1", Optional.of("value1"));
    data.put("key2", Optional.empty());

    final RequiredNullableMapObjectDto original =
        RequiredNullableMapObjectDto.builder().setData_(data).setMetadata("meta").build();

    final String json = mapper.writeValueAsString(original);
    final RequiredNullableMapObjectDto deserialized =
        mapper.readValue(json, RequiredNullableMapObjectDto.class);

    assertEquals(original.getMetadata(), deserialized.getMetadata());
    assertEquals(original.getData(), deserialized.getData());
  }

  @Test
  void serializeAndDeserialize_when_integerMapRoundTrip_then_objectsEqual()
      throws JsonProcessingException {
    final Map<String, Optional<Integer>> counters = new HashMap<>();
    counters.put("count1", Optional.of(100));
    counters.put("count2", Optional.empty());

    final IntegerMapObjectDto original =
        IntegerMapObjectDto.builder().setCounters_(counters).build();

    final String json = mapper.writeValueAsString(original);
    final IntegerMapObjectDto deserialized = mapper.readValue(json, IntegerMapObjectDto.class);

    assertEquals(original.getCounters(), deserialized.getCounters());
  }
}
