package com.github.muehmar.openapischema.spring.rest.anyof;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.muehmar.openapi.util.Tristate;
import com.github.muehmar.openapischema.spring.rest.ControllerUnitTest;
import openapischema.springexample.api.v1.model.AdminAndOrUserDto;
import openapischema.springexample.api.v1.model.AdminDto;
import openapischema.springexample.api.v1.model.UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest
public class TestAnyOfController extends ControllerUnitTest {
  public static final String API_V1_ANYOF = "/api/v1/anyof";
  @Autowired private AnyOfInterface anyOfInterface;

  @Autowired private MockMvc mockMvc;

  @Test
  void get_when_called_then_correctSerializedDtoReturned() throws Exception {
    final AdminDto admin =
        AdminDto.newBuilder()
            .setId("admin-id")
            .setType("admin")
            .setAdminname("admin-name")
            .andAllOptionals()
            .setLevel(5L)
            .build();

    final AdminAndOrUserDto adminAndOrUserDto =
        AdminAndOrUserDto.newBuilder().setAdminDto(admin).build();
    when(anyOfInterface.get()).thenReturn(adminAndOrUserDto);

    final MvcResult mvcResult =
        mockMvc.perform(get(API_V1_ANYOF)).andExpect(status().isOk()).andReturn();

    final String contentAsString = mvcResult.getResponse().getContentAsString();

    assertEquals(
        "{\"id\":\"admin-id\",\"type\":\"admin\",\"adminname\":\"admin-name\",\"level\":5}",
        contentAsString);
  }

  @Test
  void post_when_validJson_then_okAndInterfaceCalledWithDto() throws Exception {
    mockMvc
        .perform(
            post(API_V1_ANYOF)
                .content(
                    "{\"id\":\"id-123\",\"type\":\"admin-and-user\",\"adminname\":\"admin-name\","
                        + "\"username\":\"user-name\",\"level\":5,\"age\":39,\"email\":null}")
                .header(HttpHeaders.CONTENT_TYPE, "application/json"))
        .andExpect(status().isOk());

    final AdminDto admin =
        AdminDto.newBuilder()
            .setId("id-123")
            .setType("admin-and-user")
            .setAdminname("admin-name")
            .andAllOptionals()
            .setLevel(5L)
            .build();

    final UserDto user =
        UserDto.newBuilder()
            .setId("id-123")
            .setType("admin-and-user")
            .setUsername("user-name")
            .andAllOptionals()
            .setAge(39)
            .setEmail(Tristate.ofNull())
            .build();

    final AdminAndOrUserDto adminAndOrUserDto =
        AdminAndOrUserDto.newBuilder().setAdminDto(admin).setUserDto(user).build();

    verify(anyOfInterface).post(adminAndOrUserDto);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "{}",
        "{\"type\":\"Admin\",\"adminname\":\"admin-name\",\"level\":5}",
        "{\"id\":\"admin-id\",\"type\":\"Admin\",\"adminname\":\"admin-name\",\"level\":0}"
      })
  void post_when_invalidJson_then_badRequest(String json) throws Exception {
    mockMvc
        .perform(
            post(API_V1_ANYOF).content(json).header(HttpHeaders.CONTENT_TYPE, "application/json"))
        .andExpect(status().isBadRequest());

    verify(anyOfInterface, never()).post(any());
  }
}
