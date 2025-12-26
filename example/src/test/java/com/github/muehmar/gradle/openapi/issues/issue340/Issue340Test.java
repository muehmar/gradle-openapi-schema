package com.github.muehmar.gradle.openapi.issues.issue340;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static com.github.muehmar.gradle.openapi.util.ViolationFormatter.formatViolations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

public class Issue340Test {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void userBuilder_when_tagsSet_then_returnsSet() {
    final Set<String> tags = new HashSet<>(Arrays.asList("java", "kotlin", "scala"));
    final UserDto userDto = UserDto.fullUserDtoBuilder().setName("John").setTags(tags).build();

    assertEquals(tags, userDto.getTagsOr(Collections.emptySet()));
    assertTrue(userDto.getTagsOpt().isPresent());
    assertInstanceOf(Set.class, userDto.getTagsOpt().get());
  }

  @Test
  void userBuilder_when_emptyTags_then_returnsEmptySet() {
    final UserDto userDto =
        UserDto.fullUserDtoBuilder().setName("Jane").setTags(Collections.emptySet()).build();

    assertEquals(Collections.emptySet(), userDto.getTagsOr(Collections.emptySet()));
    assertTrue(userDto.getTagsOpt().isPresent());
  }

  @Test
  void serialize_when_userDtoWithTags_then_jsonContainsTagsArray() throws Exception {
    final Set<String> tags = new HashSet<>(Arrays.asList("java", "kotlin", "scala"));
    final UserDto userDto = UserDto.fullUserDtoBuilder().setName("John").setTags(tags).build();

    final String json = MAPPER.writeValueAsString(userDto);

    assertEquals("{\"name\":\"John\",\"tags\":[\"java\",\"scala\",\"kotlin\"]}", json);
  }

  @Test
  void serialize_when_userDtoWithEmptyTags_then_jsonContainsEmptyArray() throws Exception {
    final UserDto userDto =
        UserDto.fullUserDtoBuilder().setName("Jane").setTags(Collections.emptySet()).build();

    final String json = MAPPER.writeValueAsString(userDto);

    assertEquals("{\"name\":\"Jane\",\"tags\":[]}", json);
  }

  @Test
  void serialize_when_userDtoWithoutTags_then_jsonOmitsTags() throws Exception {
    final UserDto userDto =
        UserDto.fullUserDtoBuilder().setName("Bob").setTags(Optional.empty()).build();

    final String json = MAPPER.writeValueAsString(userDto);

    assertEquals("{\"name\":\"Bob\"}", json);
  }

  @Test
  void deserialize_when_jsonWithTagsArray_then_returnsUserDtoWithSet() throws Exception {
    final String json = "{\"name\":\"John\",\"tags\":[\"java\",\"kotlin\",\"scala\"]}";

    final UserDto userDto = MAPPER.readValue(json, UserDto.class);

    assertEquals("John", userDto.getName());
    assertEquals(
        new HashSet<>(Arrays.asList("java", "kotlin", "scala")),
        userDto.getTagsOr(Collections.emptySet()));
  }

  @Test
  void deserialize_when_jsonWithEmptyTagsArray_then_returnsUserDtoWithEmptySet() throws Exception {
    final String json = "{\"name\":\"Jane\",\"tags\":[]}";

    final UserDto userDto = MAPPER.readValue(json, UserDto.class);

    assertEquals("Jane", userDto.getName());
    assertEquals(Collections.emptySet(), userDto.getTagsOr(Collections.emptySet()));
    assertTrue(userDto.getTagsOpt().isPresent());
  }

  @Test
  void deserialize_when_jsonWithoutTags_then_returnsUserDtoWithoutTags() throws Exception {
    final String json = "{\"name\":\"Bob\"}";

    final UserDto userDto = MAPPER.readValue(json, UserDto.class);

    assertEquals("Bob", userDto.getName());
    assertFalse(userDto.getTagsOpt().isPresent());
  }

  @Test
  void deserialize_when_jsonWithDuplicateTags_then_returnsUserDtoWithUniqueTags() throws Exception {
    final String json = "{\"name\":\"Alice\",\"tags\":[\"java\",\"kotlin\",\"java\",\"scala\"]}";

    final UserDto userDto = MAPPER.readValue(json, UserDto.class);

    assertEquals("Alice", userDto.getName());
    final Set<String> expectedTags = new HashSet<>(Arrays.asList("java", "kotlin", "scala"));
    assertEquals(expectedTags, userDto.getTagsOr(Collections.emptySet()));
  }

  @Test
  void validate_when_userDtoWithNonUniqueTags_then_violations() throws Exception {
    final String json = "{\"name\":\"Alice\",\"tags\":[\"java\",\"kotlin\",\"java\",\"scala\"]}";

    final UserDto userDto = MAPPER.readValue(json, UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = validate(userDto);

    assertEquals(
        Arrays.asList("tagsUniqueItems -> tags does not contain unique items"),
        formatViolations(violations));
  }

  @Test
  void validate_when_userDtoWithUniqueTags_then_noViolations() {
    final Set<String> tags = new HashSet<>(Arrays.asList("java", "kotlin", "scala"));
    final UserDto userDto = UserDto.fullUserDtoBuilder().setName("John").setTags(tags).build();

    final Set<ConstraintViolation<UserDto>> violations = validate(userDto);

    assertEquals(0, violations.size());
    assertTrue(userDto.isValid());
  }

  @Test
  void validate_when_userDtoWithEmptyTags_then_noViolations() {
    final UserDto userDto =
        UserDto.fullUserDtoBuilder().setName("Jane").setTags(Collections.emptySet()).build();

    final Set<ConstraintViolation<UserDto>> violations = validate(userDto);

    assertEquals(0, violations.size());
    assertTrue(userDto.isValid());
  }

  @Test
  void validate_when_userDtoWithEmptyName_then_valid() {
    final Set<String> tags = new HashSet<>(Arrays.asList("java", "kotlin"));
    final UserDto userDto = UserDto.fullUserDtoBuilder().setName("").setTags(tags).build();

    final Set<ConstraintViolation<UserDto>> violations = validate(userDto);

    // Empty string is valid, only null is invalid for @NotNull
    assertEquals(0, violations.size());
    assertTrue(userDto.isValid());
  }

  @Test
  void tagListDto_when_constructed_then_isSet() {
    final Set<String> tags = new HashSet<>(Arrays.asList("java", "kotlin", "scala"));
    final TagListDto tagListDto = TagListDto.fromItems(tags);

    assertEquals(tags, tagListDto.getItems());
  }

  @Test
  void serialize_when_tagListDto_then_jsonIsArray() throws Exception {
    final Set<String> tags = new HashSet<>(Arrays.asList("java", "kotlin", "scala"));
    final TagListDto tagListDto = TagListDto.fromItems(tags);

    final String json = MAPPER.writeValueAsString(tagListDto);

    assertEquals("[\"java\",\"scala\",\"kotlin\"]", json);
  }

  @Test
  void serialize_when_tagListDtoEmpty_then_jsonIsEmptyArray() throws Exception {
    final TagListDto tagListDto = TagListDto.fromItems(Collections.emptySet());

    final String json = MAPPER.writeValueAsString(tagListDto);

    assertEquals("[]", json);
  }

  // Deserialization Tests for TagListDto
  @Test
  void deserialize_when_jsonArray_then_returnsTagListDtoWithSet() throws Exception {
    final String json = "[\"java\",\"kotlin\",\"scala\"]";

    final TagListDto tagListDto = MAPPER.readValue(json, TagListDto.class);

    assertEquals(new HashSet<>(Arrays.asList("java", "kotlin", "scala")), tagListDto.getItems());
  }

  @Test
  void deserialize_when_emptyJsonArray_then_returnsTagListDtoWithEmptySet() throws Exception {
    final String json = "[]";

    final TagListDto tagListDto = MAPPER.readValue(json, TagListDto.class);

    assertEquals(Collections.emptySet(), tagListDto.getItems());
    assertTrue(tagListDto.getItems().isEmpty());
  }

  @Test
  void deserialize_when_jsonArrayWithDuplicates_then_returnsTagListDtoWithUniqueItems()
      throws Exception {
    final String json = "[\"java\",\"kotlin\",\"java\",\"scala\",\"kotlin\"]";

    final TagListDto tagListDto = MAPPER.readValue(json, TagListDto.class);

    final Set<String> expectedTags = new HashSet<>(Arrays.asList("java", "kotlin", "scala"));
    assertEquals(expectedTags, tagListDto.getItems());
  }

  @Test
  void validate_when_tagListDtoWithNonUniqueItems_then_noViolations() throws Exception {
    final String json = "[\"java\",\"kotlin\",\"java\",\"scala\",\"kotlin\"]";

    final TagListDto tagListDto = MAPPER.readValue(json, TagListDto.class);

    final Set<ConstraintViolation<TagListDto>> violations = validate(tagListDto);

    assertEquals(
        Arrays.asList("itemsUniqueItems -> items does not contain unique items"),
        formatViolations(violations));
  }

  @Test
  void validate_when_tagListDtoWithUniqueItems_then_noViolations() {
    final Set<String> tags = new HashSet<>(Arrays.asList("java", "kotlin", "scala"));
    final TagListDto tagListDto = TagListDto.fromItems(tags);

    final Set<ConstraintViolation<TagListDto>> violations = validate(tagListDto);

    assertEquals(0, violations.size());
    assertTrue(tagListDto.isValid());
  }

  @Test
  void validate_when_tagListDtoEmpty_then_noViolations() {
    final TagListDto tagListDto = TagListDto.fromItems(Collections.emptySet());

    final Set<ConstraintViolation<TagListDto>> violations = validate(tagListDto);

    assertEquals(0, violations.size());
    assertTrue(tagListDto.isValid());
  }

  @Test
  void validate_when_tagListDtoWithSingleItem_then_noViolations() {
    final Set<String> tags = new HashSet<>(Collections.singletonList("java"));
    final TagListDto tagListDto = TagListDto.fromItems(tags);

    final Set<ConstraintViolation<TagListDto>> violations = validate(tagListDto);

    assertEquals(0, violations.size());
    assertTrue(tagListDto.isValid());
  }
}
