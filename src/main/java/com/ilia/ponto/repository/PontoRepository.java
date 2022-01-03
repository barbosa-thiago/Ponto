package com.ilia.ponto.repository;

import com.ilia.ponto.model.Ponto;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface PontoRepository extends JpaRepository<Ponto, Long> {

  List<Ponto> findByUsuarioId(UUID id);

  @Query(value = "select case when (count(*) >= 4) then 'true' else 'false' end " +
      "from Ponto as p " +
      "inner join Usuario as u " +
      "on u.id = :id " +
      "and u.id = p.usuario_id " +
      "where p.localDateTime between " +
      ":startTime " +
      "and " +
      " :endTime",
      nativeQuery = true)
  boolean isLimitRecordsPerDayReached(String startTime, String endTime, UUID id);
}
