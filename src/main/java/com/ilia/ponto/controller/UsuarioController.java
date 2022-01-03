package com.ilia.ponto.controller;

import com.ilia.ponto.model.Usuario;
import com.ilia.ponto.requestbody.UsuarioPostBody;
import com.ilia.ponto.service.UsuarioService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<Usuario> save(@RequestBody @Valid UsuarioPostBody usuarioPostBody) {
    return new ResponseEntity<>(usuarioService.save(usuarioPostBody), HttpStatus.CREATED);
  }
}
