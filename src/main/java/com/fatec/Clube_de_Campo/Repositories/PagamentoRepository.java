package com.fatec.Clube_de_Campo.Repositories;

import aj.org.objectweb.asm.commons.Remapper;
import com.fatec.Clube_de_Campo.Entities.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    Optional<Pagamento> findLatestPagamentoByAssociadoId(Long associadoId);
}
