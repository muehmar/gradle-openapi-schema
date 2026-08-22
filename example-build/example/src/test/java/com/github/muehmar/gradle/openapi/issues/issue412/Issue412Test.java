package com.github.muehmar.gradle.openapi.issues.issue412;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * The all-args constructor of a generated DTO used to be {@code public}, which allowed constructing
 * a DTO with a value and its companion presence/not-null flag contradicting each other - a state no
 * intended construction path can reach and which yields inconsistent validation results. It is
 * package-private now, so only the DTO itself, its builder and sibling DTOs of the same package can
 * call it.
 */
class Issue412Test {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void constructor_when_objectDto_then_isPackagePrivate() {
    assertPackagePrivate(allArgsConstructorOf(UserDto.class));
  }

  @Test
  void constructor_when_arrayDto_then_isPackagePrivate() {
    assertPackagePrivate(allArgsConstructorOf(TagsDto.class));
  }

  @Test
  void builder_when_used_then_dtoStillConstructable() {
    final UserDto dto =
        UserDto.builder()
            .setId("1")
            .setUsername("someName")
            .andAllOptionals()
            .setEmail("some@mail.ch")
            .build();

    assertEquals("1", dto.getId());
    assertEquals("someName", dto.getUsernameOr(null));
    assertEquals("some@mail.ch", dto.getEmailOr(null));
  }

  @Test
  void deserialize_when_arrayDtoWithJsonCreatorConstructor_then_stillWorks() throws Exception {
    final TagsDto dto = MAPPER.readValue("[\"a\",\"b\"]", TagsDto.class);

    assertEquals(List.of("a", "b"), dto.getItems());
    assertEquals("[\"a\",\"b\"]", MAPPER.writeValueAsString(dto));
  }

  private static void assertPackagePrivate(Constructor<?> constructor) {
    final int modifiers = constructor.getModifiers();

    assertFalse(Modifier.isPublic(modifiers), "constructor is public");
    assertFalse(Modifier.isProtected(modifiers), "constructor is protected");
    assertFalse(Modifier.isPrivate(modifiers), "constructor is private");
  }

  private static Constructor<?> allArgsConstructorOf(Class<?> dtoClass) {
    return Arrays.stream(dtoClass.getDeclaredConstructors())
        .max(Comparator.comparingInt(Constructor::getParameterCount))
        .orElseThrow(IllegalStateException::new);
  }
}
