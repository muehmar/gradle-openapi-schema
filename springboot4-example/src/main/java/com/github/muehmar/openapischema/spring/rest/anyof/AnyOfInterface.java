package com.github.muehmar.openapischema.spring.rest.anyof;

import openapischema.springboot4example.api.v1.model.AdminAndOrUserDto;

public interface AnyOfInterface {
  void post(AdminAndOrUserDto dto);

  AdminAndOrUserDto get();
}
