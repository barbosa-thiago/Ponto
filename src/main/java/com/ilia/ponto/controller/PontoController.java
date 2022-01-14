package com.ilia.ponto.controller;

import com.ilia.ponto.model.DateInput;
import com.ilia.ponto.model.Ponto;
import com.ilia.ponto.service.PontoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.UUID;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("schedules")
@RequiredArgsConstructor
public class PontoController {

  private final PontoService pontoService;

  @PostMapping
  @Operation(summary = "Registra ponto ", description = "formato data: yyyy-MM-ddTHH:mm:ss")
  @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "created successfully"),
      @ApiResponse(responseCode = "400", description = "data invalida. Checar mensagem de erro")})
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<Ponto> save(@RequestBody @Valid DateInput dateTime) {

    Ponto ponto = pontoService.save(dateTime.getDateTime());
    return new ResponseEntity<>(ponto, HttpStatus.CREATED);
  }

  @GetMapping("/listForUser")
  @Operation(summary = "Lista Pontos do Usuario nos ultimos 30 dias")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "successful request"),
      @ApiResponse(responseCode = "401", description = "unauthorized")})
  public ResponseEntity<Page<Ponto>>
  findPontosPerUserLast30days(@ParameterObject @PageableDefault Pageable pageable, @RequestParam UUID userId) {
    return new ResponseEntity<>(pontoService.findPontosPerUserLast30days(pageable, userId), HttpStatus.OK);
  }
}
