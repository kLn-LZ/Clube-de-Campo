package com.fatec.Clube_de_Campo.Repositories;

import com.fatec.Clube_de_Campo.Entities.Cobranca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PagamentoRepository extends JpaRepository<Cobranca, Long> {
    Optional<Cobranca> findLatestPagamentoByAssociadoId(Long associadoId);
}
