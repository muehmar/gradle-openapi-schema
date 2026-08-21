package com.github.muehmar.openapischema.spring.rest.anyof;

import com.github.muehmar.openapischema.spring.AnyOfApiDelegate;
import openapischema.springboot2example.api.v1.model.AdminAndOrUserDto;
import org.springframework.http.ResponseEntity;

public class AnyOfDelegate implements AnyOfApiDelegate {
  private final AnyOfInterface anyOfInterface;

  public AnyOfDelegate(AnyOfInterface anyOfInterface) {
    this.anyOfInterface = anyOfInterface;
  }

  @Override
  public ResponseEntity<AdminAndOrUserDto> apiV1AnyofGet() {
    return ResponseEntity.ok(anyOfInterface.get());
  }

  @Override
  public ResponseEntity<Void> apiV1AnyofPost(AdminAndOrUserDto adminAndOrUserDto) {
    anyOfInterface.post(adminAndOrUserDto);
    return ResponseEntity.ok().build();
  }
}
