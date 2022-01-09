package com.ilia.ponto.controller;

import com.ilia.ponto.model.DateInput;
import com.ilia.ponto.model.Ponto;
import com.ilia.ponto.service.PontoService;
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
  public ResponseEntity<Ponto> save(@RequestBody DateInput dateInput) {

    Ponto ponto = pontoService.save(dateInput.getDataHora());
    return new ResponseEntity<>(ponto, HttpStatus.CREATED);
  }
}
