package com.github.muehmar.gradle.openapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import OpenApiSchema.example.api.v1.model.CarAllOfDto;
import OpenApiSchema.example.api.v1.model.CarDto;
import OpenApiSchema.example.api.v1.model.ColorDto;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class TestAllOf {
  @Test
  void getDeclaredFields_when_usingCarAllOfAndItsSubSchemas_then_carAllOfHasAllMembers() {
    final CarAllOfDto innerObject =
        CarAllOfDto.newBuilder().setModel("Ford").andAllOptionals().setIsCombi(true).build();

    final ColorDto color =
        ColorDto.newBuilder().setColorKey(0).setColorName("Yellow").andAllOptionals().build();

    final CarDto car =
        CarDto.newBuilder()
            .setColorKey(0)
            .setColorName("Yellow")
            .setModel("Ford")
            .andAllOptionals()
            .setIsCombi(true)
            .build();

    final List<Field> colorFields = Arrays.asList(color.getClass().getDeclaredFields());
    final List<Field> innerObjectFields = Arrays.asList(innerObject.getClass().getDeclaredFields());
    final List<Field> carFields = Arrays.asList(car.getClass().getDeclaredFields());

    assertEquals(colorFields.size() + innerObjectFields.size(), carFields.size());

    final Function<Field, Predicate<Field>> getFieldPredicate =
        field ->
            otherField ->
                otherField.getName().equals(field.getName())
                    && otherField.getType().equals(field.getType());

    final List<Field> missingFields =
        carFields.stream()
            .filter(
                field -> {
                  final Predicate<Field> areSameFields = getFieldPredicate.apply(field);
                  return !(colorFields.stream().anyMatch(areSameFields)
                      || innerObjectFields.stream().anyMatch(areSameFields));
                })
            .collect(Collectors.toList());

    assertEquals(0, missingFields.size());
  }
}
