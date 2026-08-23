package com.github.muehmar.gradle.openapi.issues.issue258;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.gradle.openapi.util.ValidationUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

public class Issue258Test {
  private static final JsonMapper mapper = MapperFactory.jsonMapper();

  @Test
  void validate_when_optionalMapWithNullStringValue_then_constraintViolation() throws Exception {
    final InlinedMapSchemaDto dto =
        mapper.readValue("{\"map\":{\"key\":null}}", InlinedMapSchemaDto.class);

    final Set<ConstraintViolation<InlinedMapSchemaDto>> violations = ValidationUtil.validate(dto);

    assertEquals(1, violations.size());
    assertEquals("map_[key].<map value>", propertyPath(violations));
  }

  @Test
  void validate_when_optionalMapWithNonNullStringValues_then_noConstraintViolation()
      throws Exception {
    final InlinedMapSchemaDto dto =
        mapper.readValue("{\"map\":{\"key\":\"value\"}}", InlinedMapSchemaDto.class);

    assertTrue(ValidationUtil.validate(dto).isEmpty());
  }

  @Test
  void validate_when_optionalMapAbsent_then_noConstraintViolation() throws Exception {
    final InlinedMapSchemaDto dto = mapper.readValue("{}", InlinedMapSchemaDto.class);

    assertTrue(ValidationUtil.validate(dto).isEmpty());
  }

  @Test
  void validate_when_mapWithNullValueBuiltWithBuilder_then_constraintViolation() {
    final Map<String, String> map = new HashMap<>();
    map.put("key", null);
    final InlinedMapSchemaDto dto =
        InlinedMapSchemaDto.builder().andAllOptionals().setMap(map).build();

    final Set<ConstraintViolation<InlinedMapSchemaDto>> violations = ValidationUtil.validate(dto);

    assertEquals(1, violations.size());
    assertEquals("map_[key].<map value>", propertyPath(violations));
  }

  @Test
  void validate_when_requiredMapWithNullStringValue_then_constraintViolation() throws Exception {
    final RequiredMapSchemaDto dto =
        mapper.readValue("{\"map\":{\"key\":null}}", RequiredMapSchemaDto.class);

    final Set<ConstraintViolation<RequiredMapSchemaDto>> violations = ValidationUtil.validate(dto);

    assertEquals(1, violations.size());
    assertEquals("map_[key].<map value>", propertyPath(violations));
  }

  @Test
  void validate_when_nullableMapWithNullStringValue_then_constraintViolation() throws Exception {
    final NullableMapSchemaDto dto =
        mapper.readValue("{\"map\":{\"key\":null}}", NullableMapSchemaDto.class);

    final Set<ConstraintViolation<NullableMapSchemaDto>> violations = ValidationUtil.validate(dto);

    assertEquals(1, violations.size());
    assertEquals("map_[key].<map value>", propertyPath(violations));
  }

  @Test
  void validate_when_nullableMapIsNull_then_noConstraintViolation() throws Exception {
    final NullableMapSchemaDto dto = mapper.readValue("{\"map\":null}", NullableMapSchemaDto.class);

    assertTrue(ValidationUtil.validate(dto).isEmpty());
  }

  @Test
  void validate_when_mapWithNullObjectValue_then_constraintViolation() throws Exception {
    final ObjectMapSchemaDto dto =
        mapper.readValue("{\"map\":{\"key\":null}}", ObjectMapSchemaDto.class);

    final Set<ConstraintViolation<ObjectMapSchemaDto>> violations = ValidationUtil.validate(dto);

    assertEquals(1, violations.size());
    assertEquals("map_[key].<map value>", propertyPath(violations));
  }

  @Test
  void validate_when_mapWithNullIntegerValue_then_constraintViolation() throws Exception {
    final IntegerMapSchemaDto dto =
        mapper.readValue("{\"map\":{\"key\":null}}", IntegerMapSchemaDto.class);

    final Set<ConstraintViolation<IntegerMapSchemaDto>> violations = ValidationUtil.validate(dto);

    assertEquals(1, violations.size());
    assertEquals("map_[key].<map value>", propertyPath(violations));
  }

  @Test
  void validate_when_allOfInheritedMapWithNullValue_then_constraintViolation() throws Exception {
    final AllOfMapSchemaDto dto =
        mapper.readValue("{\"map\":{\"key\":null},\"other\":\"x\"}", AllOfMapSchemaDto.class);

    final Set<ConstraintViolation<AllOfMapSchemaDto>> violations = ValidationUtil.validate(dto);

    assertEquals(1, violations.size());
    assertEquals("requiredMapSchemaDto.map_[key].<map value>", propertyPath(violations));
  }

  @Test
  void validate_when_allOfInheritedMapWithNonNullValue_then_noConstraintViolation()
      throws Exception {
    final AllOfMapSchemaDto dto =
        mapper.readValue("{\"map\":{\"key\":\"value\"},\"other\":\"x\"}", AllOfMapSchemaDto.class);

    assertTrue(ValidationUtil.validate(dto).isEmpty());
  }

  @Test
  void validate_when_oneOfBranchMapWithNullValue_then_constraintViolation() throws Exception {
    final OneOfMapSchemaDto dto =
        mapper.readValue("{\"map\":{\"key\":null}}", OneOfMapSchemaDto.class);

    assertTrue(
        ValidationUtil.validate(dto).stream()
            .anyMatch(
                v ->
                    v.getPropertyPath()
                        .toString()
                        .equals("invalidOneOf[RequiredMapSchema].map_[key].<map value>")));
  }

  @Test
  void validate_when_nestedContainerMapWithNullListItem_then_constraintViolation()
      throws Exception {
    final NestedContainerMapSchemaDto dto =
        mapper.readValue("{\"map\":{\"key\":[\"a\",null]}}", NestedContainerMapSchemaDto.class);

    final Set<ConstraintViolation<NestedContainerMapSchemaDto>> violations =
        ValidationUtil.validate(dto);

    assertEquals(1, violations.size());
    assertEquals("map_[key].items_[1].<list element>", propertyPath(violations));
  }

  private static <T> String propertyPath(Set<ConstraintViolation<T>> violations) {
    return violations.iterator().next().getPropertyPath().toString();
  }
}
