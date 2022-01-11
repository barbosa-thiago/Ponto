package com.ilia.ponto.controller;

import com.ilia.ponto.model.DateInput;
import com.ilia.ponto.model.Ponto;
import com.ilia.ponto.service.PontoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("schedules")
@RequiredArgsConstructor
public class PontoController {

  private final PontoService pontoService;

  @PostMapping
  @Operation(summary = "Registra ponto ", description = "formato data: yyyy-MM-ddTHH:mm:ss")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<Ponto> save(@RequestBody DateInput dateInput) {

    Ponto ponto = pontoService.save(dateInput.getDataHora());
    return new ResponseEntity<>(ponto, HttpStatus.CREATED);
  }
}
