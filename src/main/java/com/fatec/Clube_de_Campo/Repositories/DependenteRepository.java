package com.fatec.Clube_de_Campo.Repositories;

import com.fatec.Clube_de_Campo.Entities.Dependente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DependenteRepository extends JpaRepository<Dependente, Long> {
}
