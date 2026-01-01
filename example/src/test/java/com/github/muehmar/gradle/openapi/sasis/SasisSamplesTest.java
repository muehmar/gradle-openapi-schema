package com.github.muehmar.gradle.openapi.sasis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.openapi.util.Tristate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class SasisSamplesTest {

  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  @Disabled(
      "The sasis API has currently for all object schemas additionalProperties=false which makes it impossible to send data for the allOf compositions")
  void test() throws Exception {
    final String json =
        "  {\n"
            + "            \"careProvider\": {\n"
            + "              \"careProviderParties\": [\n"
            + "                {\n"
            + "                  \"isValid\": false,\n"
            + "                  \"party\": {\n"
            + "                    \"familyName\": \"Invalid Family Name\",\n"
            + "                    \"__typename\": \"PartyNaturalPerson\"\n"
            + "                  }\n"
            + "                },\n"
            + "                {\n"
            + "                  \"isValid\": true,\n"
            + "                  \"party\": {\n"
            + "                    \"familyName\": \"Valid Family Name\",\n"
            + "                    \"__typename\": \"PartyNaturalPerson\"\n"
            + "                  }\n"
            + "                }\n"
            + "              ]\n"
            + "            }\n"
            + "          }";

    final ResponsesContractClearingNumberDto dto =
        MAPPER.readValue(json, ResponsesContractClearingNumberDto.class);

    final List<String> strings =
        dto.getCareProviderOpt()
            .map(ResponsesContractCareProviderDto::getCareProviderPartiesTristate)
            .flatMap(Tristate::toOptional)
            .map(
                list ->
                    list.stream()
                        .map(
                            providerParty ->
                                providerParty
                                    .getPartyTristate()
                                    .toOptional()
                                    .flatMap(
                                        partyParty ->
                                            partyParty
                                                .foldOneOf(
                                                    ResponsesContractPartyJuristicPersonDto
                                                        ::getGlobalLocationNumberTristate,
                                                    ResponsesContractPartyNaturalPersonDto
                                                        ::getFamilyNameTristate)
                                                .toOptional())
                                    .orElse(""))
                        .collect(Collectors.toList()))
            .orElse(Collections.emptyList());

    assertEquals(Collections.emptyList(), strings);
  }
}
