package com.ilia.ponto.repository;

import com.ilia.ponto.model.Usuario;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

  Optional<Usuario> findByPontoLocalDateTime(LocalDateTime localDateTime);

  Optional<Usuario> findByUsername(String username);
}
