package com.github.muehmar.gradle.openapi.allof;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import org.junit.jupiter.api.Test;

class AllOfArrayTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void deserialize_when_executed_then_arrayDeserialized() throws Exception {
    final ReportDto report =
        MAPPER.readValue(
            "{\"invoices\":[{\"orderNumber\":1234,\"title\":\"Hello World!\",\"paid\":false}]}",
            ReportDto.class);

    assertEquals(1, report.getInvoices().size());
    assertEquals(1234, report.getInvoices().get(0).getOrderNumber());
    assertEquals("Hello World!", report.getInvoices().get(0).getTitle());
    assertEquals(false, report.getInvoices().get(0).getPaid());
  }
}
