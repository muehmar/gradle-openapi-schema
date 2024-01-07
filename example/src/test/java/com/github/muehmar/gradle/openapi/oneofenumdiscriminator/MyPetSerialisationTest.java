package com.github.muehmar.gradle.openapi.oneofenumdiscriminator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import org.junit.jupiter.api.Test;

class MyPetSerialisationTest {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void writeValueAsString_when_dogDto_then_correctJson() throws JsonProcessingException {
    final DogDto dogDto =
        DogDto.builder()
            .setId("dog-id")
            .setType(PetDto.TypeEnum.DOG)
            .setBark("bark")
            .andAllOptionals()
            .build();
    final MyPetDto dto = MyPetDto.builder().setDogDto(dogDto).build();

    assertEquals(
        "{\"bark\":\"bark\",\"id\":\"dog-id\",\"type\":\"Dog\"}", MAPPER.writeValueAsString(dto));
  }

  @Test
  void writeValueAsString_when_userDto_then_correctJson() throws JsonProcessingException {
    final CatDto catDto =
        CatDto.builder()
            .setId("cat-id")
            .setType(PetDto.TypeEnum.CAT)
            .setName("mimmi")
            .andAllOptionals()
            .build();
    final MyPetDto dto = MyPetDto.builder().setCatDto(catDto).build();

    assertEquals(
        "{\"id\":\"cat-id\",\"name\":\"mimmi\",\"type\":\"Cat\"}", MAPPER.writeValueAsString(dto));
  }
}
