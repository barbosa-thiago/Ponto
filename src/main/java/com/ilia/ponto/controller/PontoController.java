package com.ilia.ponto.controller;

import com.ilia.ponto.model.Ponto;
import com.ilia.ponto.service.PontoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("ponto")
@RequiredArgsConstructor
public class PontoController {

  private final PontoService pontoService;
  @GetMapping
  public ResponseEntity<String> ponto(@RequestParam String date) {
    return new ResponseEntity<>("ponto registrado " + date, HttpStatus.OK);
  }
  @PostMapping
  public ResponseEntity<Ponto> save(@RequestParam String date) {

    Ponto ponto = pontoService.save(date);
    return new ResponseEntity<>(ponto, HttpStatus.CREATED);
  }

}
