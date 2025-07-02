package com.fatec.Clube_de_Campo.Repositories;

import com.fatec.Clube_de_Campo.Entities.AgendaAtividade;
import com.fatec.Clube_de_Campo.Entities.Atividade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AgendaAtividadeRepository extends JpaRepository<AgendaAtividade, Long> {
    List<AgendaAtividade> findByAtividadeAndData(Atividade atividade, LocalDate data);
    Optional<AgendaAtividade> findByAtividadeAndDataAndTime(Atividade atividade, LocalDate data, String time);
    long countByAtividadeAndData(Atividade atividade, LocalDate data);

    @Query("SELECT COUNT(a) FROM AgendaAtividade a WHERE a.atividade = :atividade AND a.data = :data AND a.id != :id")
    long countByAtividadeAndDataExcludingId(@Param("atividade") Atividade atividade, @Param("data") LocalDate data, @Param("id") Long id);

    boolean existsByAtividadeId(Long atividadeId);
}
