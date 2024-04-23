package com.github.muehmar.openapischema.spring.rest.oneof;

import javax.validation.Valid;
import openapischema.spring2example.api.v1.model.AdminOrUserDiscriminatorDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class OneOfController {

  private final OneOfInterface oneOfInterface;

  public OneOfController(OneOfInterface oneOfInterface) {
    this.oneOfInterface = oneOfInterface;
  }

  @PostMapping("/oneof")
  public String post(@Valid @RequestBody AdminOrUserDiscriminatorDto dto) {
    oneOfInterface.post(dto);
    return "OK";
  }

  @GetMapping("/oneof")
  public AdminOrUserDiscriminatorDto get() {
    return oneOfInterface.get();
  }
}
