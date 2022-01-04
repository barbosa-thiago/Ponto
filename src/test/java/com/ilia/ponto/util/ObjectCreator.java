package com.ilia.ponto.util;

import com.ilia.ponto.model.Ponto;
import com.ilia.ponto.model.Usuario;
import java.time.LocalDateTime;
import java.util.UUID;

public class ObjectCreator {
  private static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.now();
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
}
