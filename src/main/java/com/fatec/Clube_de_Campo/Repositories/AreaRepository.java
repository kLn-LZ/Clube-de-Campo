package com.fatec.Clube_de_Campo.Repositories;

import com.fatec.Clube_de_Campo.Entities.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {
    List<Area> findByTipoAreaId(Long tipoAreaId);
    Optional<Area> findByNomeAndTipoAreaId(String nome, Long tipoAreaId);
}
