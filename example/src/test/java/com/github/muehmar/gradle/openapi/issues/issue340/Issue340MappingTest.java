package com.github.muehmar.gradle.openapi.issues.issue340;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static com.github.muehmar.gradle.openapi.util.ViolationFormatter.formatViolations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.issues.issue340mapping.TagListDto;
import com.github.muehmar.gradle.openapi.issues.issue340mapping.UserDto;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

public class Issue340MappingTest {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void userBuilder_when_tagsSet_then_returnsCustomSet() {
    final CustomSet<String> tags =
        CustomSet.fromSet(new HashSet<>(Arrays.asList("java", "kotlin", "scala")));
    final UserDto userDto = UserDto.fullUserDtoBuilder().setName("John").setTags(tags).build();

    assertEquals(tags, userDto.getTagsOr(null));
    assertTrue(userDto.getTagsOpt().isPresent());
  }

  @Test
  void userBuilder_when_emptyTags_then_returnsEmptyCustomSet() {
    final UserDto userDto =
        UserDto.fullUserDtoBuilder()
            .setName("Jane")
            .setTags(CustomSet.fromSet(new HashSet<>()))
            .build();

    assertEquals(CustomSet.fromSet(new HashSet<>()), userDto.getTagsOr(null));
    assertTrue(userDto.getTagsOpt().isPresent());
  }

  @Test
  void serialize_when_userDtoWithTags_then_jsonContainsTagsArray() throws JsonProcessingException {
    final CustomSet<String> tags =
        CustomSet.fromSet(new HashSet<>(Arrays.asList("java", "kotlin", "scala")));
    final UserDto userDto = UserDto.fullUserDtoBuilder().setName("John").setTags(tags).build();

    final String json = MAPPER.writeValueAsString(userDto);

    assertEquals("{\"name\":\"John\",\"tags\":[\"java\",\"scala\",\"kotlin\"]}", json);
  }

  @Test
  void serialize_when_userDtoWithEmptyTags_then_jsonContainsEmptyArray()
      throws JsonProcessingException {
    final UserDto userDto =
        UserDto.fullUserDtoBuilder()
            .setName("Jane")
            .setTags(CustomSet.fromSet(new HashSet<>()))
            .build();

    final String json = MAPPER.writeValueAsString(userDto);

    assertEquals("{\"name\":\"Jane\",\"tags\":[]}", json);
  }

  @Test
  void serialize_when_userDtoWithoutTags_then_jsonOmitsTags() throws JsonProcessingException {
    final UserDto userDto =
        UserDto.fullUserDtoBuilder()
            .setName("Bob")
            .setTags(Optional.<CustomSet<String>>empty())
            .build();

    final String json = MAPPER.writeValueAsString(userDto);

    assertEquals("{\"name\":\"Bob\"}", json);
  }

  @Test
  void deserialize_when_jsonWithTagsArray_then_returnsUserDtoWithCustomSet()
      throws JsonProcessingException {
    final String json = "{\"name\":\"John\",\"tags\":[\"java\",\"kotlin\",\"scala\"]}";

    final UserDto userDto = MAPPER.readValue(json, UserDto.class);

    assertEquals("John", userDto.getName());
    assertEquals(
        CustomSet.fromSet(new HashSet<>(Arrays.asList("java", "kotlin", "scala"))),
        userDto.getTagsOr(null));
  }

  @Test
  void deserialize_when_jsonWithEmptyTagsArray_then_returnsUserDtoWithEmptyCustomSet()
      throws JsonProcessingException {
    final String json = "{\"name\":\"Jane\",\"tags\":[]}";

    final UserDto userDto = MAPPER.readValue(json, UserDto.class);

    assertEquals("Jane", userDto.getName());
    assertEquals(CustomSet.fromSet(new HashSet<>()), userDto.getTagsOr(null));
    assertTrue(userDto.getTagsOpt().isPresent());
  }

  @Test
  void deserialize_when_jsonWithoutTags_then_returnsUserDtoWithoutTags()
      throws JsonProcessingException {
    final String json = "{\"name\":\"Bob\"}";

    final UserDto userDto = MAPPER.readValue(json, UserDto.class);

    assertEquals("Bob", userDto.getName());
    assertFalse(userDto.getTagsOpt().isPresent());
  }

  @Test
  void deserialize_when_jsonWithDuplicateTags_then_returnsUserDtoWithUniqueTags()
      throws JsonProcessingException {
    final String json = "{\"name\":\"Alice\",\"tags\":[\"java\",\"kotlin\",\"java\",\"scala\"]}";

    final UserDto userDto = MAPPER.readValue(json, UserDto.class);

    assertEquals("Alice", userDto.getName());
    final CustomSet<String> expectedTags =
        CustomSet.fromSet(new HashSet<>(Arrays.asList("java", "kotlin", "scala")));
    assertEquals(expectedTags, userDto.getTagsOr(CustomSet.fromSet(new HashSet<>())));
  }

  @Test
  void validate_when_userDtoWithNonUniqueTags_then_violations() throws JsonProcessingException {
    final String json = "{\"name\":\"Alice\",\"tags\":[\"java\",\"kotlin\",\"java\",\"scala\"]}";

    final UserDto userDto = MAPPER.readValue(json, UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = validate(userDto);

    assertEquals(
        Arrays.asList("tagsUniqueItems -> tags does not contain unique items"),
        formatViolations(violations));
  }

  @Test
  void validate_when_userDtoWithUniqueTags_then_noViolations() {
    final CustomSet<String> tags =
        CustomSet.fromSet(new HashSet<>(Arrays.asList("java", "kotlin", "scala")));
    final UserDto userDto = UserDto.fullUserDtoBuilder().setName("John").setTags(tags).build();

    final Set<ConstraintViolation<UserDto>> violations = validate(userDto);

    assertEquals(0, violations.size());
  }

  @Test
  void validate_when_userDtoWithEmptyTags_then_noViolations() {
    final UserDto userDto =
        UserDto.fullUserDtoBuilder()
            .setName("Jane")
            .setTags(CustomSet.fromSet(new HashSet<>()))
            .build();

    final Set<ConstraintViolation<UserDto>> violations = validate(userDto);

    assertEquals(0, violations.size());
  }

  @Test
  void validate_when_userDtoWithEmptyName_then_valid() {
    final CustomSet<String> tags =
        CustomSet.fromSet(new HashSet<>(Arrays.asList("java", "kotlin")));
    final UserDto userDto = UserDto.fullUserDtoBuilder().setName("").setTags(tags).build();

    final Set<ConstraintViolation<UserDto>> violations = validate(userDto);

    assertEquals(0, violations.size());
  }

  @Test
  void tagListDto_when_constructed_then_isCustomSet() {
    final CustomSet<String> tags =
        CustomSet.fromSet(new HashSet<>(Arrays.asList("java", "kotlin", "scala")));
    final TagListDto tagListDto = TagListDto.fromItems(tags);

    assertEquals(tags, tagListDto.getItems());
  }

  @Test
  void serialize_when_tagListDto_then_jsonIsArray() throws JsonProcessingException {
    final CustomSet<String> tags =
        CustomSet.fromSet(new HashSet<>(Arrays.asList("java", "kotlin", "scala")));
    final TagListDto tagListDto = TagListDto.fromItems(tags);

    final String json = MAPPER.writeValueAsString(tagListDto);

    assertEquals("[\"java\",\"scala\",\"kotlin\"]", json);
  }

  @Test
  void serialize_when_tagListDtoEmpty_then_jsonIsEmptyArray() throws JsonProcessingException {
    final TagListDto tagListDto = TagListDto.fromItems(CustomSet.fromSet(new HashSet<>()));

    final String json = MAPPER.writeValueAsString(tagListDto);

    assertEquals("[]", json);
  }

  @Test
  void deserialize_when_jsonArray_then_returnsTagListDtoWithCustomSet()
      throws JsonProcessingException {
    final String json = "[\"java\",\"kotlin\",\"scala\"]";

    final TagListDto tagListDto = MAPPER.readValue(json, TagListDto.class);

    assertEquals(
        CustomSet.fromSet(new HashSet<>(Arrays.asList("java", "kotlin", "scala"))),
        tagListDto.getItems());
  }

  @Test
  void deserialize_when_emptyJsonArray_then_returnsTagListDtoWithEmptyCustomSet()
      throws JsonProcessingException {
    final String json = "[]";

    final TagListDto tagListDto = MAPPER.readValue(json, TagListDto.class);

    assertEquals(CustomSet.fromSet(new HashSet<>()), tagListDto.getItems());
    assertEquals(0, tagListDto.getItems().asSet().size());
  }

  @Test
  void deserialize_when_jsonArrayWithDuplicates_then_returnsTagListDtoWithUniqueItems()
      throws JsonProcessingException {
    final String json = "[\"java\",\"kotlin\",\"java\",\"scala\",\"kotlin\"]";

    final TagListDto tagListDto = MAPPER.readValue(json, TagListDto.class);

    final CustomSet<String> expectedTags =
        CustomSet.fromSet(new HashSet<>(Arrays.asList("java", "kotlin", "scala")));
    assertEquals(expectedTags, tagListDto.getItems());
  }

  @Test
  void validate_when_tagListDtoWithNonUniqueItems_then_violations() throws JsonProcessingException {
    final String json = "[\"java\",\"kotlin\",\"java\",\"scala\",\"kotlin\"]";

    final TagListDto tagListDto = MAPPER.readValue(json, TagListDto.class);

    final Set<ConstraintViolation<TagListDto>> violations = validate(tagListDto);

    assertEquals(
        Arrays.asList("itemsUniqueItems -> items does not contain unique items"),
        formatViolations(violations));
  }

  @Test
  void validate_when_tagListDtoWithUniqueItems_then_noViolations() {
    final CustomSet<String> tags =
        CustomSet.fromSet(new HashSet<>(Arrays.asList("java", "kotlin", "scala")));
    final TagListDto tagListDto = TagListDto.fromItems(tags);

    final Set<ConstraintViolation<TagListDto>> violations = validate(tagListDto);

    assertEquals(0, violations.size());
  }

  @Test
  void validate_when_tagListDtoEmpty_then_noViolations() {
    final TagListDto tagListDto = TagListDto.fromItems(CustomSet.fromSet(new HashSet<>()));

    final Set<ConstraintViolation<TagListDto>> violations = validate(tagListDto);

    assertEquals(0, violations.size());
  }

  @Test
  void validate_when_tagListDtoWithSingleItem_then_noViolations() {
    final CustomSet<String> tags = CustomSet.fromSet(new HashSet<>(Arrays.asList("java")));
    final TagListDto tagListDto = TagListDto.fromItems(tags);

    final Set<ConstraintViolation<TagListDto>> violations = validate(tagListDto);

    assertEquals(0, violations.size());
  }
}
