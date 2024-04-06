package com.github.muehmar.openapischema.spring.rest.anyof;

import javax.validation.Valid;
import openapischema.spring2example.api.v1.model.AdminAndOrUserDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class AnyOfController {

  private final AnyOfInterface anyOfInterface;

  public AnyOfController(AnyOfInterface anyOfInterface) {
    this.anyOfInterface = anyOfInterface;
  }

  @PostMapping("/anyof")
  public String post(@Valid @RequestBody AdminAndOrUserDto dto) {
    anyOfInterface.post(dto);
    return "OK";
  }

  @GetMapping("/anyof")
  public AdminAndOrUserDto get() {
    return anyOfInterface.get();
  }
}
