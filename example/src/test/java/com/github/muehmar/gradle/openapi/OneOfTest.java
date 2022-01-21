package com.github.muehmar.gradle.openapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class OneOfTest {

  public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @BeforeAll
  static void setupMapper() {
    OBJECT_MAPPER.registerModule(new JavaTimeModule());
    OBJECT_MAPPER.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
  }

  @Test
  void onlyColor() throws JsonProcessingException {
    final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    final Validator validator = validatorFactory.getValidator();

    final OneOfColorOrUser colorOrUser =
        OBJECT_MAPPER.readValue(
            "{\n"
                + "  \"colorKey\": 5,\n"
                + "  \"colorName\": \"green\", \n"
                + "\"propertyType\":\"color\""
                + "}",
            OneOfColorOrUser.class);

    final Set<ConstraintViolation<OneOfColorOrUser>> violations = validator.validate(colorOrUser);
    violations.forEach(v -> System.out.println(v.getPropertyPath() + " " + v.getMessage()));
    assertTrue(violations.isEmpty());
  }

  @Test
  void onlyUser() throws JsonProcessingException {
    final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    final Validator validator = validatorFactory.getValidator();

    final OneOfColorOrUser colorOrUser =
        OBJECT_MAPPER.readValue(
            "{\n"
                + "  \"id\": \"ID123456\",\n"
                + "  \"username\": null, \n"
                + "\"propertyType\":\"user\""
                + "}",
            OneOfColorOrUser.class);

    final Set<ConstraintViolation<OneOfColorOrUser>> violations = validator.validate(colorOrUser);
    violations.forEach(v -> System.out.println(v.getPropertyPath() + " " + v.getMessage()));
    assertTrue(violations.isEmpty());
  }

  @Test
  void userSchemaButColorDiscriminator() throws JsonProcessingException {
    final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    final Validator validator = validatorFactory.getValidator();

    final OneOfColorOrUser colorOrUser =
        OBJECT_MAPPER.readValue(
            "{\n"
                + "  \"id\": \"ID123456\",\n"
                + "  \"username\": null, \n"
                + "\"propertyType\":\"color\""
                + "}",
            OneOfColorOrUser.class);

    final Set<ConstraintViolation<OneOfColorOrUser>> violations = validator.validate(colorOrUser);
    violations.forEach(v -> System.out.println(v.getPropertyPath() + " " + v.getMessage()));
    assertFalse(violations.isEmpty());
  }

  @Test
  void incomplete() throws JsonProcessingException {
    final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    final Validator validator = validatorFactory.getValidator();

    final OneOfColorOrUser oneOfColorOrUser =
        OBJECT_MAPPER.readValue("{\n" + "  \"id\": \"ID123456\"\n" + "}", OneOfColorOrUser.class);

    final Set<ConstraintViolation<OneOfColorOrUser>> violations =
        validator.validate(oneOfColorOrUser);
    violations.forEach(v -> System.out.println(v.getPropertyPath() + " " + v.getMessage()));
    assertFalse(violations.isEmpty());
  }

  @Test
  void both() throws JsonProcessingException {
    final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    final Validator validator = validatorFactory.getValidator();

    final OneOfColorOrUser oneOfColorOrUser =
        OBJECT_MAPPER.readValue(
            "{\n"
                + "  \"colorKey\": 5,\n"
                + "  \"colorName\": \"green\", \n"
                + "  \"id\": \"ID123456\",\n"
                + "  \"username\": null"
                + "}",
            OneOfColorOrUser.class);

    final Set<ConstraintViolation<OneOfColorOrUser>> violations =
        validator.validate(oneOfColorOrUser);
    violations.forEach(v -> System.out.println(v.getPropertyPath() + " " + v.getMessage()));
    assertFalse(violations.isEmpty());
  }

  @Test
  void serializeColor() throws JsonProcessingException {
    final OneOfColorOrUser oneOfColorOrUser =
        OneOfColorOrUser.fromColor(new ColorDto2(15, "yellow"));
    final String value = OBJECT_MAPPER.writeValueAsString(oneOfColorOrUser);
    assertEquals("{\"colorKey\":15,\"colorName\":\"yellow\",\"propertyType\":\"color\"}", value);
  }

  @Test
  void serializeUser() throws JsonProcessingException {
    final OneOfColorOrUser oneOfColorOrUser =
        OneOfColorOrUser.fromUser(new UserDto2("ID123456", null, true, null, null, true));
    final String value = OBJECT_MAPPER.writeValueAsString(oneOfColorOrUser);
    assertEquals(
        "{\"id\":\"ID123456\",\"phone\":null,\"propertyType\":\"user\",\"username\":null}", value);
  }
}
