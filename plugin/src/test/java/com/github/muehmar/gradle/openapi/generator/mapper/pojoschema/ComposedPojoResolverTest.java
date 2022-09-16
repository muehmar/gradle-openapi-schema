package com.github.muehmar.gradle.openapi.generator.mapper.pojoschema;

import static com.github.muehmar.gradle.openapi.generator.model.Necessity.OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.REQUIRED;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import java.util.Comparator;
import org.junit.jupiter.api.Test;

class ComposedPojoResolverTest {

  @Test
  void resolve_when_twoPojosAndOneAllOfComposedPojos_then_composedPojoCreated() {

    final PojoName tiresName = PojoName.ofNameAndSuffix(Name.ofString("Tires"), "Dto");
    final PojoName colorName = PojoName.ofNameAndSuffix(Name.ofString("Color"), "Dto");

    final ObjectPojo tiresPojo =
        ObjectPojo.of(
            tiresName,
            "Tires",
            PList.of(
                new PojoMember(
                    Name.ofString("tireKey"), "Key", StringType.uuid(), REQUIRED, NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("tireName"),
                    "Name",
                    StringType.noFormat(),
                    REQUIRED,
                    NOT_NULLABLE)));

    final ObjectPojo colorPojo =
        ObjectPojo.of(
            colorName,
            "Colors",
            PList.of(
                new PojoMember(
                    Name.ofString("colorKey"),
                    "Key",
                    NumericType.formatLong(),
                    OPTIONAL,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("colorName"),
                    "Name",
                    StringType.noFormat(),
                    OPTIONAL,
                    NOT_NULLABLE)));

    final ComposedPojo composedPojo =
        new ComposedPojo(
            PojoName.ofNameAndSuffix(Name.ofString("Composed"), "Dto"),
            "Description",
            ComposedPojo.CompositionType.ALL_OF,
            PList.of(colorName, tiresName),
            PList.empty());

    // method call
    final PList<Pojo> resultingPojos =
        ComposedPojoResolver.resolve(PList.single(composedPojo), PList.of(colorPojo, tiresPojo))
            .sort(Comparator.comparing(pojo -> pojo.getName().asString()));

    assertEquals(3, resultingPojos.size());

    assertEquals(colorPojo, resultingPojos.apply(0));
    assertEquals(tiresPojo, resultingPojos.apply(2));

    assertEquals(
        ObjectPojo.of(
            composedPojo.getName(),
            composedPojo.getDescription(),
            colorPojo.getMembers().concat(tiresPojo.getMembers())),
        resultingPojos.apply(1));
  }
}
