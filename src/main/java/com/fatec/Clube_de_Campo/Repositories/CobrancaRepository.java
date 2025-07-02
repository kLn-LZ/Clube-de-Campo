package com.fatec.Clube_de_Campo.Repositories;

import com.fatec.Clube_de_Campo.Entities.Cobranca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CobrancaRepository extends JpaRepository<Cobranca, Long> {
    Optional<Cobranca> findTopByAssociadoIdOrderByDataVencimentoDesc(Long associadoId);
    Optional<Cobranca> findFirstByAssociadoIdAndPagoFalseOrderByDataVencimentoAsc(Long associadoId);
    boolean existsByAssociadoIdAndMesAndAno(Long associadoId, int mes, int ano);


}
