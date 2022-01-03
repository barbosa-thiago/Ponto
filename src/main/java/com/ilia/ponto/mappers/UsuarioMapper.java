package com.ilia.ponto.mappers;

import com.ilia.ponto.model.Usuario;
import com.ilia.ponto.requestbody.UsuarioPostBody;
import lombok.NoArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
@NoArgsConstructor
public abstract class UsuarioMapper {

  public static final UsuarioMapper INSTANCE = Mappers.getMapper(UsuarioMapper.class);

  public abstract Usuario mapUsuario(UsuarioPostBody usuarioPostBody);
}
