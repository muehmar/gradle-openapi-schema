package com.github.muehmar.gradle.openapi.generator.mapper.resolver;

import static com.github.muehmar.gradle.openapi.generator.model.AdditionalProperties.anyTypeAllowed;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.REQUIRED;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PropertyScope;
import com.github.muehmar.gradle.openapi.generator.model.UnresolvedComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.UnresolvedComposedPojoBuilder;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojoBuilder;
import com.github.muehmar.gradle.openapi.generator.model.type.IntegerType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import java.util.Comparator;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ComposedPojoResolverTest {

  @Test
  void resolve_when_twoPojosAndOneAllOfComposedPojos_then_composedPojoCreated() {

    final PojoName tiresName = PojoName.ofNameAndSuffix(Name.ofString("Tires"), "Dto");
    final PojoName colorName = PojoName.ofNameAndSuffix(Name.ofString("Color"), "Dto");

    final ObjectPojo tiresPojo =
        ObjectPojoBuilder.create()
            .name(tiresName)
            .description("Tires")
            .members(
                PList.of(
                    new PojoMember(
                        Name.ofString("tireKey"),
                        "Key",
                        StringType.uuid(),
                        PropertyScope.DEFAULT,
                        REQUIRED,
                        NOT_NULLABLE),
                    new PojoMember(
                        Name.ofString("tireName"),
                        "Name",
                        StringType.noFormat(),
                        PropertyScope.DEFAULT,
                        REQUIRED,
                        NOT_NULLABLE)))
            .constraints(Constraints.empty())
            .additionalProperties(anyTypeAllowed())
            .build();

    final ObjectPojo colorPojo =
        ObjectPojoBuilder.create()
            .name(colorName)
            .description("Colors")
            .members(
                PList.of(
                    new PojoMember(
                        Name.ofString("colorKey"),
                        "Key",
                        IntegerType.formatLong(),
                        PropertyScope.DEFAULT,
                        OPTIONAL,
                        NOT_NULLABLE),
                    new PojoMember(
                        Name.ofString("colorName"),
                        "Name",
                        StringType.noFormat(),
                        PropertyScope.DEFAULT,
                        OPTIONAL,
                        NOT_NULLABLE)))
            .constraints(Constraints.empty())
            .additionalProperties(anyTypeAllowed())
            .build();

    final UnresolvedComposedPojo unresolvedComposedPojo =
        UnresolvedComposedPojoBuilder.create()
            .name(PojoName.ofNameAndSuffix(Name.ofString("Composed"), "Dto"))
            .description("Description")
            .type(UnresolvedComposedPojo.CompositionType.ALL_OF)
            .pojoNames(PList.of(colorName, tiresName))
            .constraints(Constraints.empty())
            .andAllOptionals()
            .discriminator(Optional.empty())
            .build();

    // method call
    final PList<Pojo> resultingPojos =
        UnresolvedComposedPojoResolver.resolve(
                PList.single(unresolvedComposedPojo), PList.of(colorPojo, tiresPojo))
            .sort(Comparator.comparing(pojo -> pojo.getName().asString()));

    assertEquals(3, resultingPojos.size());

    assertEquals(colorPojo, resultingPojos.apply(0));
    assertEquals(tiresPojo, resultingPojos.apply(2));

    assertEquals(
        ObjectPojoBuilder.create()
            .name(unresolvedComposedPojo.getName())
            .description(unresolvedComposedPojo.getDescription())
            .members(colorPojo.getMembers().concat(tiresPojo.getMembers()))
            .constraints(Constraints.empty())
            .additionalProperties(anyTypeAllowed())
            .build(),
        resultingPojos.apply(1));
  }
}
