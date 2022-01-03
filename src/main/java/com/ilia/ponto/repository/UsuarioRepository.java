package com.ilia.ponto.repository;

import com.ilia.ponto.model.Usuario;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

  Optional<Usuario> findByIdAndPontoLocalDateTime(UUID id, LocalDateTime localDateTime);

  Optional<Usuario> findByUsername(String username);
}
