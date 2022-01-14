package com.ilia.ponto.controller;

import com.ilia.ponto.model.Usuario;
import com.ilia.ponto.projection.UsuarioUsernameId;
import com.ilia.ponto.requestbody.UsuarioPostBody;
import com.ilia.ponto.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("usuario")
public class UsuarioController {
  private final UsuarioService usuarioService;

  @PostMapping
  @Operation(summary = "Cria Usuário")
  @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "usuario criado"),
      @ApiResponse(responseCode = "400", description = "checar mensagem de erro")})
  public ResponseEntity<Usuario> save(@RequestBody @Valid UsuarioPostBody usuarioPostBody) {
    return new ResponseEntity<>(usuarioService.save(usuarioPostBody), HttpStatus.CREATED);
  }

  @GetMapping
  @Operation(summary = "Lista Usuarios(username e id")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "usuarios listados")})
  public ResponseEntity<Page<UsuarioUsernameId>> findAll(@PageableDefault @ParameterObject Pageable pageable) {
    return new ResponseEntity<>(usuarioService.findAll(pageable), HttpStatus.OK);
  }
}
