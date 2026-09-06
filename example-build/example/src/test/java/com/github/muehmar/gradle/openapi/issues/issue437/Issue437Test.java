package com.github.muehmar.gradle.openapi.issues.issue437;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Test;

/**
 * The discriminator property is the one property guaranteed to be present on every variant of a
 * discriminated oneOf/anyOf composition, so its getter is public on the composition DTO. All other
 * flat property getters of a oneOf/anyOf composition DTO remain package-private, as such a property
 * only has a well-defined meaning on the member DTO obtained through the decomposition.
 */
class Issue437Test {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void discriminatorGetter_when_oneOfCompositionWithInlineStringDiscriminator_then_isPublic()
      throws NoSuchMethodException {
    assertPublic(PetDto.class.getDeclaredMethod("getPetType"));
  }

  @Test
  void discriminatorGetter_when_oneOfCompositionWithEnumDiscriminatorFromAllOfBase_then_isPublic()
      throws NoSuchMethodException {
    assertPublic(EventDto.class.getDeclaredMethod("getEventType"));
  }

  @Test
  void discriminatorGetter_when_anyOfComposition_then_isPublic() throws NoSuchMethodException {
    assertPublic(MessageDto.class.getDeclaredMethod("getMessageType"));
  }

  @Test
  void nonDiscriminatorGetter_when_oneOfComposition_then_staysPackagePrivate()
      throws NoSuchMethodException {
    assertPackagePrivate(PetDto.class.getDeclaredMethod("getMeowsOpt"));
  }

  @Test
  void nonDiscriminatorGetter_when_anyOfComposition_then_staysPackagePrivate()
      throws NoSuchMethodException {
    assertPackagePrivate(MessageDto.class.getDeclaredMethod("getEmail"));
  }

  @Test
  void discriminatorGetter_when_deserializedOneOf_then_returnsTag() throws Exception {
    final PetDto pet = MAPPER.readValue("{\"petType\":\"Cat\",\"meows\":true}", PetDto.class);

    assertEquals("Cat", pet.getPetType());
  }

  @Test
  void discriminatorGetter_when_deserializedEnumDiscriminator_then_returnsTag() throws Exception {
    final EventDto event =
        MAPPER.readValue("{\"eventType\":\"user\",\"username\":\"john\"}", EventDto.class);

    assertEquals(EventBaseDto.EventTypeEnum.USER, event.getEventType());
  }

  @Test
  void discriminatorGetter_when_deserializedAnyOf_then_returnsTag() throws Exception {
    final MessageDto message =
        MAPPER.readValue(
            "{\"messageType\":\"EmailMessage\",\"email\":\"some@mail.ch\"}", MessageDto.class);

    assertEquals("EmailMessage", message.getMessageType());
  }

  private static void assertPublic(Method method) {
    assertTrue(
        Modifier.isPublic(method.getModifiers()),
        () -> "Expected " + method + " to be public but was not");
  }

  private static void assertPackagePrivate(Method method) {
    final int modifiers = method.getModifiers();
    assertFalse(
        Modifier.isPublic(modifiers), () -> "Expected " + method + " to be package-private");
    assertFalse(Modifier.isProtected(modifiers));
    assertFalse(Modifier.isPrivate(modifiers));
  }
}
