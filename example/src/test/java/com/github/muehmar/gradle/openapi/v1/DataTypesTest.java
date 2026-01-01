package com.github.muehmar.gradle.openapi.v1;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingSupplier;

class DataTypesTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  private static final DataTypesDto DTO =
      DataTypesDto.builder()
          .andAllOptionals()
          .setString("string")
          .setDate(LocalDate.of(2018, 12, 30))
          .setDateTime(
              ZonedDateTime.of(
                  2021, 9, 21, 9, 36, 0, 0, ZoneId.ofOffset("", ZoneOffset.ofHours(2))))
          .setTime(LocalTime.of(12, 32, 15))
          .setByte("bytes")
          .setBinary(new byte[] {0x15, 0x47})
          .setEmail("mail@mail.ch")
          .setUuid(UUID.fromString("12263dbb-1a07-4dfa-822c-f3e58dc6a420"))
          .setUri(URI.create("github.com"))
          .setUrl(wrapCheckedException(() -> new URL("https://github.com")))
          .setDefaultInt(24)
          .setInt32(26)
          .setInt64(64L)
          .setDefaultNumber(25.75f)
          .setFloat(87.25f)
          .setDouble(125.5)
          .setBool(true)
          .setData("data")
          .build();

  private static final String JSON =
      "{\"binary\":\"FUc=\",\"bool\":true,\"byte\":\"bytes\",\"data\":\"data\",\"date\":\"2018-12-30\",\"dateTime\":\"2021-09-21T09:36:00+02:00\",\"defaultInt\":24,\"defaultNumber\":25.75,\"double\":125.5,\"email\":\"mail@mail.ch\",\"float\":87.25,\"int32\":26,\"int64\":64,\"string\":\"string\",\"time\":\"12:32:15\",\"uri\":\"github.com\",\"url\":\"https://github.com\",\"uuid\":\"12263dbb-1a07-4dfa-822c-f3e58dc6a420\"}";

  @Test
  void writeValueAsString_when_dto_then_correctJson() throws Exception {
    assertEquals(JSON, MAPPER.writeValueAsString(DTO));
  }

  @Test
  void readValue_when_json_then_correctDto() throws Exception {
    final DataTypesDto deserializedDto = MAPPER.readValue(JSON, DataTypesDto.class);

    assertEquals(DTO, deserializedDto);
  }

  private static <A> A wrapCheckedException(ThrowingSupplier<A> a) {
    try {
      return a.get();
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }
}
