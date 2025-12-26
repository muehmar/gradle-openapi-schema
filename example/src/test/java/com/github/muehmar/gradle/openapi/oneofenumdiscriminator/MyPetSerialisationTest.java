package com.github.muehmar.gradle.openapi.oneofenumdiscriminator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import org.junit.jupiter.api.Test;

class MyPetSerialisationTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void writeValueAsString_when_dogDto_then_correctJson() throws Exception {
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
  void writeValueAsString_when_userDto_then_correctJson() throws Exception {
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
