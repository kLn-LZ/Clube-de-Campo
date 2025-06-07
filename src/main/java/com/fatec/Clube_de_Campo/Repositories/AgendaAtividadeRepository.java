package com.fatec.Clube_de_Campo.Repositories;

import com.fatec.Clube_de_Campo.Entities.AgendaAtividade;
import com.fatec.Clube_de_Campo.Entities.Atividade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;

@Repository
public interface AgendaAtividadeRepository extends JpaRepository<AgendaAtividade, Long> {

    Collection<AgendaAtividade> findByAtividadeAndData(Atividade atividade, LocalDate data);

    long countByAtividadeAndData(Atividade atividade, LocalDate data);
}
