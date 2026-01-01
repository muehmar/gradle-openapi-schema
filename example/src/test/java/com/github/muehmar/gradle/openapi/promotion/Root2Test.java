package com.github.muehmar.gradle.openapi.promotion;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.openapi.util.Tristate;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class Root2Test {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void writeValueAsString_when_root2DtoSample_then_correctJson() throws Exception {
    final Root2Leaf1BDto root2Lead1BDto =
        Root2Leaf1BDto.root2Leaf1BDtoBuilder()
            .setLeaf1BLeaf2ADto(Leaf1BLeaf2ADto.leaf1BLeaf2ADtoBuilder().setProp3("prop3").build())
            .setLeaf1BLeaf2BDto(Leaf1BLeaf2BDto.leaf1BLeaf2BDtoBuilder().setProp4("prop4").build())
            .build();

    final Root2Dto root2Dto =
        Root2Dto.root2DtoBuilder()
            .setProp2(2)
            .setRoot2Leaf1BDto(root2Lead1BDto)
            .andAllOptionals()
            .setProp1(1)
            .build();

    final String json = MAPPER.writeValueAsString(root2Dto);

    assertEquals("{\"prop1\":1,\"prop2\":2,\"prop3\":\"prop3\",\"prop4\":\"prop4\"}", json);
  }

  @Test
  void readValue_when_root2DtoSample_then_correctDto() throws Exception {
    final String JSON = "{\"prop1\":1,\"prop2\":2,\"prop3\":\"prop3\",\"prop4\":\"prop4\"}";
    final Root2Dto root2Dto = MAPPER.readValue(JSON, Root2Dto.class);

    assertEquals(Optional.of(1), root2Dto.getProp1Opt());
    assertEquals(2, root2Dto.getProp2());

    final boolean foldOneOfResult =
        root2Dto.foldOneOf(
            leaf1ADto -> {
              fail("Lead1ADto should not be valid!");
              return false;
            },
            root2Leaf1BDto -> {
              assertEquals("prop3", root2Leaf1BDto.getProp3());
              assertEquals(Optional.of("prop4"), root2Leaf1BDto.getProp4Opt());

              final List<Boolean> foldAnyOfResult =
                  root2Leaf1BDto.foldAnyOf(
                      leaf1BLeaf2BDto -> {
                        assertEquals("prop4", leaf1BLeaf2BDto.getProp4());
                        return true;
                      },
                      root2Leaf2CDto -> {
                        assertEquals(Tristate.ofAbsent(), root2Leaf2CDto.getProp5Tristate());
                        return true;
                      });

              assertEquals(2, foldAnyOfResult.size());

              return true;
            });

    assertTrue(foldOneOfResult);
  }

  @ParameterizedTest
  @MethodSource("propertyGettersAndReturnTypes")
  void getReturnType_when_root2Dto_then_isPublicMethodAndMatchReturnType(
      Class<?> dtoClass, String methodName, Class<?> returnType) {

    final Optional<Method> maybeMethod =
        Arrays.stream(dtoClass.getDeclaredMethods())
            .filter(method -> method.getName().equals(methodName))
            .findFirst();

    assertTrue(maybeMethod.isPresent());
    final Method getProp1Method = maybeMethod.get();

    assertEquals(returnType, getProp1Method.getReturnType());
    assertEquals(methodName.startsWith("get"), Modifier.isPublic(getProp1Method.getModifiers()));
    assertEquals(methodName.startsWith("as"), Modifier.isPrivate(getProp1Method.getModifiers()));
  }

  public static Stream<Arguments> propertyGettersAndReturnTypes() {
    return Stream.of(
        arguments(Root2Dto.class, "getProp1Opt", Optional.class),
        arguments(Root2Dto.class, "getProp2", Integer.class),
        arguments(Root2Leaf1BDto.class, "getProp3", String.class),
        arguments(Root2Leaf1BDto.class, "getProp4Opt", Optional.class),
        arguments(Root2Leaf1BDto.class, "getLeaf1BLeaf2ADto", Leaf1BLeaf2ADto.class),
        arguments(Root2Leaf1BDto.class, "getLeaf1BLeaf2BDto", Optional.class),
        arguments(Root2Leaf1BDto.class, "getRoot2Leaf2CDto", Optional.class),
        arguments(Root2Leaf1BDto.class, "asLeaf1BLeaf2BDto", Leaf1BLeaf2BDto.class),
        arguments(Root2Leaf1BDto.class, "asRoot2Leaf2CDto", Root2Leaf2CDto.class),
        arguments(Root2Leaf2CDto.class, "getProp1", Integer.class),
        arguments(Root2Leaf2CDto.class, "getProp5Tristate", Tristate.class),
        arguments(Leaf1BDto.class, "getProp3", String.class),
        arguments(Leaf1BDto.class, "getProp4Opt", Optional.class),
        arguments(Leaf1BLeaf2ADto.class, "getProp3", String.class),
        arguments(Leaf1BLeaf2BDto.class, "getProp4", String.class),
        arguments(Leaf2ADto.class, "getProp3", Object.class),
        arguments(Leaf2BDto.class, "getProp4", Object.class),
        arguments(Leaf2CDto.class, "getProp1", Object.class),
        arguments(Leaf2CDto.class, "getProp5Tristate", Tristate.class));
  }

  @ParameterizedTest
  @MethodSource("nonPresentMethodNames")
  void getMethod_when_nonPresentMethodNames_then_noSuchMethodException(
      Class<?> dtoClass, String methodName) {
    assertThrows(NoSuchMethodException.class, () -> dtoClass.getMethod(methodName));
  }

  public static Stream<Arguments> nonPresentMethodNames() {
    return Stream.of(
        arguments(Root2Dto.class, "getProp1"),
        arguments(Root2Leaf1BDto.class, "getProp4"),
        arguments(Root2Leaf2CDto.class, "getProp5"),
        arguments(Leaf1BDto.class, "getProp4"),
        arguments(Leaf2CDto.class, "getProp5"));
  }
}
