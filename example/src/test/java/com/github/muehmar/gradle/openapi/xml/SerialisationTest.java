package com.github.muehmar.gradle.openapi.xml;

import static com.github.muehmar.gradle.openapi.xml.BookDto.fullBookDtoBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SerialisationTest {
  public static final BookDto BOOK_DTO =
      fullBookDtoBuilder()
          .setId(123456)
          .setTitle("The Hitchhiker's Guide to the Galaxy")
          .setAuthor("Douglas Adams")
          .build();
  private static final XmlMapper XML_MAPPER = new XmlMapper();
  public static final String XML =
      "<xml-book><author>Douglas Adams</author><id>123456</id><title>The Hitchhiker's Guide to the Galaxy</title></xml-book>";

  @BeforeAll
  static void setupMapper() {
    XML_MAPPER.setConfig(
        XML_MAPPER.getSerializationConfig().with(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY));
  }

  @Test
  void serialize_when_bookDto_then_correctXml() throws JsonProcessingException {
    final String xml = XML_MAPPER.writeValueAsString(BOOK_DTO);
    assertEquals(XML, xml);
  }

  @Test
  void deserialize_when_xml_then_correctBookDto() throws JsonProcessingException {
    final BookDto bookDto = XML_MAPPER.readValue(XML, BookDto.class);
    assertEquals(BOOK_DTO, bookDto);
  }
}
