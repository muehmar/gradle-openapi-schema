package com.github.muehmar.gradle.openapi.xml;

import static com.github.muehmar.gradle.openapi.xml.ContainerArrayDto.fullContainerArrayDtoBuilder;
import static com.github.muehmar.gradle.openapi.xml.ContainerStandardArrayDto.fullContainerStandardArrayDtoBuilder;
import static com.github.muehmar.gradle.openapi.xml.ContainerWrappedArrayDto.fullContainerWrappedArrayDtoBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.gradle.openapi.util.XmlMapper;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class ArraySerialisationTest {
  private static final XmlMapper XML_MAPPER = MapperFactory.xmlMapper();

  private static final ContainerStandardArrayDto CONTAINER_STANDARD_ARRAY_DTO =
      fullContainerStandardArrayDtoBuilder().setBooks(Arrays.asList("book1", "book2")).build();
  private static final String CONTAINER_STANDARD_ARRAY_XML =
      "<ContainerStandardArray><books><books>book1</books><books>book2</books></books></ContainerStandardArray>";

  private static final ContainerArrayDto CONTAINER_ARRAY_DTO =
      fullContainerArrayDtoBuilder().setBooks(Arrays.asList("book1", "book2")).build();
  private static final String CONTAINER_ARRAY_XML =
      "<ContainerArray><books>book1</books><books>book2</books></ContainerArray>";

  private static final ContainerWrappedArrayDto CONTAINER_WRAPPED_ARRAY_DTO =
      fullContainerWrappedArrayDtoBuilder().setBooks(Arrays.asList("book1", "book2")).build();
  private static final String CONTAINER_WRAPPED_ARRAY_XML =
      "<ContainerWrappedArray><book-array><item>book1</item><item>book2</item></book-array></ContainerWrappedArray>";

  @Test
  void serialize_when_containerArrayDto_then_matchXml() throws Exception {
    final String xml = XML_MAPPER.writeValueAsString(CONTAINER_ARRAY_DTO);
    assertEquals(CONTAINER_ARRAY_XML, xml);
  }

  @Test
  void deserialize_when_containerArrayDto_then_matchDto() throws Exception {
    final ContainerArrayDto containerArrayDto =
        XML_MAPPER.readValue(CONTAINER_ARRAY_XML, ContainerArrayDto.class);
    assertEquals(CONTAINER_ARRAY_DTO, containerArrayDto);
  }

  @Test
  void serialize_when_containerWrappedArrayDto_then_matchXml() throws Exception {
    final String xml = XML_MAPPER.writeValueAsString(CONTAINER_WRAPPED_ARRAY_DTO);
    assertEquals(CONTAINER_WRAPPED_ARRAY_XML, xml);
  }

  @Test
  void deserialize_when_containerWrappedArrayDto_then_matchDto() throws Exception {
    final ContainerWrappedArrayDto containerWrappedArrayDto =
        XML_MAPPER.readValue(CONTAINER_WRAPPED_ARRAY_XML, ContainerWrappedArrayDto.class);
    assertEquals(CONTAINER_WRAPPED_ARRAY_DTO, containerWrappedArrayDto);
  }

  @Test
  void serialize_when_containerStandardArrayDto_then_matchXml() throws Exception {

    final String xml = XML_MAPPER.writeValueAsString(CONTAINER_STANDARD_ARRAY_DTO);
    assertEquals(CONTAINER_STANDARD_ARRAY_XML, xml);
  }

  @Test
  void deserialize_when_containerStandardArrayDto_then_matchDto() throws Exception {
    final ContainerStandardArrayDto containerStandardArrayDto =
        XML_MAPPER.readValue(CONTAINER_STANDARD_ARRAY_XML, ContainerStandardArrayDto.class);
    assertEquals(CONTAINER_STANDARD_ARRAY_DTO, containerStandardArrayDto);
  }
}
