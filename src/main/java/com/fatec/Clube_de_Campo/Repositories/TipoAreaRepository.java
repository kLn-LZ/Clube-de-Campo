package com.fatec.Clube_de_Campo.Repositories;

import com.fatec.Clube_de_Campo.Entities.TipoArea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoAreaRepository extends JpaRepository<TipoArea, Long> {
    Optional<TipoArea> findByNome(String nome);
}
