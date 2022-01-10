package com.ilia.ponto.util;

import com.ilia.ponto.model.Ponto;
import com.ilia.ponto.model.Usuario;
import com.ilia.ponto.requestbody.UsuarioPostBody;
import java.time.LocalDateTime;
import java.util.UUID;

public class ObjectCreator {
  private static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(2022, 1, 3, 1, 0, 0);
  private static final Usuario USUARIO = new Usuario(UUID.randomUUID(), "john", "john", null);
  private static Ponto ponto = new Ponto(1L, LOCAL_DATE_TIME, USUARIO);

  public static Ponto createValidPonto() {
    return ponto;
  }

  public static Ponto createPontoToBeSaved() {
    ponto.setId(null);
    return ponto;
  }

  public static Usuario createValidUsuario() {
    return USUARIO;
  }

  public static UsuarioPostBody createUsuarioPostBody() {
    return new UsuarioPostBody("secret", USUARIO.getUsername(), USUARIO.getName());
  }

  public static UsuarioPostBody createInvalidUsuarioPostBody() {
    return new UsuarioPostBody("secret", null, USUARIO.getName());
  }
}
