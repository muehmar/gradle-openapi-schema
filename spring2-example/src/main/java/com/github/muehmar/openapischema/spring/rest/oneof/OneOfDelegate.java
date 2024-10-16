package com.github.muehmar.openapischema.spring.rest.oneof;

import com.github.muehmar.openapischema.spring.OneOfApiDelegate;
import openapischema.spring2example.api.v1.model.AdminOrUserDiscriminatorDto;
import org.springframework.http.ResponseEntity;

public class OneOfDelegate implements OneOfApiDelegate {
  private final OneOfInterface oneOfInterface;

  public OneOfDelegate(OneOfInterface oneOfInterface) {
    this.oneOfInterface = oneOfInterface;
  }

  @Override
  public ResponseEntity<AdminOrUserDiscriminatorDto> apiV1OneofGet() {
    return ResponseEntity.ok(oneOfInterface.get());
  }

  @Override
  public ResponseEntity<Void> apiV1OneofPost(
      AdminOrUserDiscriminatorDto adminOrUserDiscriminatorDto) {
    oneOfInterface.post(adminOrUserDiscriminatorDto);
    return ResponseEntity.ok().build();
  }
}
