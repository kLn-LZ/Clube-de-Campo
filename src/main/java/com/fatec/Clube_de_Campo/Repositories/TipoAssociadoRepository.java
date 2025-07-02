package com.fatec.Clube_de_Campo.Repositories;

import com.fatec.Clube_de_Campo.Entities.TipoAssociado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoAssociadoRepository extends JpaRepository<TipoAssociado, Long> {
    Optional<TipoAssociado> findByNome(String nome);
}
