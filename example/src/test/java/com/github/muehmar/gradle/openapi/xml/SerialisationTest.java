package com.github.muehmar.gradle.openapi.xml;

import static com.github.muehmar.gradle.openapi.xml.BookDto.fullBookDtoBuilder;
import static com.github.muehmar.gradle.openapi.xml.BookProp1Dto.fullBookProp1DtoBuilder;
import static com.github.muehmar.gradle.openapi.xml.BookProp2Dto.fullBookProp2DtoBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SerialisationTest {
  private static final XmlMapper XML_MAPPER = new XmlMapper();

  private static final BookDto BOOK_DTO =
      fullBookDtoBuilder()
          .setId(123456)
          .setTitle("The Hitchhiker's Guide to the Galaxy")
          .setAuthor("Douglas Adams")
          .build();

  private static final String BOOK_XML =
      "<xml-book><author>Douglas Adams</author><id>123456</id><title>The Hitchhiker's Guide to the Galaxy</title></xml-book>";

  private static final BookProp1Dto BOOK_PROP1_DTO =
      fullBookProp1DtoBuilder()
          .setId(123456)
          .setTitle("The Hitchhiker's Guide to the Galaxy")
          .setAuthor("Douglas Adams")
          .build();

  private static final String BOOK_PROP1_XML =
      "<BookProp1 xml-title=\"The Hitchhiker's Guide to the Galaxy\"><author>Douglas Adams</author><id>123456</id></BookProp1>";

  private static final BookProp2Dto BOOK_PROP2_DTO =
      fullBookProp2DtoBuilder()
          .setId(123456)
          .setTitle("The Hitchhiker's Guide to the Galaxy")
          .setAuthor("Douglas Adams")
          .build();

  private static final String BOOK_PROP2_XML =
      "<BookProp2><author>Douglas Adams</author><id>123456</id><xml-title>The Hitchhiker's Guide to the Galaxy</xml-title></BookProp2>";

  @BeforeAll
  static void setupMapper() {
    XML_MAPPER.setConfig(
        XML_MAPPER.getSerializationConfig().with(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY));
  }

  @Test
  void serialize_when_bookDto_then_correctXml() throws JsonProcessingException {
    final String xml = XML_MAPPER.writeValueAsString(BOOK_DTO);
    assertEquals(BOOK_XML, xml);
  }

  @Test
  void deserialize_when_bookXml_then_correctBookDto() throws JsonProcessingException {
    final BookDto bookDto = XML_MAPPER.readValue(BOOK_XML, BookDto.class);
    assertEquals(BOOK_DTO, bookDto);
  }

  @Test
  void serialize_when_bookProp1Dto_then_correctXml() throws JsonProcessingException {
    final String xml = XML_MAPPER.writeValueAsString(BOOK_PROP1_DTO);
    assertEquals(BOOK_PROP1_XML, xml);
  }

  @Test
  void deserialize_when_bookProp1Xml_then_correctBookPropDto() throws JsonProcessingException {
    final BookProp1Dto bookPropDto = XML_MAPPER.readValue(BOOK_PROP1_XML, BookProp1Dto.class);
    assertEquals(BOOK_PROP1_DTO, bookPropDto);
  }

  @Test
  void serialize_when_bookProp2Dto_then_correctXml() throws JsonProcessingException {
    final String xml = XML_MAPPER.writeValueAsString(BOOK_PROP2_DTO);
    assertEquals(BOOK_PROP2_XML, xml);
  }

  @Test
  void deserialize_when_bookProp2Xml_then_correctBookPropDto() throws JsonProcessingException {
    final BookProp2Dto bookPropDto = XML_MAPPER.readValue(BOOK_PROP2_XML, BookProp2Dto.class);
    assertEquals(BOOK_PROP2_DTO, bookPropDto);
  }
}
