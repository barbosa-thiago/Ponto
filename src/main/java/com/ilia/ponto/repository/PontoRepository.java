package com.ilia.ponto.repository;

import com.ilia.ponto.model.Ponto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface PontoRepository extends JpaRepository<Ponto, Long> {


  @Query(value = "select p " +
      "from Ponto as p " +
      "inner join Usuario as u " +
      "on u.id = :id " +
      "and u = p.usuario " +
      "where p.localDateTime > " +
      ":#{#localDateTime.withHour(0).withMinute(0).withSecond(0)} " +
      "and " +
      "p.localDateTime < :#{#localDateTime.withHour(23).withMinute(59).withSecond(59)}")
  List<Ponto> findUserSchedulesPerDay(@Param("localDateTime") LocalDateTime localDateTime, @Param("id") UUID id);


  @Query(value = "select distinct p from Ponto as p " +
      "join Usuario as u " +
      "on p.usuario.id = :id " +
      "where p.localDateTime > :#{#dateTime.minusMonths(1).withHour(0)}")
  Page<Ponto> findPontosPerUserLast30days(Pageable pageable,
                                          @Param("id") UUID id,
                                          @Param("dateTime") LocalDateTime dateTime);

}
