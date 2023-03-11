package com.github.muehmar.openapischema.spring.rest.oneof;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import OpenApiSchema.springexample.api.v1.model.AdminDto;
import OpenApiSchema.springexample.api.v1.model.AdminOrUserDiscriminatorDto;
import com.github.muehmar.openapischema.spring.rest.ControllerUnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest
public class TestOneOfController extends ControllerUnitTest {
  public static final String API_V1_ONEOF = "/api/v1/oneof";
  @Autowired private OneOfInterface oneOfInterface;

  @Autowired private MockMvc mockMvc;

  @Test
  void get_when_called_then_correctSerializedDtoReturned() throws Exception {
    final AdminDto admin =
        AdminDto.newBuilder()
            .setId("admin-id")
            .setAdminname("admin-name")
            .andAllOptionals()
            .setType("type")
            .setLevel(5L)
            .build();

    final AdminOrUserDiscriminatorDto adminOrUserDto = AdminOrUserDiscriminatorDto.fromAdmin(admin);
    when(oneOfInterface.get()).thenReturn(adminOrUserDto);

    final MvcResult mvcResult =
        mockMvc.perform(get(API_V1_ONEOF)).andExpect(status().isOk()).andReturn();

    final String contentAsString = mvcResult.getResponse().getContentAsString();

    assertEquals(
        "{\"id\":\"admin-id\",\"type\":\"Admin\",\"adminname\":\"admin-name\",\"level\":5}",
        contentAsString);
  }

  @Test
  void post_when_validJson_then_okAndInterfaceCalledWithDto() throws Exception {
    mockMvc
        .perform(
            post(API_V1_ONEOF)
                .content(
                    "{\"id\":\"admin-id\",\"type\":\"Admin\",\"adminname\":\"admin-name\",\"level\":5}")
                .header(HttpHeaders.CONTENT_TYPE, "application/json"))
        .andExpect(status().isOk());

    final AdminDto admin =
        AdminDto.newBuilder()
            .setId("admin-id")
            .setAdminname("admin-name")
            .andAllOptionals()
            .setType("type")
            .setLevel(5L)
            .build();

    final AdminOrUserDiscriminatorDto adminOrUserDto = AdminOrUserDiscriminatorDto.fromAdmin(admin);

    verify(oneOfInterface).post(adminOrUserDto);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "{\"id\":\"admin-id\",\"type\":\"User\",\"adminname\":\"admin-name\",\"level\":5}",
        "{\"type\":\"Admin\",\"adminname\":\"admin-name\",\"level\":5}",
        "{\"id\":\"admin-id\",\"type\":\"Admin\",\"adminname\":\"admin-name\",\"level\":0}"
      })
  void post_when_invalidJson_then_badRequest(String json) throws Exception {
    mockMvc
        .perform(
            post(API_V1_ONEOF).content(json).header(HttpHeaders.CONTENT_TYPE, "application/json"))
        .andExpect(status().isBadRequest());

    verify(oneOfInterface, never()).post(any());
  }
}
