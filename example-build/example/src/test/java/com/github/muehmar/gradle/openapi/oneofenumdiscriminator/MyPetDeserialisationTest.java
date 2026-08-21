package com.github.muehmar.gradle.openapi.oneofenumdiscriminator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class MyPetDeserialisationTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void fold_when_matchesDog_then_dogDtoReturned() throws Exception {
    final MyPetDto myPetDto =
        MAPPER.readValue("{\"id\":\"dog-id\",\"type\":\"Dog\",\"bark\":\"bark\"}", MyPetDto.class);

    final Object obj = myPetDto.foldOneOf(admin -> admin, user -> user);

    final DogDto dogDto =
        DogDto.builder()
            .setId("dog-id")
            .setType(PetDto.TypeEnum.DOG)
            .setBark("bark")
            .andAllOptionals()
            .build();

    assertEquals(dogDto, obj);
    assertEquals(Optional.empty(), myPetDto.getCatDto());
    assertEquals(Optional.of(dogDto), myPetDto.getDogDto());
  }

  @Test
  void fold_when_matchesCat_then_catDtoReturned() throws Exception {
    final MyPetDto myPetDto =
        MAPPER.readValue("{\"id\":\"cat-id\",\"type\":\"Cat\",\"name\":\"mimmi\"}", MyPetDto.class);

    final Object obj = myPetDto.foldOneOf(admin -> admin, user -> user);

    final CatDto catDto =
        CatDto.builder()
            .setId("cat-id")
            .setType(PetDto.TypeEnum.CAT)
            .setName("mimmi")
            .andAllOptionals()
            .build();

    assertEquals(catDto, obj);
    assertEquals(Optional.of(catDto), myPetDto.getCatDto());
    assertEquals(Optional.empty(), myPetDto.getDogDto());
  }
}
