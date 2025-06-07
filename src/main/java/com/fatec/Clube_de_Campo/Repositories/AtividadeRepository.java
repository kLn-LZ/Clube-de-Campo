package com.fatec.Clube_de_Campo.Repositories;

import com.fatec.Clube_de_Campo.Entities.Atividade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AtividadeRepository extends JpaRepository<Atividade, Long> {
    Optional<Atividade> findByNome(String nome);
}
